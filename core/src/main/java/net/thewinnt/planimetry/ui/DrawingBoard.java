package net.thewinnt.planimetry.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Predicate;

import net.thewinnt.gdxutils.FontUtils;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.Shape.SelectionStatus;
import net.thewinnt.planimetry.shapes.factories.CircleFactory;
import net.thewinnt.planimetry.shapes.factories.LineFactory;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.factories.ShapeFactory;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class DrawingBoard extends Actor {
    private final ShapeDrawer drawer;
    private final FontProvider font;
    private final List<Shape> shapes = new ArrayList<>();
    private final List<Consumer<Shape>> selectionListeners = new ArrayList<>();
    private double scale = Math.pow(1.25, 15); // pixels per unit
    private Vec2 offset = Vec2.ZERO;
    private Shape selection;
    private ShapeFactory creatingShape;
    private boolean isPanning = false;
    private boolean startedAtPoint = false;

    public DrawingBoard(ShapeDrawer drawer, FontProvider font) {
        this.drawer = drawer;
        this.font = font;
        this.addListener(new ActorGestureListener(DynamicPlanimetry.IS_MOBILE ? 20 : 2, 0.4f, 1.1f, Integer.MAX_VALUE) {
            @Override
            public void zoom(InputEvent event, float initialDistance, float distance) {
                scale /= (distance / initialDistance);
                event.handle();
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                isPanning = true;
                if (creatingShape == null && selection != null && startedAtPoint && selection instanceof PointProvider point && point.canMove()) {
                    point.move(deltaX / scale, deltaY / scale);
                    for (Consumer<Shape> i : selectionListeners) {
                        i.accept(selection);
                    }
                } else {
                    offset = offset.add(-deltaX, -deltaY);
                }
                event.handle();
            }

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                int mx = Gdx.input.getX();
                int my = Gdx.input.getY();
                if (creatingShape == null) {
                    setSelection(getHoveredShape(mx, my));
                } else if (creatingShape.isDone() || creatingShape.click(event, xb(mx), yb(my))) {
                    creatingShape = null;
                }
                event.handle();
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isPanning = false;
            }

            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                int mx = Gdx.input.getX();
                int my = Gdx.input.getY();
                if (!isPanning && selection != null) {
                    startedAtPoint = selection.distanceToMouse(xb(mx), yb(my), DrawingBoard.this) <= 16 / scale;
                }
            }
        });
        this.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                scale /= Math.pow(1.25, amountY);
                return true;
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                System.out.println(keycode);
                if (creatingShape == null && (selection == null || selection instanceof PointProvider point)) {
                    switch (keycode) {
                        case Keys.L:
                            creatingShape = new LineFactory(DrawingBoard.this, LineType.INFINITE, (PointProvider)selection);
                            selection = null;
                            break;
                        case Keys.R:
                            creatingShape = new LineFactory(DrawingBoard.this, LineType.RAY, (PointProvider)selection);
                            selection = null;
                            break;
                        case Keys.S:
                            creatingShape = new LineFactory(DrawingBoard.this, LineType.SEGMENT, (PointProvider)selection);
                            selection = null;
                            break;
                        case Keys.C:
                            creatingShape = new CircleFactory(DrawingBoard.this).setAddRadius(true);
                            selection = null;
                        default:
                            break;
                    }
                }
                return true;
            }
        });
    }

    /** local space -> global (render) space */
    private float x(float x) {
        return x + getX();
    }

    /** local space -> global (render) space */
    private float y(float y) {
        return y + getY();
    }

    /** global space -> board space */
    public double xb(double x) {
        return ((x - getWidth() / 2f + offset.x) / scale);
    }

    /** global space -> board space */
    public double yb(double y) {
        return ((y - getHeight() / 2f - offset.y) / -scale);
    }

    /** board space -> global (render) space */
    public float bx(double x) {
        x *= scale;
        return x((float)(getWidth() / 2f + x - offset.x));
    }

    /** board space -> global (render) space */
    public float by(double y) {
        y *= scale;
        return y((float)(getHeight() / 2f + y - offset.y));
    }

    public Vec2 boardToGlobal(Vec2 point) {
        return new Vec2(bx(point.x), by(point.y));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (scale == 0) scale = 1;
        if (Double.isInfinite(scale)) scale = 1;
        int mx = Gdx.input.getX();
        int my = Gdx.input.getY();
        double step = Math.abs(Math.max(getHeight(), getWidth()) / scale / 12); // detect full width
        int j = 0;
        if (step != 0 && Double.isFinite(step)) {
            // scale to [1..10]
            if (step > 10) {
                while (step > 10) {
                    step /= 10;
                    j++;
                }
            } else if (step < 1) {
                while (step < 1) {
                    step *= 10;
                    j--;
                }
            }
            // create step
            if (step < 1.5) {
                step = 1;
            } else if (step < 4) {
                step = 2;
            } else if (step < 8) {
                step = 5;
            } else {
                step = 10;
            }
            // scale back
            step *= Math.pow(10, j);
            float hintX = by(0) - 5;
            if (hintX > getY() + getHeight() - 20) {
                hintX = getY() + getHeight() - 20;
            } else if (hintX < getY() + 30) {
                hintX = getY() + 30;
            }
            for (double i = xb(getX()) - xb(getX()) % step; i < xb(getX() + getWidth()); i += step) {
                drawer.line(bx(i), getY(), bx(i), getY() + getHeight(), DynamicPlanimetry.COLOR_GRID, 1);
                if (i == 0) continue;
                if (Math.abs(i) % 1 == 0) {
                    font.getFont(40, DynamicPlanimetry.COLOR_GRID_HINT).draw(batch, String.valueOf((long)i), bx(i), hintX, 0, Align.center, false);
                } else {
                    String string = String.format("%.8f", i);
                    int k = string.length() - 1;
                    while (string.charAt(k) == '0') k--;
                    string = string.substring(0, k + 1);
                    if (string.endsWith(",")) string = string.substring(0, string.length() - 1);
                    font.getFont(40, DynamicPlanimetry.COLOR_GRID_HINT).draw(batch, string, bx(i), hintX, 0, Align.center, false);
                }
            }
            for (double i = yb(getY() + getHeight()) - yb(getY() + getHeight()) % step; i < yb(getY()); i += step) {
                drawer.line(getX(), by(i), getX() + getWidth(), by(i), DynamicPlanimetry.COLOR_GRID, 1);
                if (Math.abs(i) < Math.pow(2, -16)) continue;
                if (Math.abs(i) % 1 == 0) {
                    float length = FontUtils.getTextLength(font.getFont(40, Color.BLACK), String.valueOf((long)i));
                    float hintY = bx(0) + 5;
                    int alignYH = Align.left;
                    if (hintY >= getX() + getWidth() - length - 10) {
                        hintY = getY() + getWidth() - 10;
                        alignYH = Align.right;
                    } else if (hintY <= getX() + 10) {
                        hintY = getX() + 10;
                        alignYH = Align.left;
                    }
                    font.getFont(40, DynamicPlanimetry.COLOR_GRID_HINT).draw(batch, String.valueOf((long)i), hintY, by(i) + 10, 0, alignYH, false);
                } else {
                    String string = String.format("%.8f", i);
                    int k = string.length() - 1;
                    while (string.charAt(k) == '0') k--;
                    string = string.substring(0, k + 1);
                    if (string.endsWith(",")) string = string.substring(0, string.length() - 1);
                    float length = FontUtils.getTextLength(font.getFont(40, Color.BLACK), string);
                    float hintY = bx(0) + 5;
                    int alignYH = Align.left;
                    if (hintY >= getX() + getWidth() - length - 10) {
                        hintY = getY() + getWidth() - 10;
                        alignYH = Align.right;
                    } else if (hintY <= getX() + length + 10) {
                        hintY = getX() + 10;
                        alignYH = Align.left;
                    }
                    font.getFont(40, DynamicPlanimetry.COLOR_GRID_HINT).draw(batch, string, hintY, by(i), 0, alignYH, false);
                }
            }
        }
        drawer.setColor(0, 0, 0, 1);
        drawer.line(getX(), by(0), getX() + getWidth(), by(0), 2);
        drawer.line(bx(0), getY(), bx(0), getY() + getHeight(), 2);
        Shape hovered = getHoveredShape(mx, my);
        for (Shape i : shapes) {
            if (selection == i) {
                i.render(drawer, SelectionStatus.SELECTED, font, this);
            } else {
                i.render(drawer, hovered == i ? SelectionStatus.HOVERED : SelectionStatus.NONE, font, this);
            }
        }
        if (DynamicPlanimetry.DEBUG_MODE) {
            font.getFont(40, Color.FIREBRICK).draw(batch, "scale: " + scale, x(5), y(getHeight() - 5));
            font.getFont(40, Color.FIREBRICK).draw(batch, "mx: " + mx, x(5), y(getHeight() - 130));
            font.getFont(40, Color.FIREBRICK).draw(batch, "my: " + my, x(5), y(getHeight() - 155));
            font.getFont(40, Color.FIREBRICK).draw(batch, "mxb: " + xb(mx), x(5), y(getHeight() - 180));
            font.getFont(40, Color.FIREBRICK).draw(batch, "mxy: " + yb(my), x(5), y(getHeight() - 205));
            if (selection != null) {
                font.getFont(40, Color.FIREBRICK).draw(batch, "Selected: " + selection, x(5), y(105));
            }
            if (creatingShape != null) {
                font.getFont(40, Color.FIREBRICK).draw(batch, "Creating: " + creatingShape, x(5), y(130));
            }
        }
    }

    /** The scale, in pixels per unit */
    public double getScale() {
        return scale;
    }

    public Vec2 getOffset() {
        return offset;
    }

    public double minX() {
        return xb(getX());
    }

    public double maxX() {
        return xb(getX() + getWidth());
    }

    public double minY() {
        return yb(getY());
    }

    public double maxY() {
        return yb(getY() + getHeight());
    }

    public Shape getSelection() {
        return selection;
    }

    public Collection<Shape> getShapes() {
        return Collections.unmodifiableList(shapes);
    }

    public Shape getHoveredShape(double mx, double my) {
        Shape hovered = null;
        double minDistance = 8 / scale;
        Collection<Shape> ignore;
        if (creatingShape != null) {
            ignore = creatingShape.getSuggestedShapes();
        } else {
            ignore = List.of();
        }
        for (Shape i : shapes) {
            if (ignore.contains(i)) continue;
            double distance = i.distanceToMouse(xb(mx), yb(my), this);
            if (distance <= minDistance) {
                hovered = i;
                minDistance = distance;
            }
        }
        return hovered;
    }

    public Shape getHoveredShape(double mx, double my, Predicate<Shape> predicate) {
        Shape hovered = null;
        double minDistance = 8 / scale;
        Collection<Shape> ignore;
        if (creatingShape != null) {
            ignore = creatingShape.getSuggestedShapes();
        } else {
            ignore = List.of();
        }
        for (Shape i : shapes) {
            if (ignore.contains(i)) continue;
            if (!predicate.evaluate(i)) continue;
            double distance = i.distanceToMouse(xb(mx), yb(my), this);
            if (distance <= minDistance) {
                hovered = i;
                minDistance = distance;
            }
        }
        return hovered;
    }

    public PointProvider getNearestPoint(double mx, double my) {
        PointProvider hovered = null;
        double minDistance = Double.MAX_VALUE;
        for (Shape i : shapes) {
            if (i instanceof PointProvider point) {
                double distance = point.distanceToMouse(xb(mx), yb(my), this);
                if (distance <= minDistance) {
                    hovered = point;
                    minDistance = distance;
                }
            }
        }
        return hovered;
    }

    public void addShape(Shape shape) {
        if (!this.shapes.contains(shape)) {
            this.shapes.add(shape);
        }
    }

    public boolean hasShape(Shape shape) {
        for (Shape i : this.shapes) {
            if (i instanceof PointReference point && point.getPoint() == shape) {
                return true;
            } else if (i == shape) {
                return true;
            }
        }
        return false;
    }

    public void setSelection(Shape shape) {
        if (this.shapes.contains(shape)) {
            selection = shape;
        } else {
            selection = null;
        }
        for (Consumer<Shape> i : this.selectionListeners) {
            i.accept(selection);
        }
    }

    public void startCreation(ShapeFactory factory) {
        this.creatingShape = factory;
        this.selection = null;
    }

    public void addSelectionListener(Consumer<Shape> listener) {
        this.selectionListeners.add(listener);
    }
}

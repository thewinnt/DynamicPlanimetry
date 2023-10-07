package net.thewinnt.planimetry.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.Shape.SelectionStatus;
import net.thewinnt.planimetry.shapes.lines.InfiniteLine;
import net.thewinnt.planimetry.shapes.point.Point;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class DrawingBoard extends Actor {
    private final ShapeDrawer drawer;
    private final FontProvider font;
    private final List<Shape> shapes = new ArrayList<>();
    private double scale = Math.pow(1.5, 10); // pixels per unit
    private Vec2 offset = Vec2.ZERO;
    private Shape selection;
    private boolean isPanning = false;
    private boolean startedAtPoint = false;
    private String pan1 = "";
    private String pan2 = "";
    private String pan3 = "";
    private String pan4 = "";

    public DrawingBoard(ShapeDrawer drawer, FontProvider font) {
        this.drawer = drawer;
        this.font = font;
        this.addListener(new ActorGestureListener() {
            @Override
            public void zoom(InputEvent event, float initialDistance, float distance) {
                scale /= (distance / initialDistance);
                event.handle();
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                int mx = Gdx.input.getX();
                int my = Gdx.input.getY();
                float movement = deltaX * deltaX + deltaY * deltaY;
                if (!isPanning && selection != null) {
                    startedAtPoint = selection.distanceToMouse(xb(mx), yb(my), DrawingBoard.this) <= movement * 2 / scale;
                }
                isPanning = true;
                pan1 = "pan: " + x + ", " + y;
                pan2 = "mpan: " + mx + ", " + my;
                pan3 = "dpan: " + deltaX + ", " + deltaY;
                pan4 = "md: " + movement;
                if (selection != null && startedAtPoint && selection instanceof PointProvider point && point.canMove()) {
                    point.move(deltaX / scale, deltaY / scale);
                } else {
                    offset = offset.add(-deltaX, -deltaY);
                }
                event.handle();
            }

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                int mx = Gdx.input.getX();
                int my = Gdx.input.getY();
                selection = getHoveredShape(mx, my);
                event.handle();
            }

            @Override
            public void panStop(InputEvent event, float x, float y, int pointer, int button) {
                isPanning = false;
            }
        });
        this.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                scale /= Math.pow(1.25, amountY);
                return true;
            }
        });
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            this.shapes.add(new Point(new Vec2(random.nextInt(-100, 100), random.nextInt(-100, 100))));
        }
        for (int i = 0; i < 10; i++) {
            this.shapes.add(new InfiniteLine(new Point(new Vec2(random.nextInt(-100, 100), random.nextInt(-100, 100))), new Point(new Vec2(random.nextInt(-100, 100), random.nextInt(-100, 100)))));
        }
        Point a = new Point(new Vec2(-20, -20));
        this.shapes.add(new InfiniteLine(a, new Point(new Vec2(30, 50))));
        this.shapes.add(a);
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
        drawer.setColor(0, 0, 0, 1);
        drawer.line(bx(-10), by(0), bx(10), by(0), 4);
        drawer.line(bx(-0), by(-10), bx(0), by(10), 4);
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
            font.getFont(40, Color.FIREBRICK).draw(batch, "x: " + offset.x, x(5), y(getHeight() - 30));
            font.getFont(40, Color.FIREBRICK).draw(batch, "y: " + offset.y, x(5), y(getHeight() - 55));
            font.getFont(40, Color.FIREBRICK).draw(batch, "rx: " + bx(0), x(5), y(getHeight() - 80));
            font.getFont(40, Color.FIREBRICK).draw(batch, "ry: " + by(0), x(5), y(getHeight() - 105));
            font.getFont(40, Color.FIREBRICK).draw(batch, "mx: " + mx, x(5), y(getHeight() - 130));
            font.getFont(40, Color.FIREBRICK).draw(batch, "my: " + my, x(5), y(getHeight() - 155));
            font.getFont(40, Color.FIREBRICK).draw(batch, "mxb: " + xb(mx), x(5), y(getHeight() - 180));
            font.getFont(40, Color.FIREBRICK).draw(batch, "mxy: " + yb(my), x(5), y(getHeight() - 205));
            font.getFont(40, Color.FIREBRICK).draw(batch, pan1, x(5), y(getHeight() - 230));
            font.getFont(40, Color.FIREBRICK).draw(batch, pan2, x(5), y(getHeight() - 255));
            font.getFont(40, Color.FIREBRICK).draw(batch, pan3, x(5), y(getHeight() - 280));
            font.getFont(40, Color.FIREBRICK).draw(batch, pan4, x(5), y(getHeight() - 305));
        }
        if (selection != null) {
            font.getFont(40, Color.FIREBRICK).draw(batch, "Selected: " + selection, x(5), y(105));
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
        for (Shape i : shapes) {
            double distance = i.distanceToMouse(xb(mx), yb(my), this);
            if (distance <= minDistance) {
                hovered = i;
                minDistance = distance;
            }
        }
        return hovered;
    }
}

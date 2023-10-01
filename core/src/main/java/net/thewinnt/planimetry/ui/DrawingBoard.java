package net.thewinnt.planimetry.ui;

import java.util.ArrayList;
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
import net.thewinnt.planimetry.shapes.lines.InfiniteLine;
import net.thewinnt.planimetry.shapes.point.Point;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class DrawingBoard extends Actor {
    private final ShapeDrawer drawer;
    private final FontProvider font;
    private final List<Shape> shapes = new ArrayList<>();
    private double scale = 1; // pixels per unit
    private Vec2 offset = Vec2.ZERO;
    private Shape selection;

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
                offset = offset.add(-deltaX, -deltaY);
            }
        });
        this.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                scale /= Math.pow(1.5, amountY);
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
        this.shapes.add(new InfiniteLine(new Point(new Vec2(-100, -50)), new Point(new Vec2(-100, 50))));
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
        if (DynamicPlanimetry.DEBUG_MODE) {
            drawer.filledRectangle(getX(), getY(), getWidth(), getHeight(), new Color(0xEEEEEEFF));
            drawer.setColor(0, 0, 0, 1);
            drawer.line(bx(-10), by(0), bx(10), by(0), 4);
            drawer.line(bx(-0), by(-10), bx(0), by(10), 4);
            drawer.setColor(0, 1, 1, 1);
            drawer.line(bx(-100), by(-70), bx(0), by(90), 2);
            drawer.setColor(1, 0.5f, 0, 1);
            drawer.line(bx(-80), by(70), bx(60), by(-30), 2);
        }
        for (Shape i : shapes) {
            i.render(drawer, i.containsRough(xb(mx), yb(my)), font, this);
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
        }
    }

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
}

package net.thewinnt.planimetry.shapes.point;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import net.thewinnt.planimetry.math.Vec2;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Point implements PointProvider {
    public final Vec2 position;

    public Point(Vec2 position) {
        this.position = position;
    }

    @Override
    public Vec2 getPosition() {
        return position;
    }

    @Override
    public boolean contains(Vec2 point) {
        return point.equals(position);
    }

    @Override
    public boolean contains(double x, double y) {
        return position.x == x && position.y == y;
    }

    @Override
    public void render(ShapeDrawer drawer, boolean selected, BitmapFont font, double scale, Vec2 offset) {
        drawer.filledCircle(offset.add(position).toVector2f(), (float)Math.max(4, 4 / scale));
    }
}

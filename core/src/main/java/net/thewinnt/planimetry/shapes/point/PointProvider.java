package net.thewinnt.planimetry.shapes.point;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;

public interface PointProvider extends Shape {
    Vec2 getPosition();
    boolean canMove();
    void move(Vec2 delta);
    void move(double dx, double dy);
    default double getX() {
        return getPosition().x;
    }
    default double getY() {
        return getPosition().y;
    }
}

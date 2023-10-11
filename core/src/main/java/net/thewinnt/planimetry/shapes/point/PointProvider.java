package net.thewinnt.planimetry.shapes.point;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.DrawingBoard;

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

    @Override
    default boolean contains(Vec2 point) {
        return getPosition().equals(point);
    }

    @Override
    default boolean contains(double x, double y) {
        return getPosition().x == x && getPosition().y == y;
    }

    @Override
    default double distanceToMouse(Vec2 point, DrawingBoard board) {
        return getPosition().distanceTo(point);
    }

    @Override
    default double distanceToMouse(double x, double y, DrawingBoard board) {
        return getPosition().distanceTo(x, y);
    }
}

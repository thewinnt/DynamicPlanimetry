package net.thewinnt.planimetry.shapes.point;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.DrawingBoard;

public abstract class PointProvider extends Shape {
    public abstract Vec2 getPosition();
    public abstract boolean canMove();
    public abstract void move(Vec2 delta);
    public abstract void move(double dx, double dy);
    public double getX() {
        return getPosition().x;
    }
    public double getY() {
        return getPosition().y;
    }

    @Override
    public boolean contains(Vec2 point) {
        return getPosition().equals(point);
    }

    @Override
    public boolean contains(double x, double y) {
        return getPosition().x == x && getPosition().y == y;
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        return getPosition().distanceTo(point);
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return getPosition().distanceTo(x, y);
    }
}

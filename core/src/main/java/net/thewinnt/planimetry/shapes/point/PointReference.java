package net.thewinnt.planimetry.shapes.point;

import java.util.Objects;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class PointReference implements PointProvider {
    private PointProvider point;

    public PointReference(PointProvider point) {
        this.point = point;
    }

    @Override
    public boolean contains(Vec2 point) {
        return this.point.contains(point);
    }

    @Override
    public boolean contains(double x, double y) {
        return this.point.contains(x, y);
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        return this.point.distanceToMouse(point, board);
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return this.point.distanceToMouse(x, y, board);
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        this.point.render(drawer, selection, font, board);
    }

    @Override
    public Vec2 getPosition() {
        return this.point.getPosition();
    }

    @Override
    public boolean canMove() {
        return this.point.canMove();
    }

    @Override
    public void move(Vec2 delta) {
        this.point.move(delta);
    }

    @Override
    public void move(double dx, double dy) {
        this.point.move(dx, dy);
    }

    public void setPoint(PointProvider point) {
        this.point = Objects.requireNonNull(point);
    }

    public PointProvider getPoint() {
        return point;
    }
}
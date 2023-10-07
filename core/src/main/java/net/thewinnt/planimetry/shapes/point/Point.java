package net.thewinnt.planimetry.shapes.point;

import com.badlogic.gdx.graphics.Color;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Point implements PointProvider {
    private Vec2 position;

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
    public void render(ShapeDrawer drawer, boolean selected, FontProvider font, DrawingBoard board) {
        Color color = selected ? DynamicPlanimetry.COLOR_POINT_SELECTED : DynamicPlanimetry.COLOR_POINT;
        drawer.filledCircle(board.boardToGlobal(position).toVector2f(), (float)Math.min(Math.max(2, board.getScale()), 8), color);
    }

    @Override
    public boolean canSelect(Vec2 point, DrawingBoard board) {
        return point.distanceToSqr(position) <= 8 / board.getScale();
    }

    @Override
    public boolean canSelect(double x, double y, DrawingBoard board) {
        return position.distanceToSqr(x, y) <= 8 / board.getScale();
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public void move(Vec2 delta) {
        position = position.add(delta);
    }

    @Override
    public void move(double dx, double dy) {
        position = position.add(dx, dy);
    }
}

package net.thewinnt.planimetry.shapes.point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.util.FontProvider;
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
    public void render(ShapeDrawer drawer, boolean selected, FontProvider font, DrawingBoard board) {
        Color color = selected ? DynamicPlanimetry.COLOR_POINT_SELECTED : DynamicPlanimetry.COLOR_POINT;
        drawer.filledCircle(board.boardToGlobal(position).toVector2f(), (float)Math.min(Math.max(2, board.getScale()), 8), color);
        double mx = board.xb(Gdx.input.getX());
        double my = board.yb(Gdx.input.getY());
        font.getFont(20, Color.FOREST).draw(drawer.getBatch(), String.valueOf(position.distanceTo(mx, my)), board.bx(position.x) + 5, board.by(position.y) + 15);
    }

    @Override
    public boolean containsRough(Vec2 point) {
        return point.distanceToSqr(position) <= 4;
    }

    @Override
    public boolean containsRough(double x, double y) {
        return position.distanceToSqr(x, y) <= 4;
    }
}

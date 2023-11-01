package net.thewinnt.planimetry.shapes.point;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.Gdx;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class MousePoint extends PointProvider {
    private final DrawingBoard board;

    public MousePoint(DrawingBoard board) {
        this.board = board;
    }

    @Override
    public Vec2 getPosition() {
        return new Vec2(this.board.xb(Gdx.input.getX()), this.board.yb(Gdx.input.getY()));
    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        drawer.filledCircle(board.boardToGlobal(getPosition()).toVector2f(), (float)Math.min(Math.max(2, board.getScale()), 8), DynamicPlanimetry.COLOR_POINT_SELECTED);
    }

    @Override public void move(Vec2 delta) {}
    @Override public void move(double dx, double dy) {}

    @Override
    public Collection<Property<?>> getProperties() {
        return List.of();
    }
}

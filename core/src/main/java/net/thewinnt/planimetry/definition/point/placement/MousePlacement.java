package net.thewinnt.planimetry.definition.point.placement;

import com.badlogic.gdx.Gdx;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.Property;

import java.util.Collection;
import java.util.List;

public class MousePlacement extends PointPlacement {
    @Override
    public Vec2 get() {
        DrawingBoard board = DynamicPlanimetry.getInstance().editorScreen.getBoard();
        return new Vec2(board.xb(Gdx.input.getX()), board.yb(Gdx.input.getY()));
    }

    @Override
    public void move(Vec2 delta) {}

    @Override
    public void move(double dx, double dy) {}

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public Collection<Property<?>> properties() {
        return List.of();
    }

    @Override
    public PointPlacementType<?> type() {
        return PointPlacementType.MOUSE_POINTER;
    }

    @Override
    public List<Shape> dependencies() {
        return List.of();
    }
}

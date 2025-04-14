package net.thewinnt.planimetry.definition.point.placement;

import com.badlogic.gdx.Gdx;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Circle;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.Property;

import java.util.Collection;
import java.util.List;

public class CircleMousePlacement extends PointPlacement {
    private final Circle circle;

    public CircleMousePlacement(Circle circle) {
        this.circle = circle;
    }

    @Override
    public Vec2 get() {
        DrawingBoard board = DynamicPlanimetry.getInstance().editorScreen.getBoard();
        double radius = circle.getRadius();
        Vec2 center = circle.center.getPosition();
        Vec2 mouse = new Vec2(board.xb(Gdx.input.getX()), board.yb(Gdx.input.getY()));
        return MathHelper.continueFromAngle(center, MathHelper.angleTo(center, mouse), radius);
    }

    public double getAngle() {
        DrawingBoard board = DynamicPlanimetry.getInstance().editorScreen.getBoard();
        Vec2 center = circle.center.getPosition();
        Vec2 mouse = new Vec2(board.xb(Gdx.input.getX()), board.yb(Gdx.input.getY()));
        return MathHelper.angleTo(center, mouse);
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

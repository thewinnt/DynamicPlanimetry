package net.thewinnt.planimetry.shapes.factories;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.definition.line.infinite.impl.AngleBasedLineDefinition;
import net.thewinnt.planimetry.definition.point.placement.CircleMousePlacement;
import net.thewinnt.planimetry.definition.point.placement.CirclePlacement;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.shapes.Circle;
// import net.thewinnt.planimetry.shapes.lines.CircleTangentLine;
import net.thewinnt.planimetry.shapes.lines.InfiniteLine;
// import net.thewinnt.planimetry.shapes.point.relative.CirclePoint;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.AngleValue;
import net.thewinnt.planimetry.value.type.PointDirectionValue;

public class CircleTangentFactory extends ShapeFactory {
    private final Circle circle;
    private final PointProvider point;
    private boolean isDone;

    public CircleTangentFactory(DrawingBoard board, Circle circle) {
        super(board);
        // TODO circle tangents
        point = new PointProvider(board.getDrawing(), new CircleMousePlacement(circle));
        this.addShape(point);
        this.circle = circle;
        addShape(new InfiniteLine(board.getDrawing(), new AngleBasedLineDefinition(point, DynamicValueType.ADD.create(new PointDirectionValue(circle.center, point), new AngleValue(MathHelper.HALF_PI)))));
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        if (point.getPlacement() instanceof CircleMousePlacement placement) {
            this.point.setPlacement(new CirclePlacement(this.circle, new AngleValue(placement.getAngle())));
        }
        isDone = true;
        return true;
    }

    @Override
    public void onRender(double mx, double my) {
        // this.newLine.setAngle(MathHelper.polarAngle(circle.center.getPosition(), new Vec2(mx, my)));
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public Component getName() {
        return Component.translatable("shape.factory.tangent", circle.getName());
    }

    @Override
    public Collection<Component> getActionHint() {
        return List.of(Component.translatable("shape.factory.hint.circle_tangent.place"));
    }
}

package net.thewinnt.planimetry.shapes.factories;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.shapes.Circle;
// import net.thewinnt.planimetry.shapes.lines.CircleTangentLine;
import net.thewinnt.planimetry.shapes.lines.InfiniteLine;
// import net.thewinnt.planimetry.shapes.point.relative.CirclePoint;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class CircleTangentFactory extends ShapeFactory {
    private final InfiniteLine newLine;
    private final Circle circle;
    private boolean isDone;

    public CircleTangentFactory(DrawingBoard board, Circle circle) {
        super(board);
        // TODO circle tangents
        this.newLine = null;
        this.circle = circle;
        addShape(newLine);
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        // if (getOrCreatePoint(x, y).getPoint() instanceof CirclePoint point) {
        //     this.newLine.stagrt.setPoint(point);
        // }
        // addShape(this.newLine.start);
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

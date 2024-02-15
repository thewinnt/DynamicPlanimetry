package net.thewinnt.planimetry.shapes.factories;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Circle;
import net.thewinnt.planimetry.shapes.lines.CircleTangentLine;
import net.thewinnt.planimetry.shapes.point.relative.CirclePoint;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class CircleTangentFactory extends ShapeFactory {
    private final CircleTangentLine newLine;
    private final Circle circle;
    private boolean isDone;

    public CircleTangentFactory(DrawingBoard board, Circle circle) {
        super(board);
        this.newLine = new CircleTangentLine(board.getDrawing(), circle, 0);
        this.circle = circle;
        addShape(newLine);
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        if (getOrCreatePoint(x, y) instanceof CirclePoint point) {
            this.newLine.a.setPoint(point);
        }
        isDone = true;
        return true;
    }

    @Override
    public void onRender(double mx, double my) {
        // TODO fix half of angles working
        this.newLine.setAngle(MathHelper.angle(new Vec2(mx, my), circle.center.getPosition(), circle.center.getPosition().add(10, 0)));
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public Component getName() {
        return Component.of(Component.literal("Касательная к "), circle.getName());
    }
}

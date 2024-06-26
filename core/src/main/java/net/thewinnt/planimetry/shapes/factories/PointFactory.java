package net.thewinnt.planimetry.shapes.factories;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.MousePoint;
import net.thewinnt.planimetry.shapes.point.Point;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class PointFactory extends ShapeFactory {
    private final PointReference point;

    public PointFactory(DrawingBoard board) {
        super(board);
        this.point = new PointReference(new MousePoint(board.getDrawing()));
        this.addShape(point);
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        this.point.setPoint(new Point(board.getDrawing(), new Vec2(x, y)));
        return true;
    }

    @Override
    public boolean isDone() {
        return !(point.getPoint() instanceof MousePoint);
    }

    @Override
    public Component getName() {
        return Component.translatable("shape.factory.point");
    }

    @Override
    public Collection<Component> getActionHint() {
        return List.of(Component.translatable("shape.factory.hint.point.place"));
    }
}

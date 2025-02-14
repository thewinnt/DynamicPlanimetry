package net.thewinnt.planimetry.shapes.factories;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.lines.PolygonalChain;
import net.thewinnt.planimetry.shapes.point.MousePoint;
import net.thewinnt.planimetry.shapes.point.Point;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class RhombusFactory extends ShapeFactory {
    private PointProvider point1;
    private PointReference point2;
    private PointReference point3;
    private PolygonalChain line;
    private boolean isDone = false;

    public RhombusFactory(DrawingBoard board, int limit) {
        super(board);
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        if (point1 == null) {
            this.point1 = getOrCreatePoint(x, y);
            this.point2 = new PointReference(new MousePoint(board.getDrawing()));
            this.line = new PolygonalChain(board.getDrawing(), point1, point2);
            this.addShape(point1);
            this.addShape(point2);
            this.addShape(line);
            return false;
        } else if (point3 == null) {
            this.point2.setPoint(new Point(board.getDrawing(), new Vec2(x, y)));
            this.point3 = new PointReference(new MousePoint(board.getDrawing()));
            line.addPoint(point3);
            return false;
        } else {
            // TODO finish this
            return true;
        }
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public Component getName() {
        return Component.translatable("shape.factory.rhombus");
    }

    @Override
    public Collection<Component> getActionHint() {
        return List.of(Component.literal("Don't use this"));
    }
}

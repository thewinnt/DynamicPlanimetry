package net.thewinnt.planimetry.shapes.factories;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.shapes.point.MousePoint;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;

public class PointFactory extends ShapeFactory {
    private final PointReference point;

    public PointFactory(DrawingBoard board) {
        super(board);
        this.point = new PointReference(new MousePoint(board.getDrawing()));
        this.addShape(point);
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        this.point.setPoint(this.getOrCreatePoint(x, y));
        return true;
    }

    @Override
    public boolean isDone() {
        return !(point.getPoint() instanceof MousePoint);
    }
}

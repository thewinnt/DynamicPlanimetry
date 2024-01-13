package net.thewinnt.planimetry.shapes.factories;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.shapes.lines.AngledInfiniteLine;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.shapes.point.MousePoint;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;

public class AngledLineFactory extends ShapeFactory {
    private final Line newLine;
    private final PointReference point;
    private boolean isDone;

    public AngledLineFactory(DrawingBoard board, Line originalLine, double angle) {
        super(board);
        this.point = new PointReference(new MousePoint(board.getDrawing()));
        this.newLine = new AngledInfiniteLine(board.getDrawing(), originalLine, point, Math.toRadians(angle));
        addShape(newLine);
        addShape(point);
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        this.point.setPoint(getOrCreatePoint(x, y));
        isDone = true;
        return true;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }
}

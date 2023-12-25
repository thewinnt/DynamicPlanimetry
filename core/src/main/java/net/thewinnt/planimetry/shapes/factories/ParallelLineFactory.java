package net.thewinnt.planimetry.shapes.factories;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.shapes.point.MousePoint;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.shapes.point.relative.TangentOffsetPoint;
import net.thewinnt.planimetry.ui.DrawingBoard;

public class ParallelLineFactory extends ShapeFactory {
    private final Line originalLine;
    private final Line newLine;
    private final PointReference a;
    private boolean isDone;

    public ParallelLineFactory(DrawingBoard board, Line originalLine, LineType newType) {
        super(board);
        this.originalLine = originalLine;
        this.a = new PointReference(new MousePoint(board.getDrawing()));
        PointReference b = new PointReference(new TangentOffsetPoint(board.getDrawing(), a, originalLine.getSlope(), originalLine.a.getPosition().distanceTo(originalLine.b.getPosition())));
        addShape(a);
        addShape(b);
        addShape((newLine = newType.create(board.getDrawing(), a, b)));
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        this.a.setPoint(TangentOffsetPoint.fromPoints(originalLine.a, a));
        return true;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }
}

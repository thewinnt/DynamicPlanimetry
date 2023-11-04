package net.thewinnt.planimetry.shapes.factories;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.shapes.Polygon;
import net.thewinnt.planimetry.shapes.lines.MultiPointLine;
import net.thewinnt.planimetry.shapes.point.MousePoint;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;

public class PolygonFactory extends ShapeFactory {
    private PointProvider point1;
    private PointReference nextPoint;
    private MultiPointLine line;
    private boolean isDone = false;

    public PolygonFactory(DrawingBoard board) {
        super(board);
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        if (point1 == null) {
            this.point1 = getOrCreatePoint(x, y);
            this.nextPoint = new PointReference(new MousePoint(board));
            this.line = new MultiPointLine(point1, nextPoint);
            board.addShape(point1);
            this.addShape(nextPoint);
            this.addShape(line);
            return false;
        } else {
            PointProvider next = getOrCreatePoint(x, y);
            if (next == point1) {
                isDone = line.getPoints().size() >= 3;
                return line.getPoints().size() >= 3;
            }
            this.nextPoint.setPoint(next);
            this.nextPoint = new PointReference(new MousePoint(board));
            this.addShape(this.nextPoint);
            this.line.addPoint(this.nextPoint);
            return false;
        }
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void onFinish() {
        var points = this.line.getPoints();
        points.remove(this.nextPoint);
        board.replaceShape(this.line, new Polygon(points));
        board.removeShape(this.nextPoint);
    }
}

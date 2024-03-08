package net.thewinnt.planimetry.shapes.factories;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.shapes.lines.MultiPointLine;
import net.thewinnt.planimetry.shapes.point.MousePoint;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.shapes.polygons.Polygon;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class FreePolygonFactory extends ShapeFactory {
    private PointProvider point1;
    private PointReference nextPoint;
    private MultiPointLine line;
    private boolean isDone = false;

    public FreePolygonFactory(DrawingBoard board) {
        super(board);
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        if (point1 == null) {
            this.point1 = getOrCreatePoint(x, y);
            this.nextPoint = new PointReference(new MousePoint(board.getDrawing()));
            this.line = new MultiPointLine(board.getDrawing(), point1, nextPoint);
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
            this.nextPoint = new PointReference(new MousePoint(board.getDrawing()));
            this.addShape(this.nextPoint);
            this.line.addPoint(this.nextPoint);
            if (this.line.points.size() == 3) {
                Polygon polygon = new Polygon(board.getDrawing(), this.line.points);
                this.replaceShape(this.line, polygon);
                this.line = polygon;
            }
            return false;
        }
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void onFinish() {
        var points = this.line.points;
        points.remove(this.nextPoint);
        board.removeShape(this.nextPoint);
        Polygon replaceCandidate = ShapeData.asSpecificPolygon((Polygon)this.line);
        if (replaceCandidate != this.line) {
            board.replaceShape(this.line, replaceCandidate);
        }
    }

    @Override
    public Component getName() {
        return Component.translatable("Freeform polygon");
    }
}

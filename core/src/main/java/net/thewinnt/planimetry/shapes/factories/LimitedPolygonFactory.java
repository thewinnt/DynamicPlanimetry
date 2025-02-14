package net.thewinnt.planimetry.shapes.factories;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.shapes.lines.PolygonalChain;
import net.thewinnt.planimetry.shapes.point.MousePoint;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.shapes.polygons.Polygon;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class LimitedPolygonFactory extends ShapeFactory {
    private final int limit;
    private PointProvider point1;
    private PointReference nextPoint;
    private PolygonalChain line;
    private boolean isDone = false;

    public LimitedPolygonFactory(DrawingBoard board, int limit) {
        super(board);
        this.limit = limit;
        if (limit < 3) {
            throw new IllegalArgumentException("A Polygon must have at least three points");
        }
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        if (point1 == null) {
            this.point1 = getOrCreatePoint(x, y);
            this.nextPoint = new PointReference(new MousePoint(board.getDrawing()));
            this.line = new PolygonalChain(board.getDrawing(), point1, nextPoint);
            this.addShape(point1);
            this.addShape(nextPoint);
            this.addShape(line);
            return false;
        } else {
            PointProvider next = getOrCreatePoint(x, y);
            if (next == point1) return false; // don't allow self-looping
            this.nextPoint.setPoint(next);
            this.nextPoint = new PointReference(new MousePoint(board.getDrawing()));
            if (this.line.points.size() == limit) {
                if (this.line.points.size() == 3) {
                    Polygon polygon = new Polygon(board.getDrawing(), this.line.points);
                    this.replaceShape(this.line, polygon);
                    this.line = polygon;
                }
                return true;
            }
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
        return Component.translatable(ShapeData.polygonName(limit), limit);
    }

    @Override
    public Collection<Component> getActionHint() {
        return List.of(Component.translatable("shape.factory.hint.limited_polygon.add_points"));
    }
}

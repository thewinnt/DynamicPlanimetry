package net.thewinnt.planimetry.shapes.factories;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.lines.PolygonalChain;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.polygons.Polygon;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class FreePolygonFactory extends ShapeFactory {
    private PointProvider point1;
    private PointProvider nextPoint;
    private PolygonalChain line;
    private boolean isDone = false;

    public FreePolygonFactory(DrawingBoard board) {
        super(board);
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        if (point1 == null) {
            this.point1 = getOrCreatePoint(x, y);
            this.nextPoint = PointProvider.mouse(board.getDrawing());
            this.line = new PolygonalChain(board.getDrawing(), point1, nextPoint);
            this.addShape(point1);
            this.addShape(nextPoint);
            this.addShape(line);
            return false;
        } else {
            PointProvider next = getPoint();
            if (next == point1) {
                isDone = line.getPoints().size() >= 3;
                return line.getPoints().size() >= 3;
            }
            this.replacePoint(nextPoint, x, y);
            this.nextPoint = PointProvider.mouse(board.getDrawing());
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
    public void onRender(double mx, double my) {
        if (this.line != null && this.line.points.size() >= 3 && (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_ENTER))) {
            finish();
        }
    }

    @Override
    public Component getName() {
        return Component.translatable("Freeform polygon");
    }

    @Override
    public Collection<Component> getActionHint() {
        if (this.line == null) {
            return List.of(Component.translatable("shape.factory.hint.free_polygon.start"));
        } else if (this.line.points.size() < 3) {
            return List.of(Component.translatable("shape.factory.hint.free_polygon.add_points"));
        }
        return List.of(Component.translatable("shape.factory.hint.free_polygon.add_points"), Component.translatable("shape.factory.hint.free_polygon.finish_creation"));
    }

    @Override
    public List<Shape> getShapeWhitelist() {
        if (point1 != null) {
            return List.of(point1);
        }
        return List.of();
    }
}

package net.thewinnt.planimetry.shapes.factories;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.lines.PolygonalChain;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class MultiPointLineFactory extends ShapeFactory {
    private PointProvider point1;
    private PointProvider nextPoint;
    private PolygonalChain line;
    private boolean isDone = false;

    public MultiPointLineFactory(DrawingBoard board) {
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
            if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
                finish();
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void onRender(double mx, double my) {
        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_ENTER) && point1 != null) {
            finish();
        }
    }

    @Override
    public void onFinish() {
        this.line.points.remove(this.nextPoint);
        board.removeShape(this.nextPoint);
    }

    @Override
    public Collection<Component> getActionHint() {
        return List.of(Component.translatable("shape.factory.hint.polygonal_chain.add_points"), Component.translatable("shape.factory.hint.polygonal_chain.finish_creation"));
    }

    @Override
    public Component getName() {
        return Component.translatable("shape.factory.polygonal_chain");
    }

    @Override
    public List<Shape> getShapeWhitelist() {
        if (point1 != null) {
            return List.of(point1);
        }
        return List.of();
    }
}

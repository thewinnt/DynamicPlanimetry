package net.thewinnt.planimetry.shapes.factories;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.shapes.lines.InfiniteLine;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.definition.line.infinite.impl.ParallelLineDefinition;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class ParallelLineFactory extends ShapeFactory {
    private final Line newLine;
    private final PointProvider point;
    private boolean isDone;

    public ParallelLineFactory(DrawingBoard board, Line originalLine) {
        // TODO parallel lines
        super(board);
        this.point = PointProvider.mouse(board.getDrawing());
        this.newLine = new InfiniteLine(board.getDrawing(), new ParallelLineDefinition(originalLine, point));
        addShape(newLine);
        addShape(point);
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        this.replacePoint(point, x, y);
        isDone = true;
        return true;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public Component getName() {
        return Component.translatable("shape.factory.parallel_line");
    }

    @Override
    public Collection<Component> getActionHint() {
        return List.of(Component.translatable("shape.factory.hint.parallel_line.place"));
    }
}

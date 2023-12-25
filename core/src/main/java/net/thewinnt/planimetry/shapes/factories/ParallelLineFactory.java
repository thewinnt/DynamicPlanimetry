package net.thewinnt.planimetry.shapes.factories;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.shapes.lines.ParallelInfiniteLine;
import net.thewinnt.planimetry.ui.DrawingBoard;

public class ParallelLineFactory extends ShapeFactory {
    private final Line originalLine;
    private final Line newLine;
    private boolean isDone;

    public ParallelLineFactory(DrawingBoard board, Line originalLine) {
        super(board);
        this.originalLine = originalLine;
        this.newLine = new ParallelInfiniteLine(board.getDrawing(), originalLine, 0);
        addShape(newLine);
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        // TODO dynamic offset!!!
        isDone = true;
        return true;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }
}

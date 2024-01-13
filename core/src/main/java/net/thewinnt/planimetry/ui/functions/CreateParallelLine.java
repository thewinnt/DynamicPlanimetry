package net.thewinnt.planimetry.ui.functions;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.shapes.factories.AngledLineFactory;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class CreateParallelLine extends BasicNamedFunction<Line> {
    private static final Component NAME = Component.literal("Построить параллельную прямую");
    public LineType type;

    public CreateParallelLine(Drawing drawing, Line shape) {
        super(
            drawing,
            shape,
            line -> {
                DrawingBoard board = DynamicPlanimetry.getInstance().editorScreen.getBoard();
                board.startCreation(new AngledLineFactory(board, line, 0));
            },
            NAME,
            "Построить"
        );
        this.type = shape.getType();
    }
}

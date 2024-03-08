package net.thewinnt.planimetry.ui.functions;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.shapes.factories.AngledLineFactory;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class CreatePerpendicularLine extends BasicNamedFunction<Line> {
    private static final Component NAME = Component.translatable("function.infinite_line.create_perpendicular");
    public LineType type;

    public CreatePerpendicularLine(Drawing drawing, Line shape) {
        super(
            drawing,
            shape,
            line -> {
                DrawingBoard board = DynamicPlanimetry.getInstance().editorScreen.getBoard();
                board.startCreation(new AngledLineFactory(board, line, 90));
            },
            NAME,
            Component.translatable("function.infinite_line.create_perpendicular.action")
        );
        this.type = shape.getType();
    }
}

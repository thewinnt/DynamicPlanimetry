package net.thewinnt.planimetry.ui.functions;

import java.util.Map;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.Settings;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.shapes.factories.AngledLineFactory;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.text.Component;

public class CreateAngledLine extends BasicPropertyFunction<Line> {
    public static final Component NAME = Component.translatable("Построить прямую под углом");
    public static final CharSequence ACTION = Component.translatable("Начать");
    public static final Component PROPERTY = Component.translatable("Угол");

    public CreateAngledLine(Drawing drawing, Line shape) {
        super(
            drawing,
            shape,
            data -> {
                DrawingBoard board = DynamicPlanimetry.getInstance().editorScreen.getBoard();
                board.startCreation(new AngledLineFactory(board, shape, (double)(data.get("angle").getValue())));
            },
            NAME,
            ACTION,
            Map.of("angle", new NumberProperty(PROPERTY, Settings.get().toUnit(MathHelper.HALF_PI)))
        );
    }
    
}

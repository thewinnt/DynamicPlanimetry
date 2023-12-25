package net.thewinnt.planimetry.ui.functions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.shapes.factories.ParallelLineFactory;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.ui.ComponentLabel;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.text.Component;

public class CreateParallelLine extends Function<Line> {
    public LineType type;

    public CreateParallelLine(Drawing drawing, Line shape) {
        super(drawing, shape);
        this.type = shape.getType();
    }

    @Override
    public WidgetGroup setupActors(StyleSet styles) {
        Table table = new Table();
        TextButton apply = new TextButton("Построить", styles.getButtonStyle(Size.MEDIUM, true));
        apply.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                DrawingBoard board = DynamicPlanimetry.getInstance().editorScreen.getBoard();
                board.startCreation(new ParallelLineFactory(board, shape));
                use();
            }
        });
        table.add(new ComponentLabel(Component.literal("Построить параллельную прямую (демо!!!)"), DynamicPlanimetry.getInstance()::getFont, Gdx.graphics.getHeight() / Size.MEDIUM.factor));
        table.add(apply).right().expand();
        return table;
    }
}

package net.thewinnt.planimetry.ui.functions;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.ui.ComponentLabel;
import net.thewinnt.planimetry.ui.ComponentSelectBox;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.text.Component;

public class LineTypeConversion extends Function<Line> {
    private LineType targetType;

    public LineTypeConversion(Drawing drawing, Line shape) {
        super(drawing, shape);
        targetType = shape.getType();
    }

    @Override
    public WidgetGroup setupActors(StyleSet styles) {
        Table table = new Table();
        SelectBox<LineType> selector = new ComponentSelectBox<>(styles.getListStyle(Size.SMALL), List.of(LineType.values()), LineType::toComponent, Size.SMALL);
        selector.setSelected(LineType.INFINITE);
        selector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                targetType = selector.getSelected();
            }
        });
        TextButton apply = new TextButton(DynamicPlanimetry.translate("function.generic_line.convert.action"), styles.getButtonStyle(Size.SMALL, true));
        apply.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                drawing.replaceShape(shape, shape.convertTo(targetType));
                use();
            }
        });
        table.add(new ComponentLabel(Component.translatable("function.generic_line.convert"), styles.font, Size.SMALL));
        table.add(selector).expand();
        table.add(apply).right().expand();
        return table;
    }
}

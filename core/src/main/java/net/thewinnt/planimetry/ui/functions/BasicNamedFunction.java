package net.thewinnt.planimetry.ui.functions;

import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.ComponentLabel;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.text.Component;

public class BasicNamedFunction<T extends Shape> extends Function<T> {
    public final Consumer<T> action;
    public final Component name;
    public final CharSequence actionButton;

    public BasicNamedFunction(Drawing drawing, T shape, Consumer<T> action, Component name, CharSequence actionButton) {
        super(drawing, shape);
        this.action = action;
        this.name = name;
        this.actionButton = actionButton;
    }

    @Override
    public WidgetGroup setupActors(StyleSet styles) {
        Table table = new Table();
        TextButton apply = new TextButton(actionButton.toString(), styles.getButtonStyle(Size.SMALL, true));
        apply.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.accept(shape);
                use();
            }
        });
        table.add(new ComponentLabel(name, DynamicPlanimetry.getInstance()::getBoldFont, Size.SMALL));
        table.add(apply).right().expand();
        return table;
    }
}

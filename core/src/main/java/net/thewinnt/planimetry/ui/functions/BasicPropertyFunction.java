package net.thewinnt.planimetry.ui.functions;

import java.util.Map;
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
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyLayout;
import net.thewinnt.planimetry.ui.text.Component;

public class BasicPropertyFunction<T extends Shape> extends Function<T> {
    public static final Component PARAMETERS = Component.literal("Параметры");
    public final Consumer<Map<String, Property<?>>> action;
    public final Component name;
    public final CharSequence actionButton;
    public final Map<String, Property<?>> properties;

    public BasicPropertyFunction(Drawing drawing, T shape, Consumer<Map<String, Property<?>>> action, Component name, CharSequence actionButton, Map<String, Property<?>> properties) {
        super(drawing, shape);
        this.action = action;
        this.name = name;
        this.actionButton = actionButton;
        this.properties = properties;
    }

    @Override
    public WidgetGroup setupActors(StyleSet styles) {
        Table table = new Table();
        TextButton apply = new TextButton(actionButton.toString(), styles.getButtonStyle(Size.MEDIUM, true));
        apply.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.accept(properties);
                use();
            }
        });
        table.add(new ComponentLabel(name, DynamicPlanimetry.getInstance()::getBoldFont, Size.MEDIUM));
        table.add(apply).right().expandX().row();
        table.add(new PropertyLayout(properties.values(), styles, PARAMETERS, Size.MEDIUM, false)).expand().fill().colspan(9999);
        return table;
    }
    
}

package net.thewinnt.planimetry.ui.properties.types;

import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.ui.ComponentLabel;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;

public class ActionProperty extends Property<Runnable> {
    private final Component buttonName;

    public ActionProperty(Component name, Component buttonName, Runnable action) {
        super(name, action);
        this.buttonName = buttonName;
    }

    @Override
    public void addValueChangeListener(Consumer<Runnable> listener) {
        throw new UnsupportedOperationException("An ActionProperty does not accept listeners");
    }

    @Override
    public boolean filterValue(Runnable value) {
        return true;
    }

    @Override
    public WidgetGroup getActorSetup(StyleSet styles, Size size) {
        Table table = new Table();
        Button apply = new Button(styles.getButtonStyle(size, true));
        apply.add(new ComponentLabel(buttonName, styles.font, size)).fill().center();
        apply.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                value.run();
            }
        });
        table.add(apply).expand().fill();
        return table;
    }
}

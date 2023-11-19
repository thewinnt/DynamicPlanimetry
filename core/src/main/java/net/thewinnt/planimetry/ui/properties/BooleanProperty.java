package net.thewinnt.planimetry.ui.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.ui.StyleSet;

public class BooleanProperty extends Property<Boolean> {
    private final List<Consumer<Boolean>> listeners = new ArrayList<>();
    private boolean value;

    public BooleanProperty() {
        super("");
    }

    public BooleanProperty(boolean value) {
        this();
        this.value = value;
    }

    public BooleanProperty(String name) {
        super(name);
    }

    public BooleanProperty(String name, boolean value) {
        super(name);
        this.value = value;
    }

    @Override
    public void addValueChangeListener(Consumer<Boolean> listener) {
        this.listeners.add(listener);
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public Table getActorSetup(StyleSet styles) {
        Table table = new Table();
        Button checkbox = new Button(styles.getCheckboxStyle());
        checkbox.setChecked(value);
        checkbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                value = checkbox.isChecked();
                for (Consumer<Boolean> i : listeners) {
                    i.accept(value);
                }
            }
        });
        table.add(checkbox).expand().fill();
        return table;
    }
}

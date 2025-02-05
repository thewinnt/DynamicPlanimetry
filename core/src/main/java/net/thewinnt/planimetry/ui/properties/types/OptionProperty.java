package net.thewinnt.planimetry.ui.properties.types;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.properties.layout.BasicLayout;
import net.thewinnt.planimetry.ui.properties.layout.CustomLayout;
import net.thewinnt.planimetry.ui.text.Component;

public class OptionProperty extends BooleanProperty {
    private final List<Consumer<Boolean>> listeners = new ArrayList<>();
    private Component onTrue = Component.translatable("true");
    private Component onFalse = Component.translatable("false");
    private boolean value;

    public OptionProperty() {
        super(Component.empty());
    }

    public OptionProperty(boolean value) {
        this();
        this.value = value;
    }

    public OptionProperty(Component name) {
        super(name);
    }

    public OptionProperty(Component name, boolean value) {
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
        for (Consumer<Boolean> i : this.listeners) {
            i.accept(value);
        }
    }

    public OptionProperty setOnTrue(Component onTrue) {
        this.onTrue = onTrue;
        return this;
    }

    public OptionProperty setOnFalse(Component onFalse) {
        this.onFalse = onFalse;
        return this;
    }

    @Override
    public Table getActorSetup(StyleSet styles, Size size) {
        Table table = new Table();
        String text = value ? onTrue.toString() : onFalse.toString();
        TextButton button = new TextButton(text, styles.getButtonStyle(size, true));
        button.setChecked(value);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                value = button.isChecked();
                button.setText(value ? onTrue.toString() : onFalse.toString());
                for (Consumer<Boolean> i : listeners) {
                    i.accept(value);
                }
            }
        });
        table.add(button).expand().fill();
        return table;
    }

    @Override
    public CustomLayout getLayout() {
        return BasicLayout.INSTANCE;
    }
}

package net.thewinnt.planimetry.ui.properties.types;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.layout.CustomLayout;
import net.thewinnt.planimetry.ui.properties.layout.SquareLayout;
import net.thewinnt.planimetry.ui.text.Component;

public class BooleanProperty extends Property<Boolean> {
    public BooleanProperty() {
        this(Component.empty());
    }

    public BooleanProperty(boolean value) {
        this();
        this.value = value;
    }

    public BooleanProperty(Component name) {
        super(name);
        this.value = Boolean.FALSE;
    }

    public BooleanProperty(Component name, boolean value) {
        super(name, value);
    }

    @Override
    public boolean filterValue(Boolean value) {
        return value != null;
    }

    @Override
    public Table getActorSetup(StyleSet styles, Size size) {
        Table table = new Table();
        Button checkbox = new Button(styles.getCheckboxStyle(size, true)) {
            @Override
            public float getPrefHeight() {
                return size.lines(1);
            }

            @Override
            public float getPrefWidth() {
                return size.lines(1);
            }
        };
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

    @Override
    public CustomLayout getLayout() {
        return SquareLayout.INSTANCE;
    }
}

package net.thewinnt.planimetry.ui.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.ui.StyleSet;

public class EnclosingProperty extends Property<Property<?>> {
    private final List<Property<?>> properties = new ArrayList<>();

    public EnclosingProperty(String name, Property<?>... properties) {
        super(name);
        for (Property<?> i : properties) {
            this.properties.add(i);
        }
    }

    public EnclosingProperty(String name, Collection<Property<?>> properties) {
        super(name);
        this.properties.addAll(properties);
    }

    @Override
    public Property<?> getValue() {
        throw new UnsupportedOperationException("An EnclosingProperty does not have a result");
    }

    @Override
    public void setValue(Property<?> value) {
        throw new UnsupportedOperationException("An EnclosingProperty cannot accept a value");
    }

    @Override
    public void addValueChangeListener(Consumer<Property<?>> listener) {
        throw new UnsupportedOperationException("An EnclosingProperty does not accept listeners");
    }

    @Override
    public WidgetGroup getActorSetup(StyleSet styles) {
        Table table = new Table();
        for (Property<?> i : this.properties) {
            table.add(new Label(i.name, styles.getLabelStyleSmall())).expand().fillX();
            table.add(i.getActorSetup(styles)).expand().fillX().row();
            table.row();
        }
        return table;
    }
}

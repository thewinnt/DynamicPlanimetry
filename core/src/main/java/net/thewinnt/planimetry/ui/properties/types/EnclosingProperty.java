package net.thewinnt.planimetry.ui.properties.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyLayout;
import net.thewinnt.planimetry.ui.text.Component;

public class EnclosingProperty extends Property<Property<?>> {
    private final List<Property<?>> properties = new ArrayList<>();

    public EnclosingProperty(Component name, Property<?>... properties) {
        super(name);
        for (Property<?> i : properties) {
            this.properties.add(i);
        }
    }

    public EnclosingProperty(Component name, Collection<? extends Property<?>> properties) {
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
        return new PropertyLayout(properties, styles, name, false);
    }

    public void addProperty(Property<?> property) {
        this.properties.add(property);
    }

    public void addProperties(Collection<Property<?>> properties) {
        this.properties.addAll(properties);
    }
}

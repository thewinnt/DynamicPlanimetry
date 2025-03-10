package net.thewinnt.planimetry.ui.properties.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyLayout;
import net.thewinnt.planimetry.ui.text.Component;

public class PropertyGroup extends Property<Property<?>> {
    private final List<Property<?>> properties = new ArrayList<>();
    private final BiFunction<StyleSet, Size, Button.ButtonStyle> styleGetter;

    public PropertyGroup(Component name, Property<?>... properties) {
        super(name);
        this.properties.addAll(Arrays.asList(properties));
        this.styleGetter = StyleSet::getButtonStyleToggleable;
    }

    public PropertyGroup(Component name, Collection<? extends Property<?>> properties) {
        super(name);
        this.properties.addAll(properties);
        this.styleGetter = StyleSet::getButtonStyleToggleable;
    }

    public PropertyGroup(Component name, BiFunction<StyleSet, Size, Button.ButtonStyle> styleGetter, Property<?>... properties) {
        super(name);
        this.properties.addAll(Arrays.asList(properties));
        this.styleGetter = styleGetter;
    }

    public PropertyGroup(Component name, BiFunction<StyleSet, Size, Button.ButtonStyle> styleGetter, Collection<? extends Property<?>> properties) {
        super(name);
        this.properties.addAll(properties);
        this.styleGetter = styleGetter;
    }

    @Override
    public Property<?> getValue() {
        throw new UnsupportedOperationException("A PropertyGroup does not have a result");
    }

    @Override
    public void setValue(Property<?> value) {
        throw new UnsupportedOperationException("A PropertyGroup cannot accept a value");
    }

    @Override
    public boolean filterValue(Property<?> value) {
        return false;
    }

    @Override
    public void addValueChangeListener(Consumer<Property<?>> listener) {
        throw new UnsupportedOperationException("A PropertyGroup does not accept listeners");
    }

    @Override
    public WidgetGroup getActorSetup(StyleSet styles, Size size) {
        return new PropertyLayout(properties, styles, name, size, false, styleGetter);
    }

    public void addProperty(Property<?> property) {
        this.properties.add(property);
    }

    public void addProperties(Collection<Property<?>> properties) {
        this.properties.addAll(properties);
    }
}

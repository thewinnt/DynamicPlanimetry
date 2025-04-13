package net.thewinnt.planimetry.ui.properties.types;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;

import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;

import java.util.function.Consumer;

public class InlineTypeProperty extends Property<Property<?>> {
    private final SelectionProperty<?> typeSelector;

    public InlineTypeProperty(Component name, SelectionProperty<?> typeSelector, Property<?> value) {
        super(name, value);
        this.typeSelector = typeSelector;
    }

    @Override
    public Property<?> getValue() {
        throw new UnsupportedOperationException("An InlineTypeProperty does not have a result");
    }

    @Override
    public void setValue(Property<?> value) {
        throw new UnsupportedOperationException("An InlineTypeProperty cannot accept a value");
    }

    @Override
    public boolean filterValue(Property<?> value) {
        return false;
    }

    @Override
    public void addValueChangeListener(Consumer<Property<?>> listener) {
        throw new UnsupportedOperationException("An InlineTypeProperty does not accept listeners");
    }

    @Override
    public WidgetGroup getActorSetup(StyleSet styles, Size size) {
        HorizontalGroup group = new HorizontalGroup().expand().fill().space(5).align(Align.bottomRight);
        group.addActor(typeSelector.getActorSetup(styles, size));
        group.addActor(value.getActorSetup(styles, size));
        return group;
    }
}

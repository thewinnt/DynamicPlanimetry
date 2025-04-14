package net.thewinnt.planimetry.ui.properties.types;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.ui.ComponentLabel;
import net.thewinnt.planimetry.ui.DynamicComponentLabel;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.layout.CustomLayout;
import net.thewinnt.planimetry.ui.properties.layout.RightAlignedLayout;
import net.thewinnt.planimetry.ui.text.Component;


public class DisplayProperty extends Property<Component> {
    public final Supplier<Component> data;
    private final boolean isDynamic;

    public DisplayProperty(Component name) {
        super(name);
        this.data = Component::empty;
        this.isDynamic = false;
    }

    public DisplayProperty(Component name, Component data) {
        super(name);
        this.data = () -> data;
        this.isDynamic = false;
    }

    public DisplayProperty(Component name, Supplier<Component> data) {
        super(name);
        this.data = data;
        this.isDynamic = true;
    }

    @Override
    public void addValueChangeListener(Consumer<Component> listener) {
        throw new UnsupportedOperationException("DisplayProperty is read-only");
    }

    @Override
    public WidgetGroup getActorSetup(StyleSet styles, Size size) {
        if (isDynamic) {
            return new Container<>(new DynamicComponentLabel(data, styles.font, size)).fill();
        } else {
            return new Container<>(new ComponentLabel(data.get(), styles.font, size)).fill();
        }
    }

    @Override
    public Component getValue() {
        return data.get();
    }

    @Override
    public void setValue(Component value) {
        throw new UnsupportedOperationException("DisplayProperty is read-only");
    }

    @Override
    public boolean filterValue(Component value) {
        return false;
    }

    @Override
    public CustomLayout getLayout() {
        return RightAlignedLayout.INSTANCE;
    }
}

package net.thewinnt.planimetry.ui.properties.types;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.ui.DynamicComponentLabel;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.layout.CustomLayout;
import net.thewinnt.planimetry.ui.properties.layout.RightAlignedLayout;
import net.thewinnt.planimetry.ui.text.Component;


public class DisplayProperty extends Property<Component> {
    public final Supplier<Component> data;

    public DisplayProperty(Component name) {
        super(name);
        this.data = () -> Component.empty();
    }

    public DisplayProperty(Component name, Component data) {
        super(name);
        this.data = () -> data;
    }
    
    public DisplayProperty(Component name, Supplier<Component> data) {
        super(name);
        this.data = data;
    }

    @Override
    public void addValueChangeListener(Consumer<Component> listener) {
        throw new UnsupportedOperationException("DisplayProperty is read-only");
    }

    @Override
    public WidgetGroup getActorSetup(StyleSet styles) {
        return new Container<>(new DynamicComponentLabel(data, styles.font, Gdx.graphics.getHeight() / Size.MEDIUM.factor)).fill();
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
    public CustomLayout getLayout() {
        return RightAlignedLayout.INSTANCE;
    }
}

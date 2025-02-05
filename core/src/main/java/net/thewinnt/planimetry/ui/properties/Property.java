package net.thewinnt.planimetry.ui.properties;

import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.properties.layout.BasicLayout;
import net.thewinnt.planimetry.ui.properties.layout.CustomLayout;
import net.thewinnt.planimetry.ui.text.Component;

public abstract class Property<T> {
    public final Component name;
    public CustomLayout layoutOverride;

    public Property(Component name) {
        this.name = name;
    }

    public Component getName() {
        return name;
    }

    public CustomLayout getLayout() {
        if (layoutOverride != null) return layoutOverride;
        return BasicLayout.INSTANCE;
    }

    public abstract void addValueChangeListener(Consumer<T> listener);
    public abstract WidgetGroup getActorSetup(StyleSet styles, Size size);

    public abstract T getValue();
    public abstract void setValue(T value);
}

package net.thewinnt.planimetry.ui.properties;

import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.properties.ui.BasicLayout;
import net.thewinnt.planimetry.ui.properties.ui.CustomLayout;

public abstract class Property<T> {
    public final String name;

    public Property(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public CustomLayout getLayout() {
        return BasicLayout.INSTANCE;
    }

    public abstract void addValueChangeListener(Consumer<T> listener);
    public abstract WidgetGroup getActorSetup(StyleSet styles);
    public abstract T getValue();
    public abstract void setValue(T value);
}

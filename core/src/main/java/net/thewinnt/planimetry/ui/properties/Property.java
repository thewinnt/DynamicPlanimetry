package net.thewinnt.planimetry.ui.properties;

import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.ui.StyleSet;

public abstract class Property<T> {
    public final String name;

    public Property(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void addValueChangeListener(Consumer<T> listener);
    public abstract WidgetGroup getActorSetup(StyleSet styles);
    public abstract T getValue();
    public abstract void setValue(T value);
}
package net.thewinnt.planimetry.ui.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.properties.layout.BasicLayout;
import net.thewinnt.planimetry.ui.properties.layout.CustomLayout;
import net.thewinnt.planimetry.ui.text.Component;

public abstract class Property<T> {
    public final Component name;
    protected final List<Consumer<T>> listeners = new ArrayList<>();
    protected T value;
    public CustomLayout layoutOverride;

    public Property(Component name) {
        this.name = name;
    }

    public Property(Component name, T value) {
        this(name);
        this.value = value;
    }

    public Component getName() {
        return name;
    }

    public CustomLayout getLayout() {
        if (layoutOverride != null) return layoutOverride;
        return BasicLayout.INSTANCE;
    }

    public void addValueChangeListener(Consumer<T> listener) {
        this.listeners.add(listener);
    }

    public abstract WidgetGroup getActorSetup(StyleSet styles, Size size);
    public abstract boolean filterValue(T value);

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (filterValue(value)) {
            this.value = value;
            for (Consumer<T> i : listeners) {
                i.accept(value);
            }
        }
    }

    public void setValueSilent(T value) {
        if (filterValue(value)) {
            this.value = value;
        }
    }
}

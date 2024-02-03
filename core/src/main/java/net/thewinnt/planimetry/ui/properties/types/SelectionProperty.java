package net.thewinnt.planimetry.ui.properties.types;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.ui.ComponentSelectBox;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;

public class SelectionProperty<T> extends Property<T> {
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private T[] options;
    private T selected;
    private Function<T, Component> componentProvider = t -> {
        if (t instanceof ComponentRepresentable component) return component.toComponent();
        return Component.literal(t.toString());
    };

    public SelectionProperty(Component name, T[] options) {
        super(name);
        this.options = options;
    }

    public SelectionProperty(T selected, Component name, T[] options) {
        this(name, options);
        this.selected = selected;
    }

    public SelectionProperty<T> setComponentProvider(Function<T, Component> componentProvider) {
        this.componentProvider = componentProvider;
        return this;
    }

    @Override
    public void addValueChangeListener(Consumer<T> listener) {
        this.listeners.add(listener);
    }

    @Override
    public T getValue() {
        return selected;
    }

    @Override
    public void setValue(T value) {
        selected = value;
        for (Consumer<T> i : this.listeners) {
            i.accept(value);
        }
    }

    @Override
    public WidgetGroup getActorSetup(StyleSet styles, Size size) {
        Table output = new Table();
        SelectBox<T> selector = new ComponentSelectBox<>(styles.getListStyle(size), List.of(options), componentProvider, size);
        selector.setSelected(selected);
        selector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selected = selector.getSelected();
                for (Consumer<T> i : listeners) {
                    i.accept(selected);
                }
            }
        });

        output.add(selector).expand().fill();
        return output;
    }
}

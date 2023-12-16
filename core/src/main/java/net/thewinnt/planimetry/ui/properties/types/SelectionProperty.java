package net.thewinnt.planimetry.ui.properties.types;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.text.Component;

public class SelectionProperty<T> extends Property<T> {
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private T[] options;
    private T selected;

    public SelectionProperty(Component name, T[] options) {
        super(name);
        this.options = options;
    }

    public SelectionProperty(T selected, Component name, T[] options) {
        this(name, options);
        this.selected = selected;
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
    }

    @Override
    public WidgetGroup getActorSetup(StyleSet styles) {
        Table output = new Table();
        SelectBox<T> selector = new SelectBox<>(styles.getListStyle(Size.MEDIUM));
        selector.setItems(options);
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

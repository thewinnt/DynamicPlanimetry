package net.thewinnt.planimetry.ui.properties.types;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.ui.ComponentSelectBox;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;

public class SelectionProperty<T> extends Property<T> {
    private Collection<T> options;
    private Function<T, Component> componentProvider = t -> {
        if (t instanceof ComponentRepresentable component) return component.toComponent();
        return Component.literal(t.toString());
    };

    public SelectionProperty(Component name, T[] options) {
        super(name);
        this.options = List.of(options);
    }

    public SelectionProperty(T selected, Component name, T[] options) {
        this(name, options);
        this.value = selected;
    }

    public SelectionProperty(Component name, Collection<T> options) {
        super(name);
        this.options = options;
    }

    public SelectionProperty(T selected, Component name, Collection<T> options) {
        this(name, options);
        this.value = selected;
    }

    public SelectionProperty<T> setComponentProvider(Function<T, Component> componentProvider) {
        this.componentProvider = componentProvider;
        return this;
    }

    @Override
    public boolean filterValue(T value) {
        return options.contains(value);
    }

    @Override
    public WidgetGroup getActorSetup(StyleSet styles, Size size) {
        Table output = new Table();
        SelectBox<T> selector = new ComponentSelectBox<>(styles.getListStyle(size), options, componentProvider, size);
        selector.setSelected(value);
        selector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setValue(selector.getSelected());
            }
        });

        output.add(selector).expand().fill();
        return output;
    }
}

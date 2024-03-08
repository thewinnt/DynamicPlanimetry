package net.thewinnt.planimetry.ui.properties.types;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import io.jbock.util.Either;
import net.thewinnt.planimetry.ui.ComponentLabel;
import net.thewinnt.planimetry.ui.ComponentSelectBox;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.drawable.DynamicIcon;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;

public class ActionSelectionProperty<T> extends Property<T> {
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private T[] options;
    private T selected;
    private Function<T, Component> componentProvider = t -> {
        if (t instanceof ComponentRepresentable component) return component.toComponent();
        return Component.literal(t.toString());
    };
    private Runnable action;
    private boolean positionBefore;
    private Either<Component, DynamicIcon> actionDisplay;

    public ActionSelectionProperty(Component name, T[] options) {
        super(name);
        this.options = options;
    }

    public ActionSelectionProperty(T selected, Component name, T[] options) {
        this(name, options);
        this.selected = selected;
    }

    public ActionSelectionProperty<T> setComponentProvider(Function<T, Component> componentProvider) {
        this.componentProvider = componentProvider;
        return this;
    }

    public ActionSelectionProperty<T> setAction(Runnable action, boolean positionBefore, Either<Component, DynamicIcon> value) {
        this.action = action;
        this.positionBefore = positionBefore;
        this.actionDisplay = value;
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

    private Button createButton(StyleSet styles, Size size) {
        Button output = new Button(styles.getButtonStyle(size, true)) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
                setWidth(getHeight());
            }

            @Override
            public float getMaxWidth() {
                return getHeight();
            }

            @Override
            public float getPrefWidth() {
                return getPrefHeight();
            }

            @Override
            public float getWidth() {
                return getHeight();
            }
        };
        if (actionDisplay.isLeft()) {
            output.add(new ComponentLabel(actionDisplay.getLeft().get(), styles.font, size));
        } else {
            output.add(new Widget() {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    super.draw(batch, parentAlpha);
                    actionDisplay.getRight().get().draw(batch, getX(), getY(), getWidth(), getHeight());
                }
            }).expand().fill();
        }
        output.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        return output;
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
        if (action != null && positionBefore) {
            output.add(createButton(styles, size)).fill().expand().width(Value.percentHeight(1)).padRight(5);
        }
        output.add(selector).expand().fill();
        if (action != null && !positionBefore) {
            output.add(createButton(styles, size)).fill().expand().width(Value.percentHeight(1)).padLeft(5);
        }
        output.setDebug(true);
        return output;
    }
}

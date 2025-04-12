package net.thewinnt.planimetry.ui.properties;

import java.util.Collection;
import java.util.function.BiFunction;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.ListSwitch;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.text.Component;

public class PropertyLayout extends WidgetGroup {
    private final Container<VerticalGroup> pane;
    public final ListSwitch list;

    public PropertyLayout(Collection<? extends Property<?>> properties, StyleSet styles, Component name, Size size, boolean open) {
        this(properties, styles, name, size, open, StyleSet::getButtonStyleToggleable);
    }

    public PropertyLayout(Collection<? extends Property<?>> properties, StyleSet styles, Component name, Size size, boolean open, BiFunction<StyleSet, Size, Button.ButtonStyle> styleGetter) {
        super();
        this.pane = new Container<>();
        if (name != null) {
            this.list = new ListSwitch(name, styles, size, styleGetter);
            list.setChecked(open);
            list.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (list.isChecked()) {
                        PropertyLayout.this.addActor(pane);
                    } else {
                        PropertyLayout.this.removeActor(pane);
                    }
                    DynamicPlanimetry.getInstance().editorScreen.layout();
                }
            });
        } else {
            this.list = null;
        }

        // pane.setupOverscroll(Gdx.graphics.getHeight() / Size.MEDIUM.getFactor() * 1.1f, 10, 200);

        VerticalGroup propertyList = new VerticalGroup().top().left().expand().fill().pad(2, 5, 2, 0);
        propertyList.setFillParent(true);
        for (Property<?> i : properties) {
            propertyList.addActor(new PropertyEntry(i, styles, size));
        }
        this.pane.setActor(propertyList);
        this.pane.align(Align.bottomLeft);
        this.pane.fill();

        if (list != null) this.addActor(list);
        if (open) this.addActor(pane);
    }

    @Override
    public void layout() {
        if (list == null) {
            this.pane.setBounds(0, 0, getWidth(), getHeight());
        } else {
            this.list.setBounds(0, getHeight() - list.getPrefHeight(), getWidth(), list.getPrefHeight());
            this.pane.setBounds(0, 0, getWidth(), getHeight() - list.getPrefHeight() - 2);
        }
    }

    @Override
    public float getPrefHeight() {
        if (this.list == null) {
            return this.pane.getPrefHeight();
        } else if (this.list.isChecked()) {
            return this.list.getPrefHeight() + this.pane.getPrefHeight();
        } else {
            return this.list.getPrefHeight();
        }
    }

    @Override
    public float getPrefWidth() {
        if (this.list == null) {
            return this.pane.getPrefWidth();
        }
        return Math.max(this.list.getPrefWidth(), this.pane.getPrefWidth());
    }

    @Override
    public float getMinHeight() {
        if (this.list != null && this.list.isChecked()) {
            return this.list.getPrefHeight() * 2;
        } else {
            return this.list.getPrefHeight();
        }
    }

    @Override
    public float getMinWidth() {
        return this.pane.getMinWidth();
    }
}

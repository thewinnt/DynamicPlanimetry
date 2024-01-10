package net.thewinnt.planimetry.ui.properties;

import java.util.Collection;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.thewinnt.planimetry.ui.ListSwitch;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.text.Component;

public class PropertyLayout extends WidgetGroup {
    private final Collection<Property<?>> properties;
    private final StyleSet styles;
    private final ScrollPane pane;
    private final VerticalGroup propertyList;
    public final ListSwitch list;

    public PropertyLayout(Collection<Property<?>> properties, StyleSet styles, Component name, boolean open) {
        super();
        this.properties = properties;
        this.styles = styles;
        this.pane = new ScrollPane(null, styles.getScrollPaneStyleNoBg());
        if (name != null) {
            this.list = new ListSwitch(name, styles);
            list.setChecked(open);
            list.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    PropertyLayout.this.rebuild();
                    if (list.isChecked()) {
                        PropertyLayout.this.addActor(pane);
                    } else {
                        PropertyLayout.this.removeActor(pane);
                    }
                }
            });
        } else {
            this.list = null;
        }

        pane.setupOverscroll(Gdx.graphics.getHeight() / Size.MEDIUM.factor * 1.1f, 10, 200);
        
        this.propertyList = new VerticalGroup().top().left().expand().fill().pad(2, 5, 2, 5);
        this.propertyList.setFillParent(true);
        this.pane.setActor(propertyList);
        if (open) {
            rebuild();
            this.addActor(pane);
        }
        this.pane.setDebug(true, true);
        
        if (list != null) this.addActor(list);
    }

    @Override
    public void layout() {
        if (list == null) {
            this.pane.setBounds(0, 0, getWidth(), getHeight());
        } else {
            this.list.setBounds(0, getHeight() - list.getPrefHeight(), getWidth(), list.getPrefHeight());
            this.pane.setBounds(0, 0, getWidth(), getHeight() - list.getPrefHeight());
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
    public float getMinHeight() {
        if (this.list != null && this.list.isChecked()) {
            return this.list.getPrefHeight() * 2;
        } else {
            return this.list.getPrefHeight();
        }
    }

    private void rebuild() {
        this.propertyList.clearChildren();
        for (Property<?> i : properties) {
            this.propertyList.addActor(new PropertyEntry(i, styles));
        }
    }
}

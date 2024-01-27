package net.thewinnt.planimetry.ui.properties;

import java.util.Collection;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.ListSwitch;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.text.Component;

public class PropertyLayout extends WidgetGroup {
    private final Container<VerticalGroup> pane;
    private final VerticalGroup propertyList;
    public final ListSwitch list;

    public PropertyLayout(Collection<Property<?>> properties, StyleSet styles, Component name, boolean open) {
        super();
        this.pane = new Container<>();
        if (name != null) {
            this.list = new ListSwitch(name, styles);
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

        // pane.setupOverscroll(Gdx.graphics.getHeight() / Size.MEDIUM.factor * 1.1f, 10, 200);
        
        this.propertyList = new VerticalGroup().top().left().expand().fill().pad(2, 5, 2, 5);
        this.propertyList.setFillParent(true);
        for (Property<?> i : properties) {
            this.propertyList.addActor(new PropertyEntry(i, styles));
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
    public float getMinHeight() {
        if (this.list != null && this.list.isChecked()) {
            return this.list.getPrefHeight() * 2;
        } else {
            return this.list.getPrefHeight();
        }
    }
}

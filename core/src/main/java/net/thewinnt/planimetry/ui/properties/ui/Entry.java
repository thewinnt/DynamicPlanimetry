package net.thewinnt.planimetry.ui.properties.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.properties.Property;

public class Entry extends WidgetGroup {
    private Property<?> property;
    private Label name;
    private StyleSet styles;
    private Actor propertySetup;

    public Entry(Property<?> property, String name, StyleSet styles) {
        this.property = property;
        this.name = new Label(name, styles.getLabelStyle(Size.MEDIUM));
        this.styles = styles;
        this.propertySetup = property.getActorSetup(styles);
        super.addActor(this.name);
        super.addActor(this.propertySetup);
    }

    @Override
    public float getPrefWidth() {
        return name.getPrefWidth() + this.property.getActorSetup(styles).getPrefWidth();
    }

    @Override
    public float getPrefHeight() {
        return Math.max(name.getPrefHeight(), this.property.getActorSetup(styles).getPrefHeight());
    }
    
    @Override
    public void layout() {
        this.name.setBounds(0, 0, name.getPrefWidth(), getHeight());
        this.propertySetup.setBounds((int)Math.ceil(name.getPrefWidth() / 25) * 25, 0, getWidth() - (int)Math.ceil(name.getPrefWidth() / 25) * 25, getHeight());
        // TODO try aligning to a table
        // TODO fix wide checkboxes
        // TODO fix text field scrolling
    }
}

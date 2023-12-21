package net.thewinnt.planimetry.ui.properties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.ui.ComponentLabel;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.properties.types.Property;

public class Entry extends WidgetGroup {
    private Property<?> property;
    private ComponentLabel name;
    private StyleSet styles;
    private Actor propertySetup;

    public Entry(Property<?> property, StyleSet styles) {
        this.property = property;
        this.name = new ComponentLabel(property.getName(), styles.font, (int)Gdx.graphics.getHeight() / 18);
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
        return Math.max(name.getPrefHeight(), this.property.getActorSetup(styles).getPrefHeight()) + 4;
    }
    
    @Override
    public void layout() {
        this.name.setBounds(0, 2, name.getPrefWidth(), getHeight() - 4);
        this.property.getLayout().layout(this.propertySetup, this);
        // TODO try aligning to a table
        // TODO fix text field scrolling
    }

    public ComponentLabel getNameLabel() {
        return name;
    }
}

package net.thewinnt.planimetry.ui.properties;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import net.thewinnt.planimetry.ui.ComponentLabel;
import net.thewinnt.planimetry.ui.StyleSet;
import net.thewinnt.planimetry.ui.StyleSet.Size;

public class PropertyEntry extends WidgetGroup {
    private Property<?> property;
    private ComponentLabel name;
    private WidgetGroup propertySetup;

    public PropertyEntry(Property<?> property, StyleSet styles) {
        this.property = property;
        this.name = new ComponentLabel(property.getName(), styles.font, Size.MEDIUM);
        this.propertySetup = property.getActorSetup(styles);
        super.addActor(this.name);
        super.addActor(this.propertySetup);
    }

    @Override
    public float getPrefWidth() {
        return name.getPrefWidth() + this.propertySetup.getPrefWidth();
    }

    @Override
    public float getPrefHeight() {
        return Math.max(name.getPrefHeight(), this.propertySetup.getPrefHeight()) + 4;
    }
    
    @Override
    public void layout() {
        if (this.propertySetup instanceof PropertyLayout) {
            super.removeActor(name);
            this.propertySetup.setBounds(0, 0, getWidth(), getHeight());
        } else {
            this.name.setBounds(0, 2, name.getPrefWidth(), getHeight() - 4);
            this.property.getLayout().layout(this.propertySetup, this);
        }
    }

    public ComponentLabel getNameLabel() {
        return name;
    }
}

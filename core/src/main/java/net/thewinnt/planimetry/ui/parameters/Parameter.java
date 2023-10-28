package net.thewinnt.planimetry.ui.parameters;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.thewinnt.planimetry.ui.StyleSet;

public abstract class Parameter<T> {
    protected final StyleSet styleSet;

    public Parameter(StyleSet styles) {
        this.styleSet = styles;
    }

    public abstract Table getActorSetup();
    public abstract T getValue();
    public abstract void updateStyles();
}

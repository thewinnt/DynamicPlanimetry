package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;

public class ComponentLabel extends Widget {
    private final Component component;
    public FontProvider font;
    public int fontSize;

    public ComponentLabel(Component component, FontProvider font, int fontSize) {
        this.component = component;
        this.font = font;
        this.fontSize = fontSize;
        this.setColor(Theme.current().textButton());
    }

    @Override
    public float getPrefWidth() {
        return (float)this.component.getSize(font, fontSize).x;
    }

    @Override
    public float getPrefHeight() {
        return (float)this.component.getSize(font, fontSize).y;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        component.draw(batch, font, fontSize, Theme.current().textUI(), getX(), getY() + getHeight() * 3 / 4);
    }
}

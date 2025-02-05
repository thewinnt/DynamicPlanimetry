package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;

public class ComponentLabel extends Widget {
    private final Component component;
    public FontProvider font;
    public Size size;

    public ComponentLabel(Component component, FontProvider font, Size size) {
        this.component = component;
        this.font = font;
        this.size = size;
        this.setColor(Theme.current().textButton());
    }

    @Override
    public float getPrefWidth() {
        return (float)this.component.getSize(font, size).x;
    }

    @Override
    public float getPrefHeight() {
        return (float)this.component.getSize(font, size).y;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        component.draw(batch, font, size, Theme.current().textUI(), getX(), getY() + getHeight() * 3 / 4);
    }
}

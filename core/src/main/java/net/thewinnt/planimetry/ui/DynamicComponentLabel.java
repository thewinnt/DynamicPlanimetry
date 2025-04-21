package net.thewinnt.planimetry.ui;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;

public class DynamicComponentLabel extends Widget {
    private final Supplier<Component> component;
    public FontProvider font;
    public Size size;

    public DynamicComponentLabel(Supplier<Component> component, FontProvider font, Size size) {
        this.component = component;
        this.font = font;
        this.size = size;
        this.setColor(GuiTheme.current().textButton());
    }

    @Override
    public float getPrefWidth() {
        return (float)this.component.get().getSize(font, size).x;
    }

    @Override
    public float getPrefHeight() {
        return (float)this.component.get().getSize(font, size).y;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        component.get().draw(batch, font, size, GuiTheme.current().textUI(), getX(), getY() + getHeight() * 3 / 4);
    }
}

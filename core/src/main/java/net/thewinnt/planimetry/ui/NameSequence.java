package net.thewinnt.planimetry.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.thewinnt.planimetry.util.FontProvider;

public class NameSequence extends Widget {
    private List<NameComponent> components = new ArrayList<>();
    public FontProvider font;
    public int fontSize;

    public NameSequence(List<NameComponent> components, FontProvider font, int fontSize) {
        components.addAll(components);
        this.font = font;
        this.fontSize = fontSize;
    }

    @Override
    public float getPrefWidth() {
        float output = 0;
        for (NameComponent i : this.components) {
            output += (float)i.getSize(font, fontSize).x;
        }
        return output;
    }

    @Override
    public float getPrefHeight() {
        float output = 0;
        for (NameComponent i : this.components) {
            output += (float)i.getSize(font, fontSize).y;
        }
        return output;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = getX();
        for (NameComponent i : components) {
            x += i.draw(batch, font, fontSize, getColor(), x, getY()).x;
        }
    }
}

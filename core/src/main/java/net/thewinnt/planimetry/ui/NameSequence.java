package net.thewinnt.planimetry.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.thewinnt.planimetry.util.FontProvider;

public class NameSequence extends Widget {
    private String typeName;
    private List<NameComponent> components = new ArrayList<>();
    public FontProvider font;
    public int fontSize;

    public NameSequence(String typeName, List<NameComponent> components, FontProvider font, int fontSize) {
        this.components.addAll(components);
        this.typeName = typeName;
        this.font = font;
        this.fontSize = fontSize;
        this.setColor(Theme.current().textButton());
    }

    @Override
    public float getPrefWidth() {
        BitmapFont fontMain = font.getFont(fontSize, Theme.current().textButton());
        BitmapFontCache cache = fontMain.getCache();
        cache.clear();
        float output = cache.addText(typeName + " ", 0, 0).width;
        for (NameComponent i : this.components) {
            output += (float)i.getSize(font, fontSize).x;
        }
        return output;
    }

    @Override
    public float getPrefHeight() {
        float output = font.getFont(fontSize, Color.BLACK).getLineHeight(); // account for type name
        for (NameComponent i : this.components) {
            float j = (float)i.getSize(font, fontSize).y;
            if (j > output) {
                output = j;
            }
        }
        return output;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = getX() + font.getFont(fontSize, getColor()).draw(batch, typeName + " ", getX(), getY() + getHeight() * 3 / 4).width;
        for (NameComponent i : components) {
            x += i.draw(batch, font, fontSize, getColor(), x, getY() + getHeight() * 3 / 4).x;
        }
    }
}

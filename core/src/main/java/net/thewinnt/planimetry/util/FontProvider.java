package net.thewinnt.planimetry.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

@FunctionalInterface
public interface FontProvider {
    public BitmapFont getFont(int size, Color color);
}

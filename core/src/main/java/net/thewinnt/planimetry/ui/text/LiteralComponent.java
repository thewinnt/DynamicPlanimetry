package net.thewinnt.planimetry.ui.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.util.FontProvider;

public record LiteralComponent(String text) implements Component, CharSequence {
    public static final LiteralComponent EMPTY = new LiteralComponent("");

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public char charAt(int index) {
        return text.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return text.subSequence(start, end);
    }

    @Override
    public Vec2 draw(Batch batch, FontProvider font, int fontSize, Color color, float x, float y) {
        GlyphLayout layout = font.getFont(fontSize, color).draw(batch, text, x, y);
        return new Vec2(layout.width, font.getFont(fontSize, color).getLineHeight());
    }

    @Override
    public Vec2 getSize(FontProvider font, int fontSize) {
        GlyphLayout layout = font.getFont(fontSize, Color.BLACK).newFontCache().addText(text, 0, 0);
        return new Vec2(layout.width, font.getFont(fontSize, Color.BLACK).getLineHeight());
    }
    
    @Override
    public String toString() {
        return text;
    }
}

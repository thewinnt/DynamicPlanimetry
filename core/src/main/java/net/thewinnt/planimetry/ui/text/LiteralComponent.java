package net.thewinnt.planimetry.ui.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import com.badlogic.gdx.utils.Null;
import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.gdxutils.FontUtils;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.util.FontProvider;
import org.jetbrains.annotations.Nullable;

public record LiteralComponent(String text, Formatting formatting) implements Component, CharSequence {
    public static final LiteralComponent EMPTY = new LiteralComponent("");

    public LiteralComponent(String text) {
        this(text, Formatting.EMPTY);
    }

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
    public Vec2 drawGetSize(Batch batch, FontProvider font, Size size, Color color, float x, float y) {
        GlyphLayout layout = font.getFont((int)(size.lines(1)), color).draw(batch, text, x, y);
        return new Vec2(layout.width, font.getFont((int)(size.lines(1)), color).getLineHeight());
    }

    @Override
    public void draw(Batch batch, FontProvider font, Size size, Color color, float x, float y) {
        font.getFont((int)(size.lines(1)), color).draw(batch, text, x, y);
    }

    @Override
    public Vec2 getSize(FontProvider font, Size size) {
        BitmapFont fnt = font.getFont((int)(size.lines(1)), Color.BLACK);
        return new Vec2(FontUtils.getTextLength(fnt, text), fnt.getLineHeight());
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public CompoundTag writeNbt() {
        CompoundTag output = new CompoundTag();
        output.putString("text", text);
        return output;
    }

    @Override
    public ComponentDeserializer<?> getDeserializer() {
        return Components.LITERAL;
    }

    public static LiteralComponent readNbt(CompoundTag nbt) {
        return new LiteralComponent(nbt.getString("text"));
    }

    @Override
    public boolean canCache() {
        return true;
    }

    @Override
    public BitmapFontCache createCache(FontProvider font, Size size, Color color) {
        var fnt = font.getFont((int)(size.lines(1)), color);
        BitmapFontCache cache = fnt.newFontCache();
        cache.setText(text, 0, 0);
        return cache;
    }
}

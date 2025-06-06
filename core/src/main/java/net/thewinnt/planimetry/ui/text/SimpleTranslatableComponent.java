package net.thewinnt.planimetry.ui.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.gdxutils.FontUtils;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.util.FontProvider;
import org.jetbrains.annotations.Nullable;

public record SimpleTranslatableComponent(String key, Formatting formatting) implements Component, CharSequence {
    public SimpleTranslatableComponent(String key) {
        this(key, Formatting.EMPTY);
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @Override
    public Vec2 drawGetSize(Batch batch, FontProvider font, Size size, Color color, float x, float y) {
        GlyphLayout layout = font.getFont((int)(size.lines(1)), color).draw(batch, toString(), x, y);
        return new Vec2(layout.width, font.getFont((int)(size.lines(1)), color).getLineHeight());
    }

    @Override
    public void draw(Batch batch, FontProvider font, Size size, Color color, float x, float y) {
        font.getFont((int)(size.lines(1)), color).draw(batch, toString(), x, y);
    }

    @Override
    public Vec2 getSize(FontProvider font, Size size) {
        BitmapFont fnt = font.getFont((int)(size.lines(1)), Color.BLACK);
        return new Vec2(FontUtils.getTextLength(fnt, toString()), fnt.getLineHeight());
    }

    @Override
    public String toString() {
        return DynamicPlanimetry.translate(key);
    }

    @Override
    public CompoundTag writeNbt() {
        CompoundTag output = new CompoundTag();
        output.putString("text", key);
        return output;
    }

    @Override
    public ComponentDeserializer<?> getDeserializer() {
        return Components.SIMPLE_TRANSLATABLE;
    }

    public static SimpleTranslatableComponent readNbt(CompoundTag nbt) {
        return new SimpleTranslatableComponent(nbt.getString("key"));
    }

    @Override
    public boolean canCache() {
        return true;
    }

    @Override
    public BitmapFontCache createCache(FontProvider font, Size size, Color color) {
        var fnt = font.getFont((int)(size.lines(1)), color);
        BitmapFontCache cache = fnt.newFontCache();
        cache.setText(toString(), 0, 0);
        return cache;
    }
}

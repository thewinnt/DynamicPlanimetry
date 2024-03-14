package net.thewinnt.planimetry.ui.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.gdxutils.FontUtils;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.StyleSet.Size;
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
    public Vec2 drawGetSize(Batch batch, FontProvider font, Size size, Color color, float x, float y) {
        GlyphLayout layout = font.getFont(Gdx.graphics.getHeight() / size.factor, color).draw(batch, text, x, y);
        return new Vec2(layout.width, font.getFont(Gdx.graphics.getHeight() / size.factor, color).getLineHeight());
    }

    @Override
    public void draw(Batch batch, FontProvider font, Size size, Color color, float x, float y) {
        font.getFont(Gdx.graphics.getHeight() / size.factor, color).draw(batch, text, x, y);
    }

    @Override
    public Vec2 getSize(FontProvider font, Size size) {
        BitmapFont fnt = font.getFont(Gdx.graphics.getHeight() / size.factor, Color.BLACK);
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
        return ComponentRegistry.LITERAL;
    }

    public static LiteralComponent readNbt(CompoundTag nbt) {
        return new LiteralComponent(nbt.getString("text").getValue());
    }
}

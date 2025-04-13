package net.thewinnt.planimetry.ui.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.gdxutils.FontUtils;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.util.FontProvider;

public class ComplexTranslatableComponent implements Component, CharSequence {
    private final String key;
    private Object[] args;

    public ComplexTranslatableComponent(String key, Object... args) {
        this.key = key;
        this.args = args;
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
        return DynamicPlanimetry.translate(key, args);
    }

    public void setArgs(Object... args) {
        this.args = args;
    }

    @Override
    public CompoundTag writeNbt() {
        CompoundTag output = new CompoundTag();
        output.putString("text", key);
        return output;
    }

    @Override
    public ComponentDeserializer<?> getDeserializer() {
        return Components.COMPLEX_TRANSLATABLE;
    }

    public static ComplexTranslatableComponent readNbt(CompoundTag nbt) {
        return new ComplexTranslatableComponent(nbt.getString("key"));
    }
}

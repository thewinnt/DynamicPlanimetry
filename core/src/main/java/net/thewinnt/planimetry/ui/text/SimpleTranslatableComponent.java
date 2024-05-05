package net.thewinnt.planimetry.ui.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.gdxutils.FontUtils;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.util.FontProvider;

public record SimpleTranslatableComponent(String key) implements Component, CharSequence {
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
        GlyphLayout layout = font.getFont((int)(Gdx.graphics.getHeight() / size.getFactor()), color).draw(batch, toString(), x, y);
        return new Vec2(layout.width, font.getFont((int)(Gdx.graphics.getHeight() / size.getFactor()), color).getLineHeight());
    }

    @Override
    public void draw(Batch batch, FontProvider font, Size size, Color color, float x, float y) {
        font.getFont((int)(Gdx.graphics.getHeight() / size.getFactor()), color).draw(batch, toString(), x, y);
    }

    @Override
    public Vec2 getSize(FontProvider font, Size size) {
        BitmapFont fnt = font.getFont((int)(Gdx.graphics.getHeight() / size.getFactor()), Color.BLACK);
        return new Vec2(FontUtils.getTextLength(fnt, toString()), fnt.getLineHeight());
    }
    
    @Override
    public String toString() {
        return DynamicPlanimetry.getInstance().getCurrentLanguage().get(key);
    }

    @Override
    public CompoundTag writeNbt() {
        CompoundTag output = new CompoundTag();
        output.putString("text", key);
        return output;
    }

    @Override
    public ComponentDeserializer<?> getDeserializer() {
        return ComponentRegistry.SIMPLE_TRANSLATABLE;
    }

    public static SimpleTranslatableComponent readNbt(CompoundTag nbt) {
        return new SimpleTranslatableComponent(nbt.getString("key").getValue());
    }
}

package net.thewinnt.planimetry.ui.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.gdxutils.FontUtils;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.util.FontProvider;

public record NameComponent(byte letter, int index, short dashes) implements Component {
    public static final String[] ALLOWED_NAMES = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    public NameComponent(int letter, int index, int dashes) {
        this((byte)letter, index, (short)dashes);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(ALLOWED_NAMES[letter]);
        if (index != 0) builder.append(index);
        builder.append("'".repeat(dashes));
        return builder.toString();
    }

    @Override
    public Vec2 draw(Batch batch, FontProvider font, Size size, Color color, float x, float y) {
        BitmapFont fontMain = font.getFont(Gdx.graphics.getHeight() / size.factor, color);
        BitmapFont fontAdditional = font.getFont(Gdx.graphics.getHeight() / size.factor / 2, color);
        GlyphLayout layoutMain = fontMain.draw(batch, ALLOWED_NAMES[letter], x, y);
        float x2 = layoutMain.width + 2;
        float indexLength;
        if (index != 0) {
            fontAdditional.draw(batch, String.valueOf(index), x + x2, y - fontMain.getLineHeight() / 3);
            indexLength = FontUtils.getTextLength(fontAdditional, String.valueOf(index));
        } else {
            indexLength = 0;
        }
        fontMain.draw(batch, "'".repeat(dashes), x + x2 - 2, y + 2);
        return new Vec2(x2 + Math.max(indexLength, FontUtils.getTextLength(fontMain, "'".repeat(dashes))), fontMain.getLineHeight());
    }

    @Override
    public Vec2 getSize(FontProvider font, Size size) {
        BitmapFont fontMain = font.getFont(Gdx.graphics.getHeight() / size.factor, Theme.current().textButton());
        BitmapFont fontAdditional = font.getFont(Gdx.graphics.getHeight() / size.factor / 2, Theme.current().textButton());
        float w1 = FontUtils.getTextLength(fontMain, ALLOWED_NAMES[letter]) + 2;
        float w2;
        if (index != 0) {
            w2 = FontUtils.getTextLength(fontAdditional, String.valueOf(index));
        } else {
            w2 = 0;
        }
        float w3 = FontUtils.getTextLength(fontMain, "'".repeat(dashes));
        return new Vec2(w1 + Math.max(w2, w3), fontMain.getLineHeight());
    }

    @Override
    public CompoundTag writeNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putByte("letterId", letter);
        nbt.putInt("index", index);
        nbt.putShort("dashes", dashes);
        return nbt;
    }

    @Override
    public ComponentDeserializer<?> getDeserializer() {
        return ComponentRegistry.NAME;
    }

    public static NameComponent readNbt(CompoundTag nbt) {
        byte letter = nbt.getByte("letterId").byteValue();
        int index = nbt.getInt("index").intValue();
        short dashes = nbt.getShort("dashes").shortValue();
        return new NameComponent(letter, index, dashes);
    }
}

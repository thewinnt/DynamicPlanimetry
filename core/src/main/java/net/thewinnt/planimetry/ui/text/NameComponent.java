package net.thewinnt.planimetry.ui.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import net.thewinnt.gdxutils.FontUtils;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.util.FontProvider;

public record NameComponent(byte letter, int index, short dashes) implements Component {
    public static final String[] ALLOWED_NAMES = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(ALLOWED_NAMES[letter]);
        if (index != 0) builder.append(index);
        builder.append("'".repeat(dashes));
        return builder.toString();
    }

    @Override
    public Vec2 draw(Batch batch, FontProvider font, int fontSize, Color color, float x, float y) {
        BitmapFont fontMain = font.getFont(fontSize, color);
        BitmapFont fontAdditional = font.getFont(fontSize / 2, color);
        GlyphLayout layoutMain = fontMain.draw(batch, ALLOWED_NAMES[letter], x, y);
        float x2 = layoutMain.width + 2;
        float indexLength;
        if (index != 0) {
            fontAdditional.draw(batch, String.valueOf(index), x + x2, y - fontMain.getLineHeight() / 4);
            indexLength = FontUtils.getTextLength(fontAdditional, String.valueOf(index));
        } else {
            indexLength = 0;
        }
        fontAdditional.draw(batch, "'".repeat(dashes), x + x2, y + fontMain.getLineHeight() / 2);
        float h3 = dashes == 0 ? 0 : fontAdditional.getLineHeight();
        return new Vec2(x2 + Math.max(indexLength, FontUtils.getTextLength(fontAdditional, "'".repeat(dashes))), fontMain.getLineHeight() / 2 + h3);
    }

    @Override
    public Vec2 getSize(FontProvider font, int fontSize) {
        BitmapFont fontMain = font.getFont(fontSize, Theme.current().textButton());
        BitmapFont fontAdditional = font.getFont(fontSize / 2, Theme.current().textButton());
        float w1 = FontUtils.getTextLength(fontMain, ALLOWED_NAMES[letter]) + 2;
        float w2;
        if (index != 0) {
            w2 = FontUtils.getTextLength(fontAdditional, String.valueOf(index));
        } else {
            w2 = 0;
        }
        float w3 = FontUtils.getTextLength(fontAdditional, "'".repeat(dashes));
        float h3 = dashes == 0 ? 0 : fontAdditional.getLineHeight();
        return new Vec2(w1 + Math.max(w2, w3), fontMain.getLineHeight() / 2 + h3);
    }
}

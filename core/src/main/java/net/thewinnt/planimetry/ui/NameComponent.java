package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.util.FontProvider;

public record NameComponent(byte letter, int index, short dashes) {
    public static final String[] ALLOWED_NAMES = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    public String toString() {
        StringBuilder builder = new StringBuilder(ALLOWED_NAMES[letter]);
        if (index != 0) builder.append(index);
        builder.append("'".repeat(dashes));
        return builder.toString();
    }

    public Vec2 draw(Batch batch, FontProvider font, int fontSize, Color color, float x, float y) {
        BitmapFont fontMain = font.getFont(fontSize, color);
        BitmapFont fontAdditional = font.getFont(fontSize / 2, color);
        GlyphLayout layoutMain = fontMain.draw(batch, ALLOWED_NAMES[letter], x, y);
        float x2 = layoutMain.width + 2;
        GlyphLayout indexLayout = fontAdditional.draw(batch, String.valueOf(index), x + x2, y - fontMain.getLineHeight() / 4);
        GlyphLayout dashLayout = fontAdditional.draw(batch, "'".repeat(dashes), x + x2, y + fontMain.getLineHeight() / 2);
        return new Vec2(x2 + Math.max(indexLayout.width, dashLayout.width) + 2, dashLayout.height + fontMain.getLineHeight() / 2);
    }

    public Vec2 getSize(FontProvider font, int fontSize) {
        BitmapFont fontMain = font.getFont(fontSize, Color.BLACK);
        BitmapFont fontAdditional = font.getFont(fontSize / 2, Color.BLACK);
        BitmapFontCache cache = fontMain.getCache();
        cache.clear();
        float w1 = cache.addText(ALLOWED_NAMES[letter], 0, 0).width + 2;
        cache = fontAdditional.getCache();
        cache.clear();
        float w2 = cache.addText(String.valueOf(index), 0, 0).width;
        cache.clear();
        GlyphLayout dashLayout = cache.addText("'".repeat(dashes), 0, 0);
        return new Vec2(w1 + Math.max(w2, dashLayout.width) + 2, fontMain.getLineHeight());
    }
}

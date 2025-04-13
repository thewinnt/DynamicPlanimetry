package net.thewinnt.gdxutils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ui.Size;

public class FontUtils {
    /**
     * Centers the text around some x coordinate
     * @param x The center coordinate
     * @param font The font that the text uses
     * @param text The text itself
     * @return The coodinate to place the text at
     */
    public static int centerTextX(float x, BitmapFont font, String text) {
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, text);
        return (int) (x - layout.width / 2);
    }

    /**
     * Aligns the text to be left of the given x-coordinate
     * @param x The coordinate of the right end of the text
     * @param font The font that the text uses
     * @param text The text itself
     * @return The coodinate to place the text at
     */
    public static int rightAlignText(float x, BitmapFont font, String text) {
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, text);
        return (int) (x - layout.width);
    }

    /**
     * Centers the text around some y coordinate
     * @param y The center coordinate
     * @param font The font that the text uses
     * @param text The text itself
     * @return The coodinate to place the text at
     */
    public static int centerTextY(float y, BitmapFont font, String text) {
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, text);
        return (int) (y + layout.height / 2);
    }

    /**
     * Draws some text centered at a specific coordinate
     * @param font The font to use
     * @param batch The batch to draw the text at
     * @param text The text to draw
     * @param x The x-coordinate of the center point
     * @param y The y-coordinate of the center point
     */
    public static void drawCenteredText(BitmapFont font, Batch batch, String text, float x, float y) {
        GlyphLayout layout = new GlyphLayout(); // don't create it twice
        layout.setText(font, text);
        font.draw(batch, text, x - layout.width / 2, y + layout.height / 2);
    }

    /**
     * Draws some text aligned right of gived coordinates
     * @param font The font to use
     * @param batch The batch to draw the text at
     * @param text The text to draw
     * @param x The x-coordinate of the right point
     * @param y The y-coordinate of the center point
     */
    public static void drawRightAlignedText(BitmapFont font, Batch batch, String text, float x, float y) {
        GlyphLayout layout = new GlyphLayout(); // don't create it twice
        layout.setText(font, text);
        font.draw(batch, text, x - layout.width, y + layout.height / 2);
    }

    /**
     * Draws some text centered at a specific coordinate
     * @param font The font to use
     * @param batch The batch to draw the text at
     * @param text The text to draw
     * @param x The x-coordinate of the left point
     * @param y The y-coordinate of the center point
     */
    public static void drawYCenteredText(BitmapFont font, Batch batch, String text, float x, float y) {
        font.draw(batch, text, x, centerTextY(y, font, text));
    }

    /**
     * Draws some text centered at a specific coordinate and scaled by some factor
     * @param font The font to use
     * @param batch The batch to draw the text at
     * @param text The text to draw
     * @param x The x-coordinate of the center point
     * @param y The y-coordinate of the center point
     * @param scale The scale of the text
     */
    public static void drawScaledCenteredText(BitmapFont font, Batch batch, String text, float x, float y, float scale) {
        float old_scale_x = font.getData().scaleX;
        float old_scale_y = font.getData().scaleY;
        font.getData().setScale(scale);
        drawCenteredText(font, batch, text, x, y);
        font.getData().setScale(old_scale_x, old_scale_y);
    }

    /**
     * Returns the length of the text for the specified font in pixels
     * @param font The font to use for calculations
     * @param text The text to get the length of
     * @return The length of the text
     */
    public static float getTextLength(BitmapFont font, String text) {
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, text);
        return layout.width;
    }
}

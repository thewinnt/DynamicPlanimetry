package net.thewinnt.gdxutils;

import com.badlogic.gdx.graphics.Color;

public class ColorUtils {

    /**
     * Returns a new {@link Color} from the given RGB components.
     * @param r Red, [0-255]
     * @param g Green, [0-255]
     * @param b Blue, [0-255]
     * @return The Color from given components. If the values are out of range, they're clamped
     * in range [0-255]
     */
    public static Color rgbColor(int r, int g, int b) {
        return new Color(r / 255f, g / 255f, b / 255f, 1);
    }

    /**
     * Returns a new {@link Color} from the given RGB components.
     * @param r Red, [0-255]
     * @param g Green, [0-255]
     * @param b Blue, [0-255]
     * @param a Alpha, [0-255]
     * @return The Color from given components. If the values are out of range, they're clamped
     * in range [0-255]
     */
    public static Color rgbColor(int r, int g, int b, int a) {
        return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    /**
     * Does a Color.lerp() but with a range check. If {@code d} is NaN, it's treated as 1.
     * @param c1 Starting color
     * @param c2 End color
     * @param d How much to interpolate
     * @return A new Color
     * @see #lerpn(Color, Color, float)
     */
    public static Color lerp(Color c1, Color c2, float d) {
        if (Float.isNaN(d)) {
            d = 1;
        } else if (d < 0) {
            d = 0;
        } else if (d > 1) {
            d = 1;
        }
        return c1.cpy().lerp(c2, d);
    }

    /**
     * Does a Color.lerp() but with a range check. If {@code d} is NaN, it's treated as 0.
     * @param c1 Starting color
     * @param c2 End color
     * @param d How much to interpolate
     * @return A new Color
     * @see #lerp(Color, Color, float)
     */
    public static Color lerpn(Color c1, Color c2, float d) {
        if (Float.isNaN(d)) {
            d = 0;
        } else if (d < 0) {
            d = 0;
        } else if (d > 1) {
            d = 1;
        }
        return c1.cpy().lerp(c2, d);
    }
}

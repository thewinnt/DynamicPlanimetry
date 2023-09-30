package net.thewinnt.planimetry.shapes;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import net.thewinnt.planimetry.math.Vec2;
import space.earlygrey.shapedrawer.ShapeDrawer;

public interface Shape {
    boolean contains(Vec2 point);
    boolean contains(double x, double y);

    /**
     * Renders the shape
     * @param drawer the {@link ShapeDrawer} to draw the shape with
     * @param selected whether the shape's selected
     * @param font the font to use for additional data
     * @param scale the scale of the board, in units per pixel
     */
    void render(ShapeDrawer drawer, boolean selected, BitmapFont font, double scale, Vec2 offset);

    default boolean shouldRender() {
        return true;
    }
}

package net.thewinnt.planimetry.shapes;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public interface Shape {
    boolean contains(Vec2 point);
    boolean contains(double x, double y);
    boolean containsRough(Vec2 point);
    boolean containsRough(double x, double y);

    /**
     * Renders the shape
     * @param drawer the {@link ShapeDrawer} to draw the shape with
     * @param selected whether the shape's selected
     * @param font the font to use for additional data
     * @param board the board to gather data from (and perform coordinate conversions)
     */
    void render(ShapeDrawer drawer, boolean selected, FontProvider font, DrawingBoard board);

    default boolean shouldRender() {
        return true;
    }
}

package net.thewinnt.planimetry.shapes;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public interface Shape {
    boolean contains(Vec2 point);
    boolean contains(double x, double y);
    double distanceToMouse(Vec2 point, DrawingBoard board);
    double distanceToMouse(double x, double y, DrawingBoard board);

    /**
     * Renders the shape
     * @param drawer the {@link ShapeDrawer} to draw the shape with
     * @param selection whether the shape's selected
     * @param font the font to use for additional data
     * @param board the board to gather data from (and perform coordinate conversions)
     */
    void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board);

    default boolean shouldRender() {
        return true;
    }

    public static enum SelectionStatus {
        NONE,
        HOVERED,
        SELECTED
    }
}

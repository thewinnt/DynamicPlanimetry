package net.thewinnt.planimetry.shapes;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class Shape {
    private static long idCounter;
    private final long id = idCounter++;

    public abstract boolean contains(Vec2 point);
    public abstract boolean contains(double x, double y);
    public abstract double distanceToMouse(Vec2 point, DrawingBoard board);
    public abstract double distanceToMouse(double x, double y, DrawingBoard board);

    /**
     * Renders the shape
     * @param drawer the {@link ShapeDrawer} to draw the shape with
     * @param selection whether the shape's selected
     * @param font the font to use for additional data
     * @param board the board to gather data from (and perform coordinate conversions)
     */
    public abstract void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board);

    public boolean shouldRender() {
        return true;
    }

    public final long getId() {
        return id;
    }

    protected float getThickness(double scale) {
        return (float)Math.min(Math.max(1, Math.cbrt(scale)), 4);
    }

    public static enum SelectionStatus {
        NONE,
        HOVERED,
        SELECTED
    }
}

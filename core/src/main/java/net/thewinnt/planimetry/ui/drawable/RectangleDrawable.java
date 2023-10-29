package net.thewinnt.planimetry.ui.drawable;

import com.badlogic.gdx.graphics.Color;

import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable;

public class RectangleDrawable extends ShapeDrawerDrawable {
    public Color inColor = new Color(0.95f, 0.95f, 0.95f, 1);
    public Color outColor = new Color(0, 0, 0, 1);
    public float lineWidth = 2;

    public RectangleDrawable(ShapeDrawer drawer) {
        super(drawer);
    }

    @Override
    public void drawShapes(ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
        if (inColor != null) shapeDrawer.filledRectangle(x + lineWidth/2, y + lineWidth/2, width - lineWidth, height - lineWidth, inColor);
        if (outColor != null) shapeDrawer.rectangle(x, y, width, height, outColor, lineWidth);
    }

    /**
     * Sets the colors of the drawable, mutating it in the process.
     * @param in the inner color. May be null.
     * @param out the outline color. May be null.
     * @return this instance for chaining
     */
    public RectangleDrawable withColors(Color in, Color out) {
        inColor = in;
        outColor = out;
        return this;
    }

    public RectangleDrawable withLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        return this;
    }
}
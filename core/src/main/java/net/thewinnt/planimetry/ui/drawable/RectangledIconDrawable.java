package net.thewinnt.planimetry.ui.drawable;

import com.badlogic.gdx.graphics.Color;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class RectangledIconDrawable extends RectangleDrawable {
    private final DynamicIcon icon;
    private Color iconColor;

    public RectangledIconDrawable(ShapeDrawer drawer, DynamicIcon icon) {
        super(drawer);
        this.icon = icon;
    }

    public RectangledIconDrawable(ShapeDrawer drawer, DynamicIcon icon, Color iconColor) {
        super(drawer);
        this.icon = icon;
        this.iconColor = iconColor;
    }

    @Override
    public void drawShapes(ShapeDrawer drawer, float x, float y, float width, float height) {
        super.drawShapes(drawer, x, y, width, height);
        float old = drawer.getPackedColor();
        drawer.setColor(iconColor);
        icon.drawShapes(drawer, x, y, width, height);
        drawer.setColor(old);
    }

    @Override
    public RectangleDrawable withColors(Color in, Color out) {
        this.iconColor = out;
        return super.withColors(in, out);
    }
}

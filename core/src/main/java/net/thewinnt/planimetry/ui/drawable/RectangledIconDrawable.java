package net.thewinnt.planimetry.ui.drawable;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class RectangledIconDrawable extends RectangleDrawable {
    private DynamicIcon icon;

    public RectangledIconDrawable(ShapeDrawer drawer, DynamicIcon icon) {
        super(drawer);
        this.icon = icon;
    }

    @Override
    public void drawShapes(ShapeDrawer drawer, float x, float y, float width, float height) {
        super.drawShapes(drawer, x, y, width, height);
        float old = drawer.getPackedColor();
        drawer.setColor(outColor);
        icon.drawShapes(drawer, x, y, width, height);
        drawer.setColor(old);
    }

    public DynamicIcon getIcon() {
        return icon;
    }
    
    public void setIcon(DynamicIcon icon) {
        this.icon = icon;
    }
}

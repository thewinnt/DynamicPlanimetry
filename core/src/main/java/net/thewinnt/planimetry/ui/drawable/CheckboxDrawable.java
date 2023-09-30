package net.thewinnt.planimetry.ui.drawable;

import com.badlogic.gdx.graphics.Color;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class CheckboxDrawable extends RectangleDrawable {
    public CheckboxDrawable(ShapeDrawer drawer) {
        super(drawer);
    }

    @Override
    public void drawShapes(ShapeDrawer drawer, float x, float y, float width, float height) {
        super.drawShapes(drawer, x, y, width, height);
        float oldColor = drawer.getPackedColor();
        drawer.setColor(Color.BLACK);
        drawer.path(new float[]{x + width / 4, y + height * 3/7, x + width / 2, y + height / 6, x + width * 5/7, y + height * 5/6}, lineWidth, JoinType.SMOOTH, true);
        drawer.setColor(oldColor);
    }
}

package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.drawable.DynamicIcon;
import net.thewinnt.planimetry.ui.text.Component;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ListSwitch extends Button {
    private final ShapeDrawer drawer;

    public ListSwitch(Component name, StyleSet styles, Size size) {
        super(styles.getButtonStyle(size, true));
        this.add(new ComponentLabel(name, styles.font, size)).left().fill().expand().padLeft(getPrefHeight());
        invalidate();
        this.drawer = styles.drawer;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (isChecked()) {
            DynamicIcon.DOWN_TRIANGLE.drawShapes(drawer, getX(), getY(), getHeight(), getHeight());
        } else {
            DynamicIcon.RIGHT_TRIANGLE.drawShapes(drawer, getX(), getY(), getHeight(), getHeight());
        }
    }
}

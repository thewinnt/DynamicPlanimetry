package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.thewinnt.planimetry.DynamicPlanimetry;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ShapeSettingsBackground extends Actor {
    public final ShapeDrawer drawer;
    public final Table creation;
    public final Table properties;

    public ShapeSettingsBackground(ShapeDrawer drawer, Table creation, Table properties) {
        this.drawer = drawer;
        this.creation = creation;
        this.properties = properties;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawer.filledRectangle(getX(), getY(), getWidth(), getHeight(), DynamicPlanimetry.COLOR_BUTTON);
        drawer.line(getX(), creation.getY() - 5, getX() + getWidth(), creation.getY() - 5, DynamicPlanimetry.COLOR_DELIMITER);
        drawer.line(getX(), properties.getY() - 5, getX() + getWidth(), properties.getY() - 5, DynamicPlanimetry.COLOR_DELIMITER);
        drawer.line(getX(), getY(), getX(), getY() + getHeight(), Color.BLACK, 2);
    }
}

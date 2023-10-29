package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.thewinnt.planimetry.DynamicPlanimetry;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ShapeSettings extends Actor {
    public final ShapeDrawer drawer;
    public final Table creation;
    public final Table properties;

    public ShapeSettings(ShapeDrawer drawer, Table creation, Table properties) {
        this.drawer = drawer;
        this.creation = creation;
        this.properties = properties;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawer.filledRectangle(getX(), getY(), getWidth(), getHeight(), DynamicPlanimetry.COLOR_BUTTON);
        drawer.line(getX(), creation.getY(), getX() + getWidth(), creation.getY(), DynamicPlanimetry.COLOR_DELIMITER);
        drawer.line(getX(), properties.getY(), getX() + getWidth(), properties.getY(), DynamicPlanimetry.COLOR_DELIMITER);
        drawer.line(getX(), getY(), getX(), getY() + getHeight(), Color.BLACK, 2);
    }
}

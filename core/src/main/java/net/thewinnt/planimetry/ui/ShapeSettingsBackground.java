package net.thewinnt.planimetry.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class ShapeSettingsBackground extends Actor {
    public final ShapeDrawer drawer;
    public final ScrollPane creation;
    public final ScrollPane properties;

    public ShapeSettingsBackground(ShapeDrawer drawer, ScrollPane creation, ScrollPane properties) {
        this.drawer = drawer;
        this.creation = creation;
        this.properties = properties;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawer.filledRectangle(getX(), getY(), getWidth(), getHeight(), Theme.current().button());
        drawer.line(getX(), creation.getY() - 5, getX() + getWidth(), creation.getY() - 5, Theme.current().delimiter());
        drawer.line(getX(), properties.getY() - 5, getX() + getWidth(), properties.getY() - 5, Theme.current().delimiter());
        drawer.line(getX(), getY(), getX(), getY() + getHeight(), Theme.current().outline(), 2);
    }
}

package net.thewinnt.planimetry.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.Vec2Property;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ShapeSettings extends Table {
    public final DrawingBoard board;
    public final ShapeDrawer drawer;
    public final StyleSet styles;
    private final List<Actor> createButtons = new ArrayList<>();
    private final List<Property<?>> selectionProperties = new ArrayList<>();
    private final List<Actor> selectionFunctions = new ArrayList<>();
    private boolean thrown = false;

    public ShapeSettings(DrawingBoard board, ShapeDrawer drawer, StyleSet styles) {
        this.board = board;
        this.drawer = drawer;
        this.styles = styles;
        this.createButtons.add(new Label("Work in progress...", styles.getLabelStyle()));
        this.selectionProperties.add(new Vec2Property("Test property", styles, new Vec2(50, 43.25)));
        this.setDebug(true, true);
    }

    @Override
    public void validate() {
        this.clearChildren();
        Table groupA = new Table();
        Table groupB = new Table();
        Table groupC = new Table();
        for (Actor i : createButtons) {
            groupA.add(i).expandX().fillX().pad(5, 5, 0, 5);
            groupA.row();
        }
        for (Property<?> i : selectionProperties) {
            var parameters = i.getParameters();
            parameters.forEach((parameter, name) -> {
                groupB.add(new Label(name, styles.getLabelStyle())).padLeft(5).padTop(10);
                groupB.add(parameter.getActorSetup()).padLeft(10).padTop(10).expandY().fillY();
                groupB.row();
            });
            groupB.row();
        }
        for (Actor i : selectionFunctions) {
            groupC.add(i).expandX().fillX().pad(5, 5, 0, 5);
            groupC.row();
        }
        this.add(groupA).top().left().fill();
        this.row();
        this.add(groupB).top().left().fill();
        this.row();
        this.add(groupC).top().left().fill();
        super.validate();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        validate();
        drawer.filledRectangle(getX(), getY(), getWidth(), getHeight(), DynamicPlanimetry.COLOR_BUTTON);
        try {
            drawer.line(getX(), this.getChild(1).getY(), getX() + getWidth(), this.getChild(1).getY(), DynamicPlanimetry.COLOR_DELIMITER);
            drawer.line(getX(), this.getChild(2).getY(), getX() + getWidth(), this.getChild(2).getY(), DynamicPlanimetry.COLOR_DELIMITER);
        } catch (IndexOutOfBoundsException e) {
            if (!thrown) {
                Notifications.addNotification(e.getMessage(), 5000);
                Notifications.addNotification("[DEBUG] children: " + this.getChildren().size, 5000);
                thrown = true;
            }
        }
        drawer.line(getX(), getY(), getX(), getY() + getHeight(), Color.BLACK, 2);
        drawChildren(batch, parentAlpha);
    }
}

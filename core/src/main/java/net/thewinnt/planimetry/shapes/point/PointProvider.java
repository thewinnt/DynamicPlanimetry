package net.thewinnt.planimetry.shapes.point;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Color;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.NameComponent;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class PointProvider extends Shape {
    protected final List<Consumer<Vec2>> movementListeners = new ArrayList<>();
    protected NameComponent name;

    public PointProvider(Drawing drawing) {
        super(drawing);
        if (this.shouldAutoAssingnName()) {
            this.name = drawing.generateName(drawing.shouldUseDashesForNaming());
        }
    }

    public PointProvider(Drawing drawing, NameComponent name) {
        super(drawing);
        this.name = name;
    }

    public abstract Vec2 getPosition();
    public abstract boolean canMove();
    public abstract void move(Vec2 delta);
    public abstract void move(double dx, double dy);

    public double getX() {
        return getPosition().x;
    }
    public double getY() {
        return getPosition().y;
    }

    @Override
    public boolean contains(Vec2 point) {
        return getPosition().equals(point);
    }

    @Override
    public boolean contains(double x, double y) {
        return getPosition().x == x && getPosition().y == y;
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        return getPosition().distanceTo(point);
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return getPosition().distanceTo(x, y);
    }

    public void addMovementListener(Consumer<Vec2> listener) {
        this.movementListeners.add(listener);
    }

    public boolean removeMovementListener(Consumer<Vec2> listener) {
        return this.movementListeners.remove(listener);
    }

    @Override
    public Component getName() {
        return Component.of(Component.literal(getTypeName()), name);
    }

    public NameComponent getNameComponent() {
        return name;
    }

    @Override
    public String getTypeName() {
        return "Точка ";
    }

    public void setName(NameComponent name) {
        this.name = name;
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        if (!board.hasShape(this)) {
            drawer.setColor(switch (selection) {
                default -> Theme.current().utilityPoint();
                case HOVERED -> Theme.current().utilityPointHovered();
                case SELECTED -> Theme.current().utilityPointSelected();
            });
            drawer.circle(board.bx(getPosition().x), board.by(getPosition().y), this.getThickness(board.getScale()) * 2, 2);
        } else {
            Color color = switch (selection) {
                default -> Theme.current().point();
                case HOVERED -> Theme.current().pointHovered();
                case SELECTED -> Theme.current().pointSelected();
            };
            drawer.filledCircle(board.boardToGlobal(getPosition()).toVector2f(), this.getThickness(board.getScale()) * 2, color);
        }
    }

    protected abstract boolean shouldAutoAssingnName();
}

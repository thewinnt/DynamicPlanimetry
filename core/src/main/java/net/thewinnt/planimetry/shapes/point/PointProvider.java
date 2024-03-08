package net.thewinnt.planimetry.shapes.point;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Color;

import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.NameComponent;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class PointProvider extends Shape {
    protected final List<Consumer<Vec2>> movementListeners = new ArrayList<>();
    protected NameComponent name;
    public boolean shouldRender = true;

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

    public double getX() {
        return getPosition().x;
    }
    public double getY() {
        return getPosition().y;
    }

    @Override
    public boolean contains(Vec2 point) {
        return MathHelper.roughlyEquals(point, getPosition());
    }

    @Override
    public boolean contains(double x, double y) {
        return MathHelper.roughlyEquals(getPosition().x, x) && MathHelper.roughlyEquals(getPosition().y, y);
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
    public boolean shouldRender() {
        return shouldRender;
    }

    @Override
    public Component getName() {
        if (nameOverride != null) return nameOverride;
        return Component.translatable(getTypeName(), name);
    }

    public Component getNameComponent() {
        if (nameOverride != null) return Component.empty();
        return name;
    }

    @Override
    public String getTypeName() {
        return "shape.point";
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
        if (this.name != null) {
            Vec2 neededSpace = this.name.getSize(font, Size.MEDIUM).mul(0.5);
            double minRadius = (Math.max(neededSpace.x, neededSpace.y)) / board.getScale();
            Vec2 start = getPosition();
            double bestSpace = 0;
            // double _worstSpace = Double.MAX_VALUE;
            Vec2 bestPos = null;
            for (double angle = 0; angle < Math.PI; angle += 0.4) {
                Vec2 test = MathHelper.continueFromAngle(start, angle, minRadius);
                double space = board.getFreeSpace(test.x, test.y);
                if (space >= bestSpace) {
                    bestSpace = space;
                    bestPos = test;
                }
                // if (space <= _worstSpace) {
                //     _worstSpace = space;
                // }
            }
            for (double angle = 0; angle < Math.PI; angle += 0.4) {
                Vec2 test = MathHelper.continueFromAngle(start, angle, -minRadius);
                double space = board.getFreeSpace(test.x, test.y);
                if (space >= bestSpace) {
                    bestSpace = space;
                    bestPos = test;
                }
                // if (space <= _worstSpace) {
                //     _worstSpace = space;
                // }
            }
            // for (double angle = 0; angle < Math.PI; angle += 0.4) {
            //     Vec2 test = MathHelper.continueFromAngle(start, angle, -minRadius);
            //     double space = board.getFreeSpace(test.x, test.y);
            //     drawer.filledCircle(board.boardToGlobal(test).toVector2f(), 2, Color.RED.cpy().lerp(Color.GREEN, (float)((space - _worstSpace) / (bestSpace - _worstSpace))));
            //     test = MathHelper.continueFromAngle(start, angle, minRadius);
            //     space = board.getFreeSpace(test.x, test.y);
            //     drawer.filledCircle(board.boardToGlobal(test).toVector2f(), 2, Color.RED.cpy().lerp(Color.GREEN, (float)((space - _worstSpace) / (bestSpace - _worstSpace))));
            // }
            if (bestPos == null) bestPos = MathHelper.continueFromAngle(start, 90, -minRadius);
            name.draw(drawer.getBatch(), font, Size.MEDIUM, Theme.current().textUI(), (float)board.bx(bestPos.x), (float)(board.by(bestPos.y) + neededSpace.y / 2));
        }
    }

    protected abstract boolean shouldAutoAssingnName();
}

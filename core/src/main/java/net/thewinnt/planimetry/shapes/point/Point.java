package net.thewinnt.planimetry.shapes.point;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.Vec2Property;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Point extends PointProvider {
    private Vec2 position;
    private final Vec2Property property;

    public Point(Vec2 position) {
        this.position = position;
        this.property = new Vec2Property("Координаты", position);
        this.property.addValueChangeListener(() -> {
            Point.this.position = property.buildResult();
        });
    }

    @Override
    public Vec2 getPosition() {
        return position;
    }

    @Override
    public boolean contains(Vec2 point) {
        return point.equals(position);
    }

    @Override
    public boolean contains(double x, double y) {
        return position.x == x && position.y == y;
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        if (!board.hasShape(this)) {
            drawer.setColor(switch (selection) {
                default -> DynamicPlanimetry.COLOR_UTIL_POINT;
                case HOVERED -> DynamicPlanimetry.COLOR_UTIL_POINT_HOVER;
                case SELECTED -> DynamicPlanimetry.COLOR_UTIL_POINT_SELECTED;
            });
            drawer.circle(board.bx(position.x), board.by(position.y), (float)Math.min(Math.max(2, board.getScale()), 8), 2);
        } else {
            Color color = switch (selection) {
                default -> DynamicPlanimetry.COLOR_POINT;
                case HOVERED -> DynamicPlanimetry.COLOR_POINT_HOVER;
                case SELECTED -> DynamicPlanimetry.COLOR_POINT_SELECTED;
            };
            drawer.filledCircle(board.boardToGlobal(position).toVector2f(), (float)Math.min(Math.max(2, board.getScale()), 8), color);
        }
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        return point.distanceToSqr(position);
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return position.distanceToSqr(x, y);
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public void move(Vec2 delta) {
        position = position.add(delta);
        this.movementListeners.forEach(i -> i.accept(delta));
    }

    @Override
    public void move(double dx, double dy) {
        position = position.add(dx, dy);
        this.movementListeners.forEach(i -> i.accept(new Vec2(dx, dy)));
    }

    @Override
    public Collection<Property<?>> getProperties() {
        return List.of(property);
    }
}

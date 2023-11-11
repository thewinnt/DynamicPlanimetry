package net.thewinnt.planimetry.shapes;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import com.badlogic.gdx.graphics.Color;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.DoubleProperty;
import net.thewinnt.planimetry.ui.properties.EnclosingProperty;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Circle extends Shape {
    public final PointProvider center;
    private boolean keepRadius = false;
    public Supplier<Double> radius;
    private PointProvider radiusPoint;

    public Circle(PointProvider center, double radius) {
        this.center = center;
        this.radius = () -> radius;
    }

    public Circle(PointProvider center, PointProvider radius, boolean keepRadius) {
        this.center = center;
        this.radius = () -> radius.getPosition().distanceTo(center.getPosition());
        this.keepRadius = keepRadius;
        this.setRadiusPoint(radius);
    }

    @Override
    public boolean contains(double x, double y) {
        return center.getPosition().distanceTo(x, y) < Math.pow(2, -23);
    }

    @Override
    public boolean contains(Vec2 point) {
        return center.getPosition().distanceTo(point) < Math.pow(2, -23);
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        return Math.abs(center.getPosition().distanceTo(point) - radius.get());
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return Math.abs(center.getPosition().distanceTo(x, y) - radius.get());
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        Vec2 center = this.center.getPosition();
        Color lineColor = switch (selection) {
            default -> DynamicPlanimetry.COLOR_SHAPE;
            case HOVERED -> DynamicPlanimetry.COLOR_SHAPE_HOVER;
            case SELECTED -> DynamicPlanimetry.COLOR_SHAPE_SELECTED;
        };
        drawer.setColor(lineColor);
        drawer.circle(board.bx(center.x), board.by(center.y), (float)(radius.get() * board.getScale()), getThickness(board.getScale()));
    }

    public void setRadiusPoint(PointProvider point) {
        if (this.radiusPoint != null) this.center.removeMovementListener(this.radiusPoint::move);
        if (this.keepRadius) this.center.addMovementListener(point::move);
        this.radiusPoint = point;
        this.radius = () -> point.getPosition().distanceTo(center.getPosition());
    }

    public boolean getKeepRadius() {
        return keepRadius;
    }

    public void setKeepRadius(boolean keepRadius) {
        if (this.keepRadius != keepRadius) {
            if (keepRadius) {
                this.center.addMovementListener(this.radiusPoint::move);
            } else if (this.radiusPoint != null) {
                this.center.removeMovementListener(this.radiusPoint::move);
            }
        }
        this.keepRadius = keepRadius;
    }

    @Override
    public Collection<Property<?>> getProperties() {
        if (radiusPoint != null) {
            return List.of(
                new EnclosingProperty("Центр", this.center.getProperties()),
                new EnclosingProperty("Точка радиуса", this.radiusPoint.getProperties())
            );
        } else {
            DoubleProperty radius = new DoubleProperty("Радиус", this.radius.get());
            radius.addValueChangeListener(r -> Circle.this.radius = () -> r);
            return List.of(new EnclosingProperty("Центр", this.center.getProperties()), radius);
        }
    }

    @Override
    public String getName() {
        return this.center.getName();
    }

    @Override
    public String getTypeName() {
        return "Окружность";
    }
}

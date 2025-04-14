package net.thewinnt.planimetry.definition.point.placement;

import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.definition.point.type.CirclePlacementType;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Circle;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.ShapeProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.DynamicValue;

import java.util.Collection;
import java.util.List;

public class CirclePlacement extends PointPlacement {
    private Circle circle;
    private DynamicValue angle;

    public CirclePlacement(Circle circle, DynamicValue angle) {
        this.circle = circle;
        this.angle = angle;
    }

    @Override
    public Vec2 get() {
        return MathHelper.continueFromAngle(circle.center.getPosition(), angle.get(), circle.getRadius());
    }

    @Override
    public void move(Vec2 delta) {
        Vec2 newPos = get().add(delta);
        double newAngle = MathHelper.angleTo(circle.center.getPosition(), newPos);
        this.angle = angle.add(newAngle - angle.get());
    }

    @Override
    public void move(double dx, double dy) {
        move(new Vec2(dx, dy));
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public Collection<Property<?>> properties() {
        PointProvider src = getSource();
        return List.of(
            PropertyHelper.setter(new ShapeProperty(Component.translatable(src.getPropertyName("circle")), src.getDrawing(), circle, s -> s instanceof Circle c && c.getRadiusPoint() != src), t -> circle = (Circle) t),
            PropertyHelper.dynamicValue(angle, t -> angle = t, src.getPropertyName("angle"))
        );
    }

    @Override
    public PointPlacementType<?> type() {
        return CirclePlacementType.INSTANCE; // TODO circle placement
    }

    @Override
    public List<Shape> dependencies() {
        return List.of(circle);
    }
}

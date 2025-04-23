package net.thewinnt.planimetry.definition.point.placement;

import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.definition.point.type.OffsetPlacementType;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.DynamicValue;

import java.util.Collection;
import java.util.List;

public class AngleOffsetPlacement extends PointPlacement {
    private PointProvider point;
    private DynamicValue angle;
    private DynamicValue offset;

    public AngleOffsetPlacement(PointProvider point, DynamicValue angle, DynamicValue offset) {
        this.point = point;
        this.angle = angle;
        this.offset = offset;
    }

    @Override
    public Vec2 get() {
        if (point == getSource()) return null;
        return MathHelper.continueFromAngle(point.getPosition(), angle.get(), offset.get());
    }

    @Override
    public void move(Vec2 delta) {
        Vec2 vec2 = get();
        if (vec2 == null) return;
        Vec2 newPos = vec2.add(delta);
        double newAngle = MathHelper.angleTo(point.getPosition(), newPos);
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
        return List.of(
            PropertyHelper.swappablePoint(point, t -> point = t, List.of(getSource()), false, "shape.generic.point"),
            PropertyHelper.dynamicValue(angle, t -> angle = t, point.getPropertyName("direction")),
            PropertyHelper.dynamicValue(offset, t -> offset = t, point.getPropertyName("angle")),
            new DisplayProperty(Component.translatable(source.getPropertyName("coordinates")), () -> get().toComponent())
        );
    }

    @Override
    public PointPlacementType<?> type() {
        return OffsetPlacementType.INSTANCE;
    }

    @Override
    public List<Shape> dependencies() {
        return List.of(point);
    }

    public PointProvider getPoint() {
        return point;
    }

    public DynamicValue getAngle() {
        return angle;
    }

    public DynamicValue getOffset() {
        return offset;
    }
}

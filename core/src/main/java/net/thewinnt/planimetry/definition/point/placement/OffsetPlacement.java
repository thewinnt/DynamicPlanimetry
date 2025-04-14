package net.thewinnt.planimetry.definition.point.placement;

import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.definition.point.type.OffsetPlacementType;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.value.DynamicValue;

import java.util.Collection;
import java.util.List;

public class OffsetPlacement extends PointPlacement {
    private PointProvider point;
    private DynamicValue offsetX;
    private DynamicValue offsetY;

    public OffsetPlacement(PointProvider point, DynamicValue offsetX, DynamicValue offsetY) {
        this.point = point;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public Vec2 get() {
        if (point == getSource()) return null;
        return point.getPosition().add(offsetX.get(), offsetY.get());
    }

    @Override
    public void move(Vec2 delta) {
        this.offsetX = offsetX.add(delta.x);
        this.offsetY = offsetY.add(delta.y);
    }

    @Override
    public void move(double dx, double dy) {
        this.offsetX = offsetX.add(dx);
        this.offsetY = offsetY.add(dy);
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public Collection<Property<?>> properties() {
        return List.of(
            PropertyHelper.swappablePoint(point, t -> point = t, List.of(getSource()), false, "shape.generic.point"),
            PropertyHelper.dynamicValue(offsetX, t -> offsetX = t, point.getPropertyName("offset_x")),
            PropertyHelper.dynamicValue(offsetY, t -> offsetY = t, point.getPropertyName("offset_y"))
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

    public DynamicValue getOffsetX() {
        return offsetX;
    }

    public DynamicValue getOffsetY() {
        return offsetY;
    }
}

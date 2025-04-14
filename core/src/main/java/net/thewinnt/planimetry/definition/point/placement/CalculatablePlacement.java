package net.thewinnt.planimetry.definition.point.placement;

import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.definition.point.type.CalculatablePlacementType;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.value.DynamicValue;

import java.util.Collection;
import java.util.List;

public class CalculatablePlacement extends PointPlacement {
    private DynamicValue x;
    private DynamicValue y;

    public CalculatablePlacement(DynamicValue x, DynamicValue y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Vec2 get() {
        return new Vec2(x.get(), y.get());
    }

    @Override
    public void move(Vec2 delta) {
        this.x = x.add(delta.x);
        this.y = y.add(delta.y);
    }

    @Override
    public void move(double dx, double dy) {
        this.x = x.add(dx);
        this.y = y.add(dy);
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public Collection<Property<?>> properties() {
        return List.of(
            PropertyHelper.dynamicValue(x, t -> x = t, source.getPropertyName("x")),
            PropertyHelper.dynamicValue(y, t -> y = t, source.getPropertyName("y"))
        );
    }

    @Override
    public PointPlacementType<?> type() {
        return CalculatablePlacementType.INSTANCE;
    }

    @Override
    public List<Shape> dependencies() {
        return List.of();
    }

    public DynamicValue getX() {
        return x;
    }

    public DynamicValue getY() {
        return y;
    }
}

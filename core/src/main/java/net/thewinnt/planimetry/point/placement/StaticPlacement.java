package net.thewinnt.planimetry.point.placement;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.point.PointPlacement;
import net.thewinnt.planimetry.point.PointPlacementType;
import net.thewinnt.planimetry.point.ValueContext;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.Vec2Property;

import java.util.Collection;
import java.util.List;

public class StaticPlacement implements PointPlacement {
    private Vec2 pos;

    public StaticPlacement(Vec2 pos) {
        this.pos = pos;
    }

    @Override
    public Vec2 get(ValueContext context) {
        return pos;
    }

    @Override
    public Collection<Property<?>> properties() {
        return List.of(PropertyHelper.setter(new Vec2Property(type().property("position"), pos), vec2 -> pos = vec2));
    }

    @Override
    public PointPlacementType<?> type() {
        return PointPlacementType.STATIC;
    }
}

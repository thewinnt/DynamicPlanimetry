package net.thewinnt.planimetry.definition.point.placement;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.Vec2Property;

import java.util.Collection;
import java.util.List;

public class StaticPlacement extends PointPlacement {
    private Vec2 pos;

    public StaticPlacement(Vec2 pos) {
        this.pos = pos;
    }

    @Override
    public void move(Vec2 delta) {
        this.pos = pos.add(delta);
    }

    @Override
    public void move(double dx, double dy) {
        this.pos = pos.add(dx, dy);
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public Vec2 get() {
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

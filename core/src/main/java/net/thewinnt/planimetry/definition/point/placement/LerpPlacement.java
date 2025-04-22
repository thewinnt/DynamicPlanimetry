package net.thewinnt.planimetry.definition.point.placement;

import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.ConstantValue;

import java.util.Collection;
import java.util.List;

public class LerpPlacement extends PointPlacement {
    private PointProvider a;
    private PointProvider b;
    private DynamicValue delta;

    @Override
    public Vec2 get() {
        return a.getPosition().lerp(b.getPosition(), delta.get());
    }

    @Override
    public void move(Vec2 delta) {
        double currentD = this.delta.get();
        Vec2 newPos = get().add(delta);
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        Vec2 projection = MathHelper.project(newPos, a, b);
        double newD = a.distanceTo(projection) / a.distanceTo(b);
        this.delta = this.delta.add(newD - currentD);
    }

    @Override
    public void move(double dx, double dy) {

    }

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public Collection<Property<?>> properties() {
        return List.of();
    }

    @Override
    public PointPlacementType<?> type() {
        return null;
    }

    @Override
    public List<Shape> dependencies() {
        return List.of();
    }
}

package net.thewinnt.planimetry.definition.point.placement;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.definition.point.type.LerpPlacementType;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.ConstantValue;

import java.util.Collection;
import java.util.List;

public class LerpPlacement extends PointPlacement {
    private PointProvider a;
    private PointProvider b;
    private DynamicValue delta;

    public LerpPlacement(PointProvider a, PointProvider b, DynamicValue delta) {
        this.a = a;
        this.b = b;
        this.delta = delta;
    }

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
        double newD;
        if (a.distanceToSqr(projection) > b.distanceToSqr(projection)) { // projection closer to b
            newD = a.distanceTo(projection) / a.distanceTo(b);
        } else { // projection closer to a
            newD = 1 - (b.distanceTo(projection) / b.distanceTo(a));
        }
        this.delta = this.delta.add(newD - currentD);
        DynamicPlanimetry.getInstance().editorScreen.show();
    }

    @Override
    public void move(double dx, double dy) {
        this.move(new Vec2(dx, dy));
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public Collection<Property<?>> properties() {
        return List.of(
            PropertyHelper.swappablePoint(a, t -> a = t, List.of(b), false, "shape.generic.point_n", 1),
            PropertyHelper.swappablePoint(b, t -> b = t, List.of(a), false, "shape.generic.point_n", 2),
            PropertyHelper.dynamicValue(delta, t -> delta = t, source.getPropertyName("delta")),
            new DisplayProperty(Component.translatable(source.getPropertyName("coordinates")), get().toComponent())
        );
    }

    public PointProvider getA() {
        return a;
    }

    public PointProvider getB() {
        return b;
    }

    public DynamicValue getDelta() {
        return delta;
    }

    @Override
    public PointPlacementType<?> type() {
        return LerpPlacementType.INSTANCE;
    }

    @Override
    public List<Shape> dependencies() {
        return List.of(a, b);
    }
}

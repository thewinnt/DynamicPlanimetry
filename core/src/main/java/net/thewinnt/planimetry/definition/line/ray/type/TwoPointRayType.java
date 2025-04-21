package net.thewinnt.planimetry.definition.line.ray.type;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.definition.line.ray.RayDefinition;
import net.thewinnt.planimetry.definition.line.ray.RayDefinitionType;
import net.thewinnt.planimetry.definition.line.ray.impl.TwoPointRay;
import net.thewinnt.planimetry.shapes.point.PointProvider;

import java.util.Comparator;
import java.util.Optional;

public class TwoPointRayType implements RayDefinitionType<TwoPointRay> {
    public static final TwoPointRayType INSTANCE = new TwoPointRayType();

    private TwoPointRayType() {}

    @Override
    public TwoPointRay fromNbt(CompoundTag nbt, LoadingContext context) {
        PointProvider a = context.resolveShape(nbt.getLong("a"));
        PointProvider b = context.resolveShape(nbt.getLong("b"));
        return new TwoPointRay(a, b);
    }

    @Override
    public CompoundTag writeNbt(RayDefinition object, SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        TwoPointRay definition = (TwoPointRay) object;
        nbt.putLong("a", definition.getA().getId());
        nbt.putLong("b", definition.getB().getId());
        context.addShape(definition.getA());
        context.addShape(definition.getB());
        return nbt;
    }

    @Override
    public TwoPointRay convert(RayDefinition other, Drawing drawing) {
        Vec2 a = other.start();
        double direction = other.direction();
        Optional<PointProvider> b = drawing.getPoints().stream()
            .min(Comparator.comparingDouble(
                value -> Math.abs(MathHelper.angleTo(a, value.getPosition()) - direction))
            );
        if (b.isPresent()) {
            return new TwoPointRay(drawing.getNearestPoint(a), b.get());
        } else {
            PointProvider point2 = PointProvider.simple(drawing, MathHelper.continueFromAngle(a, direction, 25));
            drawing.addShape(point2);
            return new TwoPointRay(drawing.getNearestPoint(a), point2);
        }
    }
}

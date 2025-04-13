package net.thewinnt.planimetry.definition.line.ray.type;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.definition.line.ray.RayDefinition;
import net.thewinnt.planimetry.definition.line.ray.RayDefinitionType;
import net.thewinnt.planimetry.definition.line.ray.impl.DirectionBasedRay;
import net.thewinnt.planimetry.shapes.point.PointProvider;

public class DirectionBasedRayType implements RayDefinitionType<DirectionBasedRay> {
    public static final DirectionBasedRayType INSTANCE = new DirectionBasedRayType();

    private DirectionBasedRayType() {}

    @Override
    public DirectionBasedRay convert(RayDefinition other, Drawing drawing) {
        return new DirectionBasedRay(drawing.getNearestPoint(other.start()), other.direction());
    }

    @Override
    public DirectionBasedRay fromNbt(CompoundTag nbt, LoadingContext context) {
        PointProvider point = context.resolveShape(nbt.getLong("point"));
        double direction = nbt.getDouble("direciton");
        return new DirectionBasedRay(point, direction);
    }

    @Override
    public CompoundTag writeNbt(RayDefinition object, SavingContext context) {
        DirectionBasedRay definition = (DirectionBasedRay) object;
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("point", context.addShape(definition.getStart()));
        nbt.putDouble("direction", definition.direction());
        return nbt;
    }
}

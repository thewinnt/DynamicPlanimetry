package net.thewinnt.planimetry.shapes.lines.definition.ray.type;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.shapes.lines.definition.ray.RayDefinition;
import net.thewinnt.planimetry.shapes.lines.definition.ray.RayDefinitionType;
import net.thewinnt.planimetry.shapes.lines.definition.ray.TwoPointRay;
import net.thewinnt.planimetry.shapes.point.PointProvider;

public class TwoPointRayType implements RayDefinitionType<TwoPointRay> {
    public static final TwoPointRayType INSTANCE = new TwoPointRayType();

    private TwoPointRayType() {}

    @Override
    public TwoPointRay fromNbt(CompoundTag nbt, LoadingContext context) {
        PointProvider a = (PointProvider) context.resolveShape(nbt.getLong("a"));
        PointProvider b = (PointProvider) context.resolveShape(nbt.getLong("b"));
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
        // TODO Auto-generated method stub
        return null;
    }
}

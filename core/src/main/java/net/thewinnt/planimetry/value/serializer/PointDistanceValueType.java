package net.thewinnt.planimetry.value.serializer;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.PointDistanceValue;

public class PointDistanceValueType implements DynamicValueType<PointDistanceValue> {
    public static final PointDistanceValueType INSTANCE = new PointDistanceValueType();

    private PointDistanceValueType() {}

    @Override
    public PointDistanceValue fromNbt(CompoundTag nbt, LoadingContext context) {
        PointProvider a = context.resolveShape(nbt.getLong("a"));
        PointProvider b = context.resolveShape(nbt.getLong("b"));
        return new PointDistanceValue(a, b);
    }

    @Override
    public CompoundTag toNbt(PointDistanceValue value, SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("a", context.addShape(value.getA()));
        nbt.putLong("b", context.addShape(value.getB()));
        return nbt;
    }

    @Override
    public PointDistanceValue create(Drawing drawing) {
        PointProvider a = (PointProvider) drawing.getRandom(shape -> shape instanceof PointProvider);
        return new PointDistanceValue(a, (PointProvider) drawing.getRandom(shape -> shape instanceof PointProvider && shape != a));
    }
}

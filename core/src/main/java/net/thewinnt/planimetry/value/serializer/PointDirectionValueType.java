package net.thewinnt.planimetry.value.serializer;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.PointDirectionValue;

public class PointDirectionValueType implements DynamicValueType<PointDirectionValue> {
    public static final PointDirectionValueType INSTANCE = new PointDirectionValueType();

    private PointDirectionValueType() {}

    @Override
    public PointDirectionValue fromNbt(CompoundTag nbt, LoadingContext context) {
        PointProvider a = context.resolveShape(nbt.getLong("a"));
        PointProvider b = context.resolveShape(nbt.getLong("b"));
        return new PointDirectionValue(a, b);
    }

    @Override
    public CompoundTag toNbt(PointDirectionValue value, SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("a", context.addShape(value.getA()));
        nbt.putLong("b", context.addShape(value.getB()));
        return nbt;
    }

    @Override
    public PointDirectionValue create(Drawing drawing) {
        PointProvider a = (PointProvider) drawing.getRandom(shape -> shape instanceof PointProvider);
        return new PointDirectionValue(a, (PointProvider) drawing.getRandom(shape -> shape instanceof PointProvider && shape != a));
    }
}

package net.thewinnt.planimetry.value.serializer;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.util.Util;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.ConstantValue;

public class ConstantValueType implements DynamicValueType<ConstantValue> {
    public static final ConstantValueType INSTANCE = new ConstantValueType();

    private ConstantValueType() {}

    @Override
    public ConstantValue fromNbt(CompoundTag nbt, LoadingContext context) {
        return new ConstantValue(nbt.getDouble("value"));
    }

    @Override
    public CompoundTag toNbt(ConstantValue value, SavingContext context) {
        return Util.make(new CompoundTag(), tag -> tag.putDouble("value", value.value()));
    }

    @Override
    public ConstantValue create(Drawing drawing) {
        return new ConstantValue(0);
    }
}

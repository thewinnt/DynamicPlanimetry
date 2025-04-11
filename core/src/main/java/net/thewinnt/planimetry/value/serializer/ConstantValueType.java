package net.thewinnt.planimetry.value.serializer;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.util.Util;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.ConstantValue;

public class ConstantValueType implements DynamicValueType<ConstantValue> {
    public static final ConstantValueType INSTANCE = new ConstantValueType();

    private ConstantValueType() {}

    @Override
    public ConstantValue fromNbt(CompoundTag tag) {
        return new ConstantValue(tag.getDouble("value"));
    }

    @Override
    public CompoundTag toNbt(ConstantValue value) {
        return Util.make(new CompoundTag(), tag -> tag.putDouble("value", value.value()));
    }
}

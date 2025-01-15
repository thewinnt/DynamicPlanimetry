package net.thewinnt.planimetry.value.serializer;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.util.Util;
import net.thewinnt.planimetry.value.DynamicValueSerializer;
import net.thewinnt.planimetry.value.type.ConstantValue;

public class ConstantSerializer implements DynamicValueSerializer<ConstantValue> {
    public static final ConstantSerializer INSTANCE = new ConstantSerializer();

    private ConstantSerializer() {}

    @Override
    public ConstantValue fromNbt(CompoundTag tag) {
        return new ConstantValue(tag.getDouble("value"));
    }

    @Override
    public CompoundTag toNbt(ConstantValue value) {
        return Util.make(new CompoundTag(), tag -> tag.putDouble("value", value.value()));
    }

    @Override
    public boolean usesContext() {
        return false;
    }
}

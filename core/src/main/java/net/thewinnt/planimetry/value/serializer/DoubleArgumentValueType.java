package net.thewinnt.planimetry.value.serializer;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.DoubleArgumentValue;

public class DoubleArgumentValueType implements DynamicValueType<DoubleArgumentValue> {
    public static final DoubleArgumentValueType INSTANCE = new DoubleArgumentValueType();

    private DoubleArgumentValueType() {}

    @Override
    public DoubleArgumentValue fromNbt(CompoundTag tag) {
        return null;
    }

    @Override
    public CompoundTag toNbt(DoubleArgumentValue value) {
        return null;
    }

    @Override
    public boolean usesContext() {
        return false;
    }
}

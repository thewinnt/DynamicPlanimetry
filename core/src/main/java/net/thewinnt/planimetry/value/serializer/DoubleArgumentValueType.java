package net.thewinnt.planimetry.value.serializer;

import java.util.function.DoubleBinaryOperator;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.ConstantValue;
import net.thewinnt.planimetry.value.type.DoubleArgumentValue;

public record DoubleArgumentValueType(DoubleBinaryOperator operation) implements DynamicValueType<DoubleArgumentValue> {
    @Override
    public DoubleArgumentValue fromNbt(CompoundTag tag) {
        DynamicValue a = DynamicValue.fromNbt(tag.getCompoundTag("a"));
        DynamicValue b = DynamicValue.fromNbt(tag.getCompoundTag("b"));
        return new DoubleArgumentValue(a, b, operation);
    }

    @Override
    public CompoundTag toNbt(DoubleArgumentValue value) {
        CompoundTag nbt = new CompoundTag();
        nbt.put("a", DynamicValue.toNbt(value.arg1()));
        nbt.put("b", DynamicValue.toNbt(value.arg2()));
        return nbt;
    }

    @Override
    public DoubleArgumentValue create() {
        return new DoubleArgumentValue(new ConstantValue(0), new ConstantValue(0), operation);
    }

    public DoubleArgumentValue create(DynamicValue arg1, DynamicValue arg2) {
        return new DoubleArgumentValue(arg1, arg2, operation);
    }
}

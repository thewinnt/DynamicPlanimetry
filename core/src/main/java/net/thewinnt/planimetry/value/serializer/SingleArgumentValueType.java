package net.thewinnt.planimetry.value.serializer;

import java.util.function.DoubleUnaryOperator;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.util.Util;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.ConstantValue;
import net.thewinnt.planimetry.value.type.SingleArgumentValue;

public record SingleArgumentValueType(DoubleUnaryOperator operation) implements DynamicValueType<SingleArgumentValue> {

    @Override
    public SingleArgumentValue fromNbt(CompoundTag tag) {
        return new SingleArgumentValue(DynamicValue.fromNbt(tag.getCompoundTag("value")), operation);
    }

    @Override
    public CompoundTag toNbt(SingleArgumentValue value) {
        return Util.make(new CompoundTag(), tag -> tag.put("value", DynamicValue.toNbt(value.input())));
    }

    @Override
    public SingleArgumentValue create() {
        return new SingleArgumentValue(new ConstantValue(0), operation);
    }
}

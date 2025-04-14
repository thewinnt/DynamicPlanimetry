package net.thewinnt.planimetry.value.serializer;

import java.util.function.DoubleUnaryOperator;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.util.Util;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.ConstantValue;
import net.thewinnt.planimetry.value.type.SingleArgumentValue;

public record SingleArgumentValueType(DoubleUnaryOperator operation) implements DynamicValueType<SingleArgumentValue> {

    @Override
    public SingleArgumentValue fromNbt(CompoundTag nbt, LoadingContext context) {
        return new SingleArgumentValue(DynamicValue.fromNbt(nbt.getCompoundTag("value"), context), operation);
    }

    @Override
    public CompoundTag toNbt(SingleArgumentValue value, SavingContext context) {
        return Util.make(new CompoundTag(), tag -> tag.put("value", DynamicValue.toNbt(value.input(), context)));
    }

    @Override
    public SingleArgumentValue create(Drawing drawing) {
        return new SingleArgumentValue(new ConstantValue(0), operation);
    }
}

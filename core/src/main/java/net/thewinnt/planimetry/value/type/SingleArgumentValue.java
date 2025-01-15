package net.thewinnt.planimetry.value.type;

import net.thewinnt.planimetry.point.ValueContext;
import net.thewinnt.planimetry.value.DynamicValue;

import java.util.function.DoubleUnaryOperator;

public record SingleArgumentValue(DynamicValue input, DoubleUnaryOperator operation) implements DynamicValue {
    @Override
    public double get(ValueContext context) {
        return operation.applyAsDouble(input().get(context));
    }
}

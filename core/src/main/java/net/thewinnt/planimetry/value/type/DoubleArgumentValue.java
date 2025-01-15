package net.thewinnt.planimetry.value.type;

import net.thewinnt.planimetry.point.ValueContext;
import net.thewinnt.planimetry.value.DynamicValue;

import java.util.function.DoubleBinaryOperator;

public record DoubleArgumentValue(DynamicValue arg1, DynamicValue arg2, DoubleBinaryOperator operation) implements DynamicValue {
    @Override
    public double get(ValueContext context) {
        return operation.applyAsDouble(arg1.get(context), arg2.get(context));
    }
}

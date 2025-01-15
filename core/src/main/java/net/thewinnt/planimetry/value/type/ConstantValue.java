package net.thewinnt.planimetry.value.type;

import net.thewinnt.planimetry.point.ValueContext;
import net.thewinnt.planimetry.value.DynamicValue;

public record ConstantValue(double value) implements DynamicValue {
    @Override
    public double get(ValueContext context) {
        return value;
    }
}

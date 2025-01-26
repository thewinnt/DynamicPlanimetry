package net.thewinnt.planimetry.value.type;

import net.thewinnt.planimetry.point.ValueContext;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.DynamicValue;

import java.util.Objects;

public final class ConstantValue implements DynamicValue {
    private final NumberProperty property;
    private double value;

    public ConstantValue(double value) {
        this.value = value;
    }

    @Override
    public double get(ValueContext context) {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (obj instanceof ConstantValue that) {
            return this.value == that.value;
        }
        return false;
    }
}

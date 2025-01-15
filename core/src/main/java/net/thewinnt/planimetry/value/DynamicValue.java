package net.thewinnt.planimetry.value;

import net.thewinnt.planimetry.point.ValueContext;

public interface DynamicValue {
    double get(ValueContext context);
}

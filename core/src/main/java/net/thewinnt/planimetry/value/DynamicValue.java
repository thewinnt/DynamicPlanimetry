package net.thewinnt.planimetry.value;

import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.point.ValueContext;
import net.thewinnt.planimetry.ui.properties.Property;

import java.util.Collection;
import java.util.Map;

public interface DynamicValue {
    double get(ValueContext context);
    Collection<Property<?>> properties();
    DynamicValueType<?> type();

    default String translationKey() {
        return Registries.DYNAMIC_VALUE_TYPES.getName(type()).toLanguageKey("value_type");
    }
}

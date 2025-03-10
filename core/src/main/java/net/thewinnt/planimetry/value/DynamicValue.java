package net.thewinnt.planimetry.value;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.point.ValueContext;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.text.Component;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.stream.Stream;

public interface DynamicValue extends Cloneable {
    double get(ValueContext context);
    Collection<Property<?>> properties();
    DynamicValueType<? extends DynamicValue> type();
    @Nullable Stream<PointProvider> dependencies();
    DynamicValue clone();

    default String translationKey(String postfix) {
        return Registries.DYNAMIC_VALUE_TYPES.getName(type()).toLanguageKey("value_type", postfix);
    }

    default Component typeName() {
        return type().name();
    }

    static CompoundTag toNbt(DynamicValue value) {
        CompoundTag output = value.type().toNbtUnchecked(value);
        output.putString("type", Registries.DYNAMIC_VALUE_TYPES.getName(value.type()).toString());
        return output;
    }

    static DynamicValue fromNbt(CompoundTag nbt) {
        Identifier type = new Identifier(nbt.getString("type"));
        return Registries.DYNAMIC_VALUE_TYPES.get(type).fromNbt(nbt);
    }
}

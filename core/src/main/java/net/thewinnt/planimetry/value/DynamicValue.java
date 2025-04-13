package net.thewinnt.planimetry.value;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.PropertySupplier;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.type.ConstantValue;
import net.thewinnt.planimetry.value.type.DoubleArgumentValue;

import java.util.stream.Stream;

public interface DynamicValue extends Cloneable, PropertySupplier {
    double get();
    DynamicValueType<? extends DynamicValue> type();
    Stream<PointProvider> dependencies();
    DynamicValue clone();

    default DynamicValue add(double delta) {
        return DynamicValueType.ADD.create(this, new ConstantValue(delta));
    }

    default String translationKey(String postfix) {
        return Registries.DYNAMIC_VALUE_TYPE.getName(type()).toLanguageKey("value_type", postfix);
    }

    default Component typeName() {
        return type().name();
    }

    static CompoundTag toNbt(DynamicValue value) {
        CompoundTag output = value.type().toNbtUnchecked(value);
        output.putString("type", Registries.DYNAMIC_VALUE_TYPE.getName(value.type()).toString());
        return output;
    }

    static DynamicValue fromNbt(CompoundTag nbt) {
        Identifier type = new Identifier(nbt.getString("type"));
        return Registries.DYNAMIC_VALUE_TYPE.get(type).fromNbt(nbt);
    }
}

package net.thewinnt.planimetry.value;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.PropertySupplier;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.type.ConstantValue;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public interface DynamicValue extends Cloneable, PropertySupplier {
    double get();
    @NotNull DynamicValueType<? extends DynamicValue> type();
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

    static CompoundTag toNbt(DynamicValue value, SavingContext context) {
        CompoundTag output = value.type().toNbtUnchecked(value, context);
        output.putString("type", Registries.DYNAMIC_VALUE_TYPE.getName(value.type()).toString());
        return output;
    }

    static DynamicValue fromNbt(CompoundTag nbt, LoadingContext context) {
        Identifier type = new Identifier(nbt.getString("type"));
        return Registries.DYNAMIC_VALUE_TYPE.get(type).fromNbt(nbt, context);
    }
}

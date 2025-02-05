package net.thewinnt.planimetry.value;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.Registry;
import net.thewinnt.planimetry.point.ValueContext;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.serializer.ConstantValueType;
import net.thewinnt.planimetry.value.type.ConstantValue;

import java.util.Optional;
import java.util.Random;

public interface DynamicValueType<T extends DynamicValue> {
    DynamicValueType<ConstantValue> CONSTANT = register(ConstantValueType.INSTANCE, new Identifier("constant"));

    T fromNbt(CompoundTag tag);
    CompoundTag toNbt(T value);

    @SuppressWarnings("unchecked")
    default CompoundTag toNbtUnchecked(DynamicValue value) {
        return toNbt((T) value);
    }

    /**
     * Returns whether the dynamic value type of this serializer needs its context
     * @return whether {@code T.get()} uses its {@link ValueContext}
     */
    boolean usesContext();

    /**
     * Returns a random value in some reasonable range, if the implementer felt like it.
     * @param outMin the preferred minimum value
     * @param outMax the preferred maximum value
     * @see net.thewinnt.planimetry.screen.MainMenuScreen
     */
    default Optional<DynamicValue> createRandom(Random random, double outMin, double outMax, ValueContext context) {
        return Optional.empty();
    }

    default Component name() {
        return Component.translatable(Registries.DYNAMIC_VALUE_TYPES.getName(this).toLanguageKey("value_type"));
    }

    static <T extends DynamicValue> DynamicValueType<T> register(DynamicValueType<T> type, Identifier id) {
        return Registry.register(Registries.DYNAMIC_VALUE_TYPES, type, id);
    }

    static void init() {}
}

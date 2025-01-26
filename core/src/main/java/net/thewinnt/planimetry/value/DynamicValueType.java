package net.thewinnt.planimetry.value;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.point.ValueContext;

import java.util.Optional;
import java.util.Random;

public interface DynamicValueType<T extends DynamicValue> {
    T fromNbt(CompoundTag tag);
    CompoundTag toNbt(T value);

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

    static void init() {}
}

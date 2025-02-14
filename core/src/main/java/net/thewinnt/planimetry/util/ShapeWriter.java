package net.thewinnt.planimetry.util;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.SavingContext;

@FunctionalInterface
public interface ShapeWriter<T> {
    CompoundTag writeNbt(T object, SavingContext context);
}

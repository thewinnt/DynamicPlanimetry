package net.thewinnt.planimetry.util;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.LoadingContext;

@FunctionalInterface
public interface ShapeReader<T> {
    T fromNbt(CompoundTag nbt, LoadingContext context);
}

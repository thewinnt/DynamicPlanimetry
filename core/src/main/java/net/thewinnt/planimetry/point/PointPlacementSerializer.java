package net.thewinnt.planimetry.point;

import net.querz.nbt.tag.CompoundTag;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

public interface PointPlacementSerializer<T extends PointPlacement> {
    T fromNbt(CompoundTag nbt);
    CompoundTag toNbt(T value);
    @Nullable T createRandom(Random random, ValueContext context);
}

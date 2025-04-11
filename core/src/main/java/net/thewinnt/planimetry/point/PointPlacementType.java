package net.thewinnt.planimetry.point;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.Registry;
import net.thewinnt.planimetry.point.placement.StaticPlacement;
import net.thewinnt.planimetry.point.type.StaticPlacementType;
import net.thewinnt.planimetry.ui.text.Component;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

public interface PointPlacementType<T extends PointPlacement> {
    PointPlacementType<StaticPlacement> STATIC = register("static", StaticPlacementType.INSTANCE);

    T fromNbt(CompoundTag nbt);
    CompoundTag toNbt(T value);
    @Nullable T createRandom(Random random, ValueContext context);

    default Component property(String postfix) {
        return Component.translatable(Registries.POINT_PLACEMENT_TYPE.getName(this).toLanguageKey("point_placement_type", postfix));
    }

    static <T extends PointPlacement> PointPlacementType<T> register(String id, PointPlacementType<T> element) {
        return Registry.register(Registries.POINT_PLACEMENT_TYPE, element, id);
    }

    static void init() {}
}

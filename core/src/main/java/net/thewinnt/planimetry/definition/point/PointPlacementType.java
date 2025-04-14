package net.thewinnt.planimetry.definition.point;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.Registry;
import net.thewinnt.planimetry.data.registry.TagKey;
import net.thewinnt.planimetry.definition.point.placement.CalculatablePlacement;
import net.thewinnt.planimetry.definition.point.placement.CirclePlacement;
import net.thewinnt.planimetry.definition.point.placement.MousePlacement;
import net.thewinnt.planimetry.definition.point.placement.OffsetPlacement;
import net.thewinnt.planimetry.definition.point.placement.StaticPlacement;
import net.thewinnt.planimetry.definition.point.type.CalculatablePlacementType;
import net.thewinnt.planimetry.definition.point.type.CirclePlacementType;
import net.thewinnt.planimetry.definition.point.type.MousePlacementType;
import net.thewinnt.planimetry.definition.point.type.OffsetPlacementType;
import net.thewinnt.planimetry.definition.point.type.StaticPlacementType;
import net.thewinnt.planimetry.shapes.ShapeDefinitionType;
import net.thewinnt.planimetry.ui.text.Component;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

public interface PointPlacementType<T extends PointPlacement> extends ShapeDefinitionType<T, PointPlacement> {
    PointPlacementType<StaticPlacement> STATIC = register("static", StaticPlacementType.INSTANCE);
    PointPlacementType<MousePlacement> MOUSE_POINTER = register("mouse_pointer", MousePlacementType.INSTANCE);
    PointPlacementType<OffsetPlacement> OFFSET = register("offset", OffsetPlacementType.INSTANCE);
    PointPlacementType<CalculatablePlacement> CALCULATABLE = register("calculatable", CalculatablePlacementType.INSTANCE);
    PointPlacementType<CirclePlacement> CIRCLE = register("circle", CirclePlacementType.INSTANCE);

    TagKey<PointPlacementType<?>> SELECTABLE = TagKey.create(Registries.POINT_PLACEMENT_TYPE, new Identifier("selectable"));

    /** If this returns null, will try to read as a static point, otherwise makes a static (0, 0) point */
    @Nullable T createRandom(Random random, ValueContext context);

    default Component property(String postfix) {
        return Component.translatable(Registries.POINT_PLACEMENT_TYPE.getName(this).toLanguageKey("point_placement_type", postfix));
    }

    static <T extends PointPlacement> PointPlacementType<T> register(String id, PointPlacementType<T> element) {
        return Registry.register(Registries.POINT_PLACEMENT_TYPE, element, id);
    }

    static void init() {}
}

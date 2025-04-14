package net.thewinnt.planimetry.definition.line.ray;

import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.Registry;
import net.thewinnt.planimetry.definition.line.ray.impl.DirectionBasedRay;
import net.thewinnt.planimetry.definition.line.ray.impl.TwoPointRay;
import net.thewinnt.planimetry.definition.line.ray.type.DirectionBasedRayType;
import net.thewinnt.planimetry.shapes.ShapeDefinitionType;
import net.thewinnt.planimetry.definition.line.ray.type.TwoPointRayType;

public interface RayDefinitionType<T extends RayDefinition> extends ShapeDefinitionType<T, RayDefinition> {
    RayDefinitionType<TwoPointRay> TWO_POINTS = register(TwoPointRayType.INSTANCE, "two_points");
    RayDefinitionType<DirectionBasedRay> DIRECTION_BASED = register(DirectionBasedRayType.INSTANCE, "direction_based");

    static <T extends RayDefinition> RayDefinitionType<T> register(RayDefinitionType<T> type, String name) {
        return Registry.register(Registries.RAY_DEFITINION_TYPE, type, name);
    }

    static void init() {}
}

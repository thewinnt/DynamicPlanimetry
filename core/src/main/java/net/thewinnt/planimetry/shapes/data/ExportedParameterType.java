package net.thewinnt.planimetry.shapes.data;

import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.Registry;

public record ExportedParameterType() {
    public static final ExportedParameterType LENGTH = register("length");
    public static final ExportedParameterType ANGLE = register("angle");

    public static ExportedParameterType register(String id) {
        return Registry.register(Registries.SHAPE_PARAMETERS, new ExportedParameterType(), id);
    }

    public static void init() {}
}

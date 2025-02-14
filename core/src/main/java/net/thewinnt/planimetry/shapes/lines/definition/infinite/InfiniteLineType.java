package net.thewinnt.planimetry.shapes.lines.definition.infinite;

import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.Registry;
import net.thewinnt.planimetry.shapes.ShapeDefinitionType;
import net.thewinnt.planimetry.shapes.lines.definition.infinite.type.TwoPointInfiniteType;

public interface InfiniteLineType<T extends InfiniteLineDefinition> extends ShapeDefinitionType<T, InfiniteLineDefinition> {
    InfiniteLineType<TwoPointInfiniteLine> TWO_POINTS = register(TwoPointInfiniteType.INSTANCE, "two_points");

    static <T extends InfiniteLineDefinition> InfiniteLineType<T> register(InfiniteLineType<T> type, String id) {
        return Registry.register(Registries.INFINITE_LINE_DEFINITION_TYPE, type, id);
    }

    static void init() {}
}

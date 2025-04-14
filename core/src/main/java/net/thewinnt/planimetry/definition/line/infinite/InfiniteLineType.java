package net.thewinnt.planimetry.definition.line.infinite;

import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.Registry;
import net.thewinnt.planimetry.data.registry.TagKey;
import net.thewinnt.planimetry.definition.line.infinite.impl.AngleBasedLineDefinition;
import net.thewinnt.planimetry.definition.line.infinite.impl.ParallelLineDefinition;
import net.thewinnt.planimetry.definition.line.infinite.impl.RelativeAngleLineDefinition;
import net.thewinnt.planimetry.definition.line.infinite.impl.TwoPointInfiniteLine;
import net.thewinnt.planimetry.definition.line.infinite.type.RelativeAngleLineType;
import net.thewinnt.planimetry.shapes.ShapeDefinitionType;
import net.thewinnt.planimetry.definition.line.infinite.type.AngleLineType;
import net.thewinnt.planimetry.definition.line.infinite.type.ParallelLineType;
import net.thewinnt.planimetry.definition.line.infinite.type.TwoPointInfiniteType;

public interface InfiniteLineType<T extends InfiniteLineDefinition> extends ShapeDefinitionType<T, InfiniteLineDefinition> {
    TagKey<InfiniteLineType<?>> SELECTABLE = TagKey.create(Registries.INFINITE_LINE_DEFINITION_TYPE, new Identifier("selectable"));

    InfiniteLineType<TwoPointInfiniteLine> TWO_POINTS = register(TwoPointInfiniteType.INSTANCE, "two_points");
    InfiniteLineType<ParallelLineDefinition> PARALLEL = register(ParallelLineType.INSTANCE, "parallel");
    InfiniteLineType<AngleBasedLineDefinition> ANGLE_BASED = register(AngleLineType.INSTANCE, "angle_based");
    InfiniteLineType<RelativeAngleLineDefinition> RELATIVE_ANGLE = register(RelativeAngleLineType.INSTANCE, "relative_angle");

    static <T extends InfiniteLineDefinition> InfiniteLineType<T> register(InfiniteLineType<T> type, String id) {
        return Registry.register(Registries.INFINITE_LINE_DEFINITION_TYPE, type, id);
    }

    static void init() {}
}

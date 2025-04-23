package net.thewinnt.planimetry.data;

import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.TagKey;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineType;
import net.thewinnt.planimetry.definition.point.PointPlacementType;

import java.util.List;

public record DefaultTags(TagKey<?> tag, List<Identifier> elements) {
    public static final DefaultTags SELECTABLE_LINES = new DefaultTags(
        InfiniteLineType.SELECTABLE,
        List.of(
            new Identifier("dynamic_planimetry:two_points"),
            new Identifier("dynamic_planimetry:angle_based")
        )
    );

    public static final DefaultTags SELECTABLE_POINTS = new DefaultTags(
        PointPlacementType.SELECTABLE,
        List.of(
            new Identifier("dynamic_planimetry:static"),
            new Identifier("dynamic_planimetry:offset"),
            new Identifier("dynamic_planimetry:calculatable"),
            new Identifier("dynamic_planimetry:circle"),
            new Identifier("dynamic_planimetry:lerp")
        )
    );
}

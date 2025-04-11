package net.thewinnt.planimetry.point;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.ShapeDefinitionType;
import net.thewinnt.planimetry.ui.properties.Property;

import java.util.Collection;

public interface PointPlacement {
    Vec2 get(ValueContext context);
    Collection<Property<?>> properties();
    PointPlacementType<?> type();
}

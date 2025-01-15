package net.thewinnt.planimetry.point;

import net.thewinnt.planimetry.math.Vec2;

public interface PointPlacement {
    Vec2 get(ValueContext context);
}

package net.thewinnt.planimetry.definition.point;

import java.util.Collection;

import net.thewinnt.planimetry.shapes.point.PointProvider;

public interface ValueContext {
    PointProvider getPoint(long id);
    Collection<PointProvider> allPoints();
}

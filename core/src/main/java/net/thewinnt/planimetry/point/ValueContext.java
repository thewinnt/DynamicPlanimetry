package net.thewinnt.planimetry.point;

import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.text.NameComponent;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ValueContext {
    Shape getShape(long id);
    PointProvider getPoint(long id);
    @Nullable PointProvider getPoint(NameComponent name);
}

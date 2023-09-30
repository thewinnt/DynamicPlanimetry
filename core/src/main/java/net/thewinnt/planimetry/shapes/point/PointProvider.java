package net.thewinnt.planimetry.shapes.point;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;

public interface PointProvider extends Shape {
    Vec2 getPosition();
}

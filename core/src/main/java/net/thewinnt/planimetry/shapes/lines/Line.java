package net.thewinnt.planimetry.shapes.lines;

import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;

public abstract class Line implements Shape {
    public final PointProvider a;
    public final PointProvider b;

    public Line(PointProvider a, PointProvider b) {
        this.a = a;
        this.b = b;
    }
}

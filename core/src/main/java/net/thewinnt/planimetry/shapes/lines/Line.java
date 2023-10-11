package net.thewinnt.planimetry.shapes.lines;

import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;

public abstract class Line implements Shape {
    public final PointReference a;
    public final PointReference b;

    public Line(PointReference a, PointReference b) {
        this.a = a;
        this.b = b;
    }

    public Line(PointProvider a, PointProvider b) {
        this.a = new PointReference(a);
        this.b = new PointReference(b);
    }
}

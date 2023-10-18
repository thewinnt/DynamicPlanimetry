package net.thewinnt.planimetry.shapes.lines;

import java.util.function.DoubleFunction;

import net.thewinnt.planimetry.math.Vec2;
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

    public double getSlope() {
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        return (a.y - b.y) / (a.x - b.x);
    }

    public DoubleFunction<Double> compileFormula() {
        // y = kx+b
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        if (a.x == b.x) return x -> Double.NaN;
        double k = (a.y - b.y) / (a.x - b.x);
        return x -> k * (x - a.x) + a.y;
    }
}

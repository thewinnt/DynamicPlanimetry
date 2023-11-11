package net.thewinnt.planimetry.shapes.lines;

import java.util.Collection;
import java.util.List;
import java.util.function.DoubleFunction;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.properties.EnclosingProperty;
import net.thewinnt.planimetry.ui.properties.Property;

public abstract class Line extends Shape {
    public final PointReference a;
    public final PointReference b;
    protected String nameOverride = null;

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

    @Override
    public Collection<Property<?>> getProperties() {
        return List.of(
            new EnclosingProperty("1 точка", this.a.getProperties()),
            new EnclosingProperty("2 точка", this.b.getProperties())
        );
    }

    @Override
    public String getName() {
        if (nameOverride != null) return nameOverride;
        return this.a.getName() + this.b.getName();
    }

    public void setName(String name) {
        this.nameOverride = name;
    }
}

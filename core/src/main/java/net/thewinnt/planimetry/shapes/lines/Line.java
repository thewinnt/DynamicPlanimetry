package net.thewinnt.planimetry.shapes.lines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.DoubleFunction;
import java.util.stream.Collectors;

import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.math.AABB;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.SegmentLike;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.ui.functions.CreateAngledLine;
import net.thewinnt.planimetry.ui.functions.CreateParallelLine;
import net.thewinnt.planimetry.ui.functions.Function;
import net.thewinnt.planimetry.ui.functions.LineTypeConversion;

public abstract class Line extends Shape {
    public Line(Drawing drawing) {
        super(drawing);
    }

    public double getSlope() {
        Vec2 a = this.point1();
        Vec2 b = this.point2();
        return (a.y - b.y) / (a.x - b.x);
    }

    public DoubleFunction<Double> compileFormula() {
        // y = kx+b
        Vec2 a = this.point1();
        Vec2 b = this.point2();
        if (a.x == b.x) return x -> Double.NaN;
        double k = (a.y - b.y) / (a.x - b.x);
        return x -> k * (x - a.x) + a.y;
    }

    protected abstract Vec2 point1();
    protected abstract Vec2 point2();

    @Override
    public Collection<Function<?>> getFunctions() {
        Collection<Function<?>> output = new ArrayList<>();
        output.add(new LineTypeConversion(drawing, this));
        output.add(new CreateParallelLine(drawing, this));
        output.add(new CreateAngledLine(drawing, this));
        output.addAll(super.getFunctions());
        return output;
    }
    
    public abstract LineType getType();
    public abstract Line convertTo(LineType other);

    public Optional<Vec2> intersection(Line other) {
        return intersectInf(other).filter(other::contains).filter(this::contains);
    }

    @Override
    public boolean intersects(AABB aabb) {
        if (aabb.contains(point1()) || aabb.contains(point2())) {
            return true;
        }
        for (SegmentLike i : aabb.asLineSegments()) {
            if (this.intersect(i).isPresent()) return true;
        }
        return false;
    }

    /** Intersects this and the other line, as if they were both infinite */
    protected Optional<Vec2> intersectInf(Line other) {
        Vec2 a = this.point1();
        Vec2 b = this.point2();
        Vec2 c = other.point1();
        Vec2 d = other.point2();
        double denom = (a.x - b.x) * (c.y - d.y) - (a.y - b.y) * (c.x - d.x);
        if (MathHelper.roughlyEquals(denom, 0)) return Optional.empty();
        double tx = (a.x * b.y - a.y * b.x) * (c.x - d.x) - (a.x - b.x) * (c.x * d.y - c.y * d.x) / denom;
        double ty = (a.x * b.y - a.y * b.x) * (c.y - d.y) - (a.y - b.y) * (c.x * d.y - c.y * d.x) / denom;
        return Optional.of(new Vec2(tx, ty));
    }

    protected Optional<Vec2> intersect(SegmentLike other) {
        Vec2 a = this.point1();
        Vec2 b = this.point2();
        Vec2 c = other.point1();
        Vec2 d = other.point2();
        double denom = (a.x - b.x) * (c.y - d.y) - (a.y - b.y) * (c.x - d.x);
        if (MathHelper.roughlyEquals(denom, 0)) return Optional.empty();
        double tx = (a.x * b.y - a.y * b.x) * (c.x - d.x) - (a.x - b.x) * (c.x * d.y - c.y * d.x) / denom;
        double ty = (a.x * b.y - a.y * b.x) * (c.y - d.y) - (a.y - b.y) * (c.x * d.y - c.y * d.x) / denom;
        return Optional.of(new Vec2(tx, ty)).filter(this::contains).filter(other::contains);
    }

    @Override
    public Collection<Vec2> intersections(Shape other) {
        if (other instanceof Line line) {
            return intersectInf(line).filter(line::contains).map(List::of).orElse(List.of());
        } else {
            Collection<SegmentLike> segments = other.asSegments();
            if (!segments.isEmpty()) {
                return segments.stream()
                               .map(this::intersect)
                               .filter(Optional::isPresent)
                               .map(Optional::get)
                               .collect(Collectors.toList());
            } else {
                return other.intersections(this); // in hopes that the other shape will have figured it out
            }
        }
    }

    @Override
    public Collection<Vec2> intersections(SegmentLike other) {
        return this.intersect(other).map(List::of).orElse(List.of());
    }

    @Override
    public Collection<SegmentLike> asSegments() {
        return List.of();
    }
}

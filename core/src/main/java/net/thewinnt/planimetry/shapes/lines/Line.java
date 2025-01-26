package net.thewinnt.planimetry.shapes.lines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.DoubleFunction;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.AABB;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.functions.CreateAngledLine;
import net.thewinnt.planimetry.ui.functions.CreateParallelLine;
import net.thewinnt.planimetry.ui.functions.Function;
import net.thewinnt.planimetry.ui.functions.LineTypeConversion;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.text.Component;

public abstract class Line extends Shape {
    public final PointReference a;
    public final PointReference b;

    public Line(Drawing drawing, PointProvider a, PointProvider b) {
        super(drawing);
        if (a instanceof PointReference point) {
            this.a = point;
        } else {
            this.a = new PointReference(a);
        }
        if (b instanceof PointReference point) {
            this.b = point;
        } else {
            this.b = new PointReference(b);
        }
        this.a.addDepending(this);
        this.b.addDepending(this);
        this.addDependency(a);
        this.addDependency(b);
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

    // /**
    //  * kx+b, k = Vec2.x, b = Vec2.y
    //  */
    // public Vec2 formulaValues() {
    //     // kx - kx0 + y0
    //     Vec2 a = this.a.getPosition();
    //     Vec2 b = this.b.getPosition();
    //     double k = (a.y - b.y) / (a.x - b.x);
    //     return new Vec2(k, -k * a.x + a.y);
    // }

    @Override
    public Collection<Property<?>> getProperties() {
        return List.of(
            new PropertyGroup(this.a.getName(), this.a.getProperties()),
            new PropertyGroup(this.b.getName(), this.b.getProperties())
        );
    }

    @Override
    public Collection<Function<?>> getFunctions() {
        Collection<Function<?>> output = new ArrayList<>();
        output.add(new LineTypeConversion(drawing, this));
        output.add(new CreateParallelLine(drawing, this));
        output.add(new CreateAngledLine(drawing, this));
        output.addAll(super.getFunctions());
        return output;
    }

    @Override
    public Component getName() {
        if (nameOverride != null) return Component.translatable(getTypeName(), nameOverride);
        return Component.translatable(getTypeName(), this.a.getNameComponent(), this.b.getNameComponent());
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        if (old == this.a) {
            this.a.setPoint((PointProvider)neo);
        } else if (old == this.b) {
            this.b.setPoint((PointProvider)neo);
        }
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("a", this.a.getId());
        nbt.putLong("b", this.b.getId());
        context.addShape(this.a);
        context.addShape(this.b);
        return nbt;
    }

    public abstract LineType getType();

    public Optional<Vec2> intersection(Line other) {
        return intersectInf(other).filter(other::contains).filter(this::contains);
    }

    @Override
    public boolean intersects(AABB aabb) {
        if (aabb.contains(a.getPosition()) || aabb.contains(b.getPosition())) {
            return true;
        }
        for (LineSegment i : aabb.asLineSegments()) {
            if (intersection(i).isPresent()) return true;
        }
        return false;
    }

    /** Intersects this and the other line, as if they were both infinite */
    protected Optional<Vec2> intersectInf(Line other) {
        double x1, x2, x3, x4, y1, y2, y3, y4;
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        Vec2 c = other.a.getPosition();
        Vec2 d = other.b.getPosition();
        x1 = a.x;
        x2 = b.x;
        x3 = c.x;
        x4 = d.x;
        y1 = a.y;
        y2 = b.y;
        y3 = c.y;
        y4 = d.y;
        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (MathHelper.roughlyEquals(denom, 0)) return Optional.empty();
        double tx = (x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4) / denom;
        double ty = (x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4) / denom;
        return Optional.of(new Vec2(tx, ty));
    }

    @Override
    public void move(Vec2 delta) {
        this.a.move(delta);
        this.b.move(delta);
    }

    @Override
    public void move(double dx, double dy) {
        this.a.move(dx, dy);
        this.b.move(dx, dy);
    }

    @Override
    public boolean canMove() {
        return true;
    }
}

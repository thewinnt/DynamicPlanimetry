package net.thewinnt.planimetry.shapes.lines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.DoubleFunction;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.SavingContext;
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
import net.thewinnt.planimetry.ui.properties.types.EnclosingProperty;
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

    @Override
    public Collection<Property<?>> getProperties() {
        return List.of(
            new EnclosingProperty(this.a.getName(), this.a.getProperties()),
            new EnclosingProperty(this.b.getName(), this.b.getProperties())
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

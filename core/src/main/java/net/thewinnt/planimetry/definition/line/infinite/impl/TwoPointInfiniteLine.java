package net.thewinnt.planimetry.definition.line.infinite.impl;

import java.util.Collection;
import java.util.List;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineDefinition;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineType;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.lines.LineSegment;
import net.thewinnt.planimetry.shapes.lines.Ray;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.text.Component;

public class TwoPointInfiniteLine extends InfiniteLineDefinition {
    private PointProvider a;
    private PointProvider b;

    public TwoPointInfiniteLine(PointProvider a, PointProvider b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Vec2 point1() {
        return a.getPosition();
    }

    @Override
    public PointProvider getBasePoint() {
        return a;
    }

    @Override
    public Vec2 point2() {
        return b.getPosition();
    }

    @Override
    public boolean canMove() {
        return a.canMove() && b.canMove();
    }

    @Override
    public void move(double dx, double dy) {
        a.move(dx, dy);
        b.move(dx, dy);
    }

    @Override
    public Component getName() {
        return Component.of(a.getNameComponent(), b.getNameComponent());
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        if (neo instanceof PointProvider point) {
            if (old == a) {
                a = point;
            } else if (old == b) {
                b = point;
            }
        }
    }

    @Override
    public Collection<Property<?>> properties() {
        return List.of(
            PropertyHelper.swappablePoint(a, point -> a = point, "shape.generic.point_n", 1),
            PropertyHelper.swappablePoint(b, point -> b = point, "shape.generic.point_n", 2)
        );
    }

    public CompoundTag writeNbt(SavingContext context) {
        CompoundTag output = new CompoundTag();
        output.putLong("a", a.getId());
        context.addShape(a);
        output.putLong("b", b.getId());
        context.addShape(b);
        return output;
    }

    public static TwoPointInfiniteLine fromNbt(CompoundTag nbt, LoadingContext context) {
        PointProvider a = context.resolveShape(nbt.getLong("a"));
        PointProvider b = context.resolveShape(nbt.getLong("b"));
        return new TwoPointInfiniteLine(a, b);
    }

    @Override
    public InfiniteLineType<?> type() {
        return InfiniteLineType.TWO_POINTS;
    }

    @Override
    public LineSegment asLineSegment(Drawing drawing) {
        return new LineSegment(drawing, a, b);
    }

    @Override
    public Ray asRay(Drawing drawing) {
        return Ray.of(drawing, a, b);
    }

    @Override
    public List<Shape> dependencies() {
        return List.of(a, b);
    }
}

package net.thewinnt.planimetry.shapes.lines.definition.infinite;

import java.util.ArrayList;
import java.util.Collection;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.lines.LineSegment;
import net.thewinnt.planimetry.shapes.lines.Ray;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.properties.types.ShapeProperty;
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
        a.rebuildProperties();
        b.rebuildProperties();
        ArrayList<Property<?>> output = new ArrayList<>();
        PropertyGroup point1 = new PropertyGroup(a.getName());
        ShapeProperty setPoint1 = new ShapeProperty(Component.translatable("shape.generic.point_n", 1), a.getDrawing(), a, t -> (t instanceof PointProvider && t != b));
        setPoint1.addValueChangeListener(shape -> a = (PointProvider) shape);
        point1.addProperty(setPoint1);
        point1.addProperties(a.getProperties());

        PropertyGroup point2 = new PropertyGroup(b.getName());
        ShapeProperty setPoint2 = new ShapeProperty(Component.translatable("shape.generic.point_n", 1), b.getDrawing(), b, t -> (t instanceof PointProvider && t != a));
        setPoint2.addValueChangeListener(shape -> b = (PointProvider) shape);
        point2.addProperty(setPoint2);
        point2.addProperties(b.getProperties());

        output.add(point1);
        output.add(point2);
        return output;
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
        PointProvider a = (PointProvider)context.resolveShape(nbt.getLong("a"));
        PointProvider b = (PointProvider)context.resolveShape(nbt.getLong("b"));
        return new TwoPointInfiniteLine(a, b);
    }

    @Override
    public InfiniteLineType<?> type() {
        return InfiniteLineType.TWO_POINTS;
    }

    @Override
    public LineSegment asLineSegment(Drawing draiwng) {
        return new LineSegment(draiwng, a, b);
    }

    @Override
    public Ray asRay(Drawing draiwng) {
        return Ray.of(draiwng, a, b);
    }
}

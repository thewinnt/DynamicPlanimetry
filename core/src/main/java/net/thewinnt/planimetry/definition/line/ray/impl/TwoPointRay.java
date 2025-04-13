package net.thewinnt.planimetry.definition.line.ray.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.definition.line.ray.RayDefinition;
import net.thewinnt.planimetry.definition.line.ray.RayDefinitionType;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.lines.InfiniteLine;
import net.thewinnt.planimetry.shapes.lines.LineSegment;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.text.Component;

public class TwoPointRay extends RayDefinition {
    private PointProvider a;
    private PointProvider b;

    public TwoPointRay(PointProvider a, PointProvider b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public InfiniteLine asInfiniteLine(Drawing drawing) {
        return InfiniteLine.of(drawing, a, b);
    }

    @Override
    public LineSegment asLineSegment(Drawing drawing) {
        return new LineSegment(drawing, a, b);
    }

    @Override
    public boolean canMove() {
        return a.canMove() && b.canMove();
    }

    @Override
    public double direction() {
        return MathHelper.angleTo(a.getPosition(), b.getPosition());
    }

    @Override
    public Component getName() {
        return Component.of(a.getNameComponent(), b.getNameComponent());
    }

    @Override
    public void move(double dx, double dy) {
        a.move(dx, dy);
        b.move(dx, dy);
    }

    @Override
    public Collection<Property<?>> properties() {
        ArrayList<Property<?>> output = new ArrayList<>();
        output.add(new PropertyGroup(Component.translatable(ShapeData.RAY.propertyName("a")), a.getProperties()));
        output.add(new PropertyGroup(Component.translatable(ShapeData.RAY.propertyName("b")), b.getProperties()));
        return output;
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        if (neo instanceof PointProvider point) {
            if (old == a) {
                this.a = point;
            } else if (old == b) {
                this.b = point;
            }
        }
    }

    @Override
    public Vec2 start() {
        return a.getPosition();
    }

    public PointProvider getA() {
        return a;
    }

    public PointProvider getB() {
        return b;
    }

    @Override
    public RayDefinitionType<?> type() {
        return RayDefinitionType.TWO_POINTS;
    }

    @Override
    public List<Shape> dependencies() {
        return List.of(a, b);
    }
}

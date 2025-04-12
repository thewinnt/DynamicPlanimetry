package net.thewinnt.planimetry.definition.line.infinite.impl;

import java.util.Collection;
import java.util.List;

import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineDefinition;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineType;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.lines.LineSegment;
import net.thewinnt.planimetry.shapes.lines.Ray;
import net.thewinnt.planimetry.definition.line.infinite.type.AngleLineType;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.properties.types.ShapeProperty;
import net.thewinnt.planimetry.ui.text.Component;

public class AngleBasedLineDefinition extends InfiniteLineDefinition {
    private PointProvider point;
    private double angle;

    public AngleBasedLineDefinition(PointProvider point, double angle) {
        this.point = point;
        this.angle = angle;
    }

    @Override
    public Vec2 point1() {
        return point.getPosition();
    }

    @Override
    public PointProvider getBasePoint() {
        return point;
    }

    @Override
    public Vec2 point2() {
        return MathHelper.continueFromAngle(point1(), angle, 10);
    }

    @Override
    public boolean canMove() {
        return point.canMove();
    }

    @Override
    public void move(double dx, double dy) {
        point.move(dx, dy);
    }

    @Override
    public Component getName() {
        return point.getNameComponent();
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        if (old == point) {
            this.point = (PointProvider) neo;
        }
    }

    @Override
    public Collection<Property<?>> properties() {
        ShapeProperty pointSelector = new ShapeProperty(Component.translatable("shape.point"), this.point.getDrawing(), this.point, t -> t instanceof PointProvider);
        pointSelector.addValueChangeListener(shape -> this.point = (PointProvider) shape);

        PropertyGroup point = new PropertyGroup(this.point.getName());
        point.addProperty(pointSelector);
        point.addProperties(this.point.getProperties());

        NumberProperty angle = new NumberProperty(createProperty("angle"), this.angle);
        angle.addValueChangeListener(t -> this.angle = t);

        return List.of(point, angle);
    }

    @Override
    public InfiniteLineType<?> type() {
        return AngleLineType.INSTANCE;
    }

    @Override
    public LineSegment asLineSegment(Drawing draiwng) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'asLineSegment'");
    }

    @Override
    public Ray asRay(Drawing draiwng) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'asRay'");
    }

    public PointProvider getPoint() {
        return point;
    }

    public double getAngle() {
        return angle;
    }
}

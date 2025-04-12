package net.thewinnt.planimetry.definition.line.infinite.impl;

import java.util.ArrayList;
import java.util.Collection;

import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineDefinition;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineType;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.shapes.lines.LineSegment;
import net.thewinnt.planimetry.shapes.lines.Ray;
import net.thewinnt.planimetry.definition.line.infinite.type.ParallelLineType;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.properties.types.ShapeProperty;
import net.thewinnt.planimetry.ui.text.Component;

public class ParallelLineDefinition extends InfiniteLineDefinition {
    protected Line line;
    protected PointProvider point;

    public ParallelLineDefinition(Line line, PointProvider point) {
        this.line = line;
        this.point = point;
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
        return MathHelper.continueFromAngle(point.getPosition(), MathHelper.angleTo(line.point1(), line.point2()), 10);
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
        } else if (old == line) {
            this.line = (Line) neo;
        }
    }

    @Override
    public Collection<Property<?>> properties() {
        point.rebuildProperties();
        ArrayList<Property<?>> output = new ArrayList<>();
        PropertyGroup point1 = new PropertyGroup(point.getName());
        ShapeProperty setPoint1 = new ShapeProperty(Component.translatable("shape.point"), point.getDrawing(), point, t -> t instanceof PointProvider);
        setPoint1.addValueChangeListener(shape -> point = (PointProvider) shape);
        point1.addProperty(setPoint1);
        point1.addProperties(point.getProperties());

        PropertyGroup point2 = new PropertyGroup(line.getName());
        ShapeProperty setPoint2 = new ShapeProperty(Component.translatable("shape.line"), line.getDrawing(), line, t -> t instanceof Line && t != getSource());
        setPoint2.addValueChangeListener(shape -> line = (Line) shape);
        point2.addProperty(setPoint2);
        point2.addProperties(line.getProperties());
        output.add(point1);
        output.add(point2);
        return output;
    }

    @Override
    public InfiniteLineType<?> type() {
        return ParallelLineType.INSTANCE;
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

    public Line getLine() {
        return line;
    }

    public PointProvider getPoint() {
        return point;
    }
}

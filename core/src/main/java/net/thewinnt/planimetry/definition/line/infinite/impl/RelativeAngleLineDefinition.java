package net.thewinnt.planimetry.definition.line.infinite.impl;

import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineDefinition;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineType;
import net.thewinnt.planimetry.definition.line.infinite.type.ParallelLineType;
import net.thewinnt.planimetry.definition.line.infinite.type.RelativeAngleLineType;
import net.thewinnt.planimetry.definition.line.ray.impl.DirectionBasedRay;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.shapes.lines.LineSegment;
import net.thewinnt.planimetry.shapes.lines.Ray;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.properties.types.ShapeProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.DynamicValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RelativeAngleLineDefinition extends InfiniteLineDefinition {
    protected Line line;
    protected PointProvider point;
    protected DynamicValue angle;

    public RelativeAngleLineDefinition(Line line, PointProvider point, DynamicValue angle) {
        this.line = line;
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
        return MathHelper.continueFromAngle(point.getPosition(), MathHelper.angleTo(line.point1(), line.point2()) + angle.get(), 10);
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
        return List.of(
            PropertyHelper.swappablePoint(point, t -> point = t, List.of(), false, "shape.generic.point"),
            PropertyHelper.refreshingSetter(new ShapeProperty(Component.translatable("shape.generic.line"), getSource().getDrawing(), line, s -> s instanceof Line), t -> line = (Line) t),
            PropertyHelper.dynamicValue(angle, t -> angle = t, getSource().getPropertyName("angle"))
        );
    }

    @Override
    public InfiniteLineType<?> type() {
        return RelativeAngleLineType.INSTANCE;
    }

    @Override
    public LineSegment asLineSegment(Drawing drawing) {
        Vec2 offset = this.point.getPosition().subtract(line.point1());
        PointProvider b = drawing.getNearestPoint(line.point2().add(offset));
        if (!this.getSource().contains(b.getPosition())) {
            b = PointProvider.simple(drawing, line.point2().add(offset));
            drawing.addShape(b);
        }
        return new LineSegment(drawing, point, b);
    }

    @Override
    public Ray asRay(Drawing drawing) {
        return new Ray(drawing, new DirectionBasedRay(point, MathHelper.angleTo(line.point1(), line.point2())));
    }

    public Line getLine() {
        return line;
    }

    public PointProvider getPoint() {
        return point;
    }

    public DynamicValue getAngle() {
        return angle;
    }

    @Override
    public List<Shape> dependencies() {
        return List.of(point);
    }
}

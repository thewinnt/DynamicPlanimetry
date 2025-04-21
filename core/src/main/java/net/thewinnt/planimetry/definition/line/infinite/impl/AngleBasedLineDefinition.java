package net.thewinnt.planimetry.definition.line.infinite.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineDefinition;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineType;
import net.thewinnt.planimetry.definition.line.ray.impl.DirectionBasedRay;
import net.thewinnt.planimetry.definition.line.ray.impl.TwoPointRay;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.data.ExportedParameter;
import net.thewinnt.planimetry.shapes.data.ExportedParameterType;
import net.thewinnt.planimetry.shapes.lines.LineSegment;
import net.thewinnt.planimetry.shapes.lines.Ray;
import net.thewinnt.planimetry.definition.line.infinite.type.AngleLineType;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.properties.types.ShapeProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.DynamicValue;

public class AngleBasedLineDefinition extends InfiniteLineDefinition {
    private PointProvider point;
    private DynamicValue angle;

    public AngleBasedLineDefinition(PointProvider point, DynamicValue angle) {
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
        return MathHelper.continueFromAngle(point1(), angle.get(), 10);
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
        ShapeProperty pointSelector = new ShapeProperty(createProperty("point"), this.point.getDrawing(), this.point, t -> t instanceof PointProvider);
        pointSelector.addValueChangeListener(shape -> this.point = (PointProvider) shape);

        PropertyGroup point = new PropertyGroup(this.point.getName());
        point.addProperty(pointSelector);
        point.addProperties(this.point.getProperties());

        Property<?> angle = PropertyHelper.dynamicValue(this.angle, this::setAngle, getSource().getPropertyName("angle"));

        return List.of(point, angle);
    }

    @Override
    public InfiniteLineType<?> type() {
        return AngleLineType.INSTANCE;
    }

    @Override
    public LineSegment asLineSegment(Drawing drawing) {
        Vec2 a = point.getPosition();
        double angle = this.angle.get();
        Optional<PointProvider> b = drawing.getPoints().stream()
                                        .min(Comparator.comparingDouble(
                                            value -> Math.abs(MathHelper.angleTo(a, value.getPosition()) - angle))
                                        );
        if (b.isPresent()) {
            return new LineSegment(drawing, point, b.get());
        } else {
            PointProvider point2 = PointProvider.simple(drawing, MathHelper.continueFromAngle(a, angle, 25));
            drawing.addShape(point2);
            return new LineSegment(drawing, point, point2);
        }
    }

    @Override
    public Ray asRay(Drawing drawing) {
        return new Ray(drawing, new DirectionBasedRay(point, angle.get()));
    }

    public PointProvider getPoint() {
        return point;
    }

    public DynamicValue getAngle() {
        return angle;
    }

    public void setAngle(DynamicValue angle) {
        this.angle = angle;
    }

    @Override
    public List<Shape> dependencies() {
        return List.of(point);
    }

    @Override
    public void registerParameters(BiConsumer<ExportedParameterType, ExportedParameter<?>> consumer) {

    }

    @Override
    public void onParameterUnlock(ExportedParameterType type) {

    }

    @Override
    public void onParameterLock(ExportedParameterType type) {

    }
}

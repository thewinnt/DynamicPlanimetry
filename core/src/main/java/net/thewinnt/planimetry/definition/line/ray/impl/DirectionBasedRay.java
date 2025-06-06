package net.thewinnt.planimetry.definition.line.ray.impl;

import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.definition.line.infinite.impl.AngleBasedLineDefinition;
import net.thewinnt.planimetry.definition.line.ray.RayDefinition;
import net.thewinnt.planimetry.definition.line.ray.RayDefinitionType;
import net.thewinnt.planimetry.definition.line.ray.type.DirectionBasedRayType;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.lines.InfiniteLine;
import net.thewinnt.planimetry.shapes.lines.LineSegment;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.type.ConstantValue;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DirectionBasedRay extends RayDefinition {
    private PointProvider start;
    private double direction;

    public DirectionBasedRay(PointProvider start, double direction) {
        this.start = start;
        this.direction = direction;
    }

    @Override
    public Vec2 start() {
        return start.getPosition();
    }

    @Override
    public double direction() {
        return direction;
    }

    @Override
    public boolean canMove() {
        return start.canMove();
    }

    @Override
    public void move(double dx, double dy) {
        start.move(dx, dy);
    }

    @Override
    public Component getName() {
        return start.getNameComponent();
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        if (old == start && neo instanceof PointProvider point) {
            this.start = point;
        }
    }

    public PointProvider getStart() {
        return start;
    }

    @Override
    public Collection<Property<?>> properties() {
        start.rebuildProperties();
        Property<?> point = PropertyHelper.swappablePoint(start, t -> start = t, List.of(), true, getSource().getPropertyName("start"));
        NumberProperty angle = PropertyHelper.angle(getSource().getPropertyName("angle"), direction, t -> direction = t);
        return List.of(point, angle);
    }

    @Override
    public RayDefinitionType<?> type() {
        return DirectionBasedRayType.INSTANCE;
    }

    @Override
    public InfiniteLine asInfiniteLine(Drawing drawing) {
        return new InfiniteLine(drawing, new AngleBasedLineDefinition(start, new ConstantValue(direction)));
    }

    @Override
    public LineSegment asLineSegment(Drawing drawing) {
        Vec2 a = start.getPosition();
        Optional<PointProvider> b = drawing.getPoints().stream()
                                        .min(Comparator.comparingDouble(
                                            value -> Math.abs(MathHelper.angleTo(a, value.getPosition()) - direction))
                                        );
        if (b.isPresent()) {
            return new LineSegment(drawing, start, b.get());
        } else {
            PointProvider point2 = PointProvider.simple(drawing, MathHelper.continueFromAngle(a, direction, 25));
            drawing.addShape(point2);
            return new LineSegment(drawing, start, point2);
        }
    }

    @Override
    public List<Shape> dependencies() {
        return List.of(start);
    }
}

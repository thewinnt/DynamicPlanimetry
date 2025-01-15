package net.thewinnt.planimetry.shapes.point.relative;

import java.util.Collection;
import java.util.List;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.NameComponentProperty;
import net.thewinnt.planimetry.ui.properties.types.ShapeProperty;
import net.thewinnt.planimetry.ui.text.Component;

public class LineSegmentCenter extends PointProvider {
    private Line segment;
    private final ShapeProperty lineProperty;
    private final NameComponentProperty componentProperty;

    public LineSegmentCenter(Drawing drawing, Line segment) {
        super(drawing);
        this.lineProperty = new ShapeProperty(Component.translatable("property.line_segment_center.line"), drawing, segment, shape -> shape instanceof Line && shape != getSegment());
        this.lineProperty.addValueChangeListener(shape -> setSegment((Line)shape));
        this.componentProperty = new NameComponentProperty(Component.translatable("property.line_segment_center.name"), this.name);
        this.componentProperty.addValueChangeListener(this::setName);
        this.addDependency(segment);
        segment.addDepending(this);
        this.segment = segment;
    }

    @Override
    public Vec2 getPosition() {
        return segment.a.getPosition().lerp(segment.b.getPosition(), 0.5);
    }

    @Override
    protected Collection<Property<?>> moreProperties() {
        return List.of(lineProperty, componentProperty);
    }

    @Override
    protected boolean shouldAutoAssingnName() {
        return false;
    }

    public Line getSegment() {
        return segment;
    }

    public void setSegment(Line segment) {
        this.segment = segment;
    }

    @Override
    public void move(Vec2 delta) {}

    @Override
    public void move(double dx, double dy) {}

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        super.replaceShape(old, neo);
        if (old == segment) {
            setSegment((Line)neo);
        }
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        context.addShape(segment);
        CompoundTag output = new CompoundTag();
        output.putLong("line", segment.getId());
        return output;
    }

    @Override
    public ShapeDeserializer<?> getDeserializer() {
        return ShapeData.LINE_SEGMENT_CENTER;
    }


    public static LineSegmentCenter readNbt(CompoundTag nbt, LoadingContext context) {
        Line line = (Line)context.resolveShape(nbt.getLong("line"));
        return new LineSegmentCenter(context.getDrawing(), line);
    }
}

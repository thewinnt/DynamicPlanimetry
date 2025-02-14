package net.thewinnt.planimetry.shapes.lines;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.SegmentLike;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.functions.Function;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class LineSegment extends Line implements SegmentLike {
    public PointProvider a;
    public PointProvider b;

    public LineSegment(Drawing drawing, PointProvider a, PointProvider b) {
        super(drawing);
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean contains(double x, double y) {
        return a.getPosition().distanceTo(x, y) + b.getPosition().distanceTo(x, y) - a.getPosition().distanceTo(b.getPosition()) < Math.pow(2, -23);
    }

    @Override
    public boolean contains(Vec2 point) {
        return a.getPosition().distanceTo(point) + b.getPosition().distanceTo(point) - a.getPosition().distanceTo(b.getPosition()) < Math.pow(2, -23);
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        double distance = Math.abs((b.x - a.x)*(a.y - point.y) - (a.x - point.x)*(b.y - a.y)) / a.distanceTo(b);
        double slope = this.getSlope();
        if (!this.contains(MathHelper.perpendicular(point, slope, distance)) && !this.contains(MathHelper.perpendicular(point, slope, -distance))) {
            return Math.min(a.distanceTo(point), b.distanceTo(point));
        } else {
            return distance;
        }
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return distanceToMouse(new Vec2(x, y), board);
    }

    @Override
    public double distanceTo(Vec2 point) {
        return distanceToMouse(point, null);
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        Color lineColor = this.getColor(selection);
        Vec2 a, b;
        a = this.a.getPosition();
        b = this.b.getPosition();
        drawer.line(board.bx(a.x), board.by(a.y), board.bx(b.x), board.by(b.y), lineColor, getThickness(board.getScale()));
        if (selection == SelectionStatus.SELECTED) {
            if (!board.getShapes().contains(this.a)) {
                this.a.render(drawer, SelectionStatus.NONE, font, board);
            }
            if (!board.getShapes().contains(this.b)) {
                this.b.render(drawer, SelectionStatus.NONE, font, board);
            }
        }
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
    public void move(Vec2 delta) {
        a.move(delta);
        b.move(delta);
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
    public Line convertTo(LineType other) {
        return switch (other) {
            case INFINITE -> InfiniteLine.of(drawing, a, b);
            case RAY -> Ray.of(drawing, a, b);
            case SEGMENT -> this;
        };
    }

    @Override
    public Component getName() {
        return Component.of(Component.translatable(getTypeName()), a.getNameComponent(), b.getNameComponent());
    }

    @Override
    public void rebuildProperties() {
        this.properties.add(new PropertyGroup(this.a.getName(), this.a.getProperties()));
        this.properties.add(new PropertyGroup(this.b.getName(), this.b.getProperties()));
        this.properties.add(new DisplayProperty(Component.translatable("property.line_segment.length"), () -> Component.number(this.a.getPosition().distanceTo(this.b.getPosition()))));
    }

    @Override
    public Collection<Function<?>> getFunctions() {
        Collection<Function<?>> output = super.getFunctions();
        // output.add(new BasicNamedFunction<>(drawing, this, s -> {
        //     LineSegmentCenter center = new LineSegmentCenter(drawing, s);
        //     center.setShouldRender(false);
        //     drawing.addShape(center);
        //     drawing.addShape(new AngledInfiniteLine(drawing, this, center, MathHelper.HALF_PI));
        // }, Component.translatable("function.line_segment.create_perpendicular_bisector"), Component.translatable("function.line_segment.create_perpendicular_bisector.action")));
        return output;
    }

    @Override
    public String getTypeName() {
        return "shape.line_segment";
    }

    @Override
    public Collection<SegmentLike> asSegments() {
        return List.of(this);
    }

    @Override
    public ShapeDeserializer<LineSegment> type() {
        return ShapeData.LINE_SEGMENT;
    }

    @Override
    protected CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("a", a.getId());
        context.addShape(a);
        nbt.putLong("b", b.getId());
        context.addShape(b);
        return nbt;
    }

    public static LineSegment readNbt(CompoundTag nbt, LoadingContext context) {
        PointReference a = (PointReference)context.resolveShape(nbt.getLong("a"));
        PointReference b = (PointReference)context.resolveShape(nbt.getLong("b"));
        return new LineSegment(context.getDrawing(), a, b);
    }

    @Override
    public LineType getType() {
        return LineType.SEGMENT;
    }
}

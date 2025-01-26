package net.thewinnt.planimetry.shapes.lines;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.factories.LineFactory.LineType;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.shapes.point.relative.LineSegmentCenter;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.functions.BasicNamedFunction;
import net.thewinnt.planimetry.ui.functions.Function;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class LineSegment extends Line {
    public LineSegment(Drawing drawing, PointProvider a, PointProvider b) {
        super(drawing, a, b);
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
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        Color lineColor = switch (selection) {
            default -> Theme.current().shape();
            case HOVERED -> Theme.current().shapeHovered();
            case SELECTED -> Theme.current().shapeSelected();
        };
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
    public Collection<Property<?>> getProperties() {
        PropertyGroup a = new PropertyGroup(this.a.getName(), this.a.getProperties());
        PropertyGroup b = new PropertyGroup(this.b.getName(), this.b.getProperties());
        DisplayProperty length = new DisplayProperty(Component.translatable("property.line_segment.length"), () -> Component.number(this.a.getPosition().distanceTo(this.b.getPosition())));
        return List.of(a, b, length);
    }

    @Override
    public Collection<Function<?>> getFunctions() {
        Collection<Function<?>> output = super.getFunctions();
        output.add(new BasicNamedFunction<>(drawing, this, s -> {
            LineSegmentCenter center = new LineSegmentCenter(drawing, s);
            center.setShouldRender(false);
            drawing.addShape(center);
            drawing.addShape(new AngledInfiniteLine(drawing, this, center, MathHelper.HALF_PI));
        }, Component.translatable("function.line_segment.create_perpendicular_bisector"), Component.translatable("function.line_segment.create_perpendicular_bisector.action")));
        return output;
    }

    @Override
    public String getTypeName() {
        return "shape.line_segment";
    }

    @Override
    public ShapeDeserializer<LineSegment> getDeserializer() {
        return ShapeData.LINE_SEGMENT;
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

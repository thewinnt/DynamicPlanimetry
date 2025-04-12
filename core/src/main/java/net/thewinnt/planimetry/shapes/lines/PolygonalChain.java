package net.thewinnt.planimetry.shapes.lines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.LongArrayTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.AABB;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Segment;
import net.thewinnt.planimetry.math.SegmentLike;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.polygons.Polygon;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class PolygonalChain extends Shape {
    public final List<PointProvider> points;
    private List<SegmentLike> segmentCache;

    public PolygonalChain(Drawing drawing, PointProvider... points) {
        super(drawing);
        this.points = new ArrayList<>(points.length);
        for (PointProvider i : points) {
            this.points.add(i);
            this.addDependency(i);
            i.addDepending(this);
        }
    }

    public PolygonalChain(Drawing drawing, Collection<PointProvider> points) {
        super(drawing);
        this.points = new ArrayList<>(points);
        for (PointProvider i : this.points) {
            this.addDependency(i);
            i.addDepending(this);
        }
    }

    public void addPoint(PointProvider point) {
        this.points.add(point);
        this.addDependency(point);
        point.addDepending(this);
        segmentCache = null;
    }

    public List<PointProvider> getPoints() {
        return points;
    }

    @Override
    public boolean contains(Vec2 point) {
        for (int i = 0; i < points.size() - 1; i++) {
            if (MathHelper.isPointOnSegment(points.get(i).getPosition(), points.get(i + 1).getPosition(), point)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(double x, double y) {
        for (int i = 0; i < points.size() - 1; i++) {
            if (MathHelper.isPointOnSegment(points.get(i).getPosition(), points.get(i + 1).getPosition(), new Vec2(x, y))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < points.size() - 1; i++) {
            double cache = MathHelper.distanceToSegment(points.get(i).getPosition(), points.get(i + 1).getPosition(), point);
            if (cache < minDist) {
                minDist = cache;
            }
        }
        return minDist;
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < points.size() - 1; i++) {
            double cache = MathHelper.distanceToSegment(points.get(i).getPosition(), points.get(i + 1).getPosition(), new Vec2(x, y));
            if (cache < minDist) {
                minDist = cache;
            }
        }
        return minDist;
    }

    @Override
    public void rebuildProperties() {
        properties.clear();
        for (PointProvider i : points) {
            i.rebuildProperties();
            properties.add(new PropertyGroup(i.getName(), i.getProperties()));
        }
        properties.add(new DisplayProperty(Component.translatable(getPropertyName("length")), () -> Component.number(getPerimeter())));
    }

    public double getPerimeter() {
        Vec2 prevPoint = points.get(0).getPosition();
        double totalLength = 0;
        for (PointProvider i : points) {
            totalLength += i.getPosition().distanceTo(prevPoint);
            prevPoint = i.getPosition();
        }
        return totalLength;
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        Color lineColor = this.getColor(selection);
        if (selection == SelectionStatus.SELECTED) {
            for (PointProvider i : points) {
                if (!board.getShapes().contains(i)) {
                    i.render(drawer, SelectionStatus.NONE, font, board);
                }
            }
        }
        Vec2 a, b;
        for (int i = 0; i < points.size() - 1; i++) {
            a = points.get(i).getPosition();
            b = points.get(i + 1).getPosition();
            drawer.line(board.bx(a.x), board.by(a.y), board.bx(b.x), board.by(b.y), lineColor, getThickness(board.getScale()));
        }
    }

    @Override
    public Component getName() {
        if (nameOverride != null) return nameOverride;
        Component[] output = new Component[this.points.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = this.points.get(i).getNameComponent();
        }
        return Component.translatable(getTypeName(), Component.of(output));
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i) == old) {
                points.set(i, (PointProvider)neo);
                segmentCache = null;
                break;
            }
        }
    }

    @Override
    public ShapeDeserializer<?> type() {
        return ShapeData.POLYGONAL_CHAIN;
    }

    @Override
    public String getPropertyName(String postfix) {
        return "property.dynamic_planimetry.polygonal_chain." + postfix;
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        long[] points = new long[this.points.size()];
        for (int i = 0; i < points.length; i++) {
            points[i] = this.points.get(i).getId();
            context.addShape(this.points.get(i));
        }
        LongArrayTag pointsTag = new LongArrayTag(points);
        nbt.put("points", pointsTag);
        return nbt;
    }

    protected static List<PointProvider> pointsFromNbt(CompoundTag nbt, LoadingContext context) {
        long[] points = nbt.getLongArray("points");
        List<PointProvider> output = new ArrayList<>();
        for (long i : points) {
            output.add((PointProvider)context.resolveShape(i));
        }
        return output;
    }

    public static PolygonalChain readNbt(CompoundTag nbt, LoadingContext context) {
        return new PolygonalChain(context.getDrawing(), pointsFromNbt(nbt, context));
    }

    @Override
    public void move(Vec2 delta) {
        for (PointProvider i : this.points) {
            i.move(delta);
        }
    }

    @Override
    public void move(double dx, double dy) {
        for (PointProvider i : this.points) {
            i.move(dx, dy);
        }
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public Collection<SegmentLike> asSegments() {
        if (segmentCache == null) {
            if (this instanceof Polygon) {
                segmentCache = new ArrayList<>(this.points.size());
                segmentCache.set(0, new Segment(points.get(0).getPosition(), points.get(points.size()).getPosition()));
                for (int i = 1; i < segmentCache.size(); i++) {
                    segmentCache.set(i, new Segment(points.get(i - 1).getPosition(), points.get(i).getPosition()));
                }
            } else {
                segmentCache = new ArrayList<>(this.points.size() - 1);
                for (int i = 1; i < segmentCache.size(); i++) {
                    segmentCache.set(i - 1, new Segment(points.get(i - 1).getPosition(), points.get(i).getPosition()));
                }
            }
        }
        return segmentCache;
    }

    @Override
    public boolean intersects(AABB aabb) {
        for (SegmentLike i : asSegments()) {
            for (SegmentLike j : aabb.asLineSegments()) {
                if (i.intersection(j).isPresent()) return true;
            }
        }
        return false;
    }

    @Override
    public Collection<Vec2> intersections(Shape other) {
        Set<Vec2> output = new HashSet<>();
        if (other instanceof Line line) {
            for (SegmentLike i : this.asSegments()) {
                line.intersect(i).ifPresent(output::add);
            }
        } else {
            Collection<SegmentLike> segments = other.asSegments();
            if (segments.isEmpty()) {
                for (SegmentLike i : this.asSegments()) {
                    output.addAll(other.intersections(i));
                }
            } else {
                for (SegmentLike i : this.asSegments()) {
                    for (SegmentLike j : segments) {
                        i.intersection(j).ifPresent(output::add);
                    }
                }
            }
        }
        return output;
    }

    @Override
    public Collection<Vec2> intersections(SegmentLike other) {
        // TODO Auto-generated method stub
        return null;
    }
}

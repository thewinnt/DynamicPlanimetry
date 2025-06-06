package net.thewinnt.planimetry.shapes.polygons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.lines.PolygonalChain;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Polygon extends PolygonalChain {
    public final List<DisplayProperty> angles = new ArrayList<>();

    public Polygon(Drawing drawing, PointProvider... points) {
        super(drawing, points);
        if (points.length < 3) {
            throw new IllegalArgumentException("A Polygon must have at least three points");
        }
        recalculateAngles();
    }

    public Polygon(Drawing drawing, Collection<PointProvider> points) {
        super(drawing, points);
        if (points.size() < 3) {
            throw new IllegalArgumentException("A Polygon must have at least three points");
        }
        recalculateAngles();
    }

    @Override
    public boolean contains(Vec2 point) {
        for (int i = 0; i < points.size() - 1; i++) {
            if (MathHelper.isPointOnSegment(points.get(i).getPosition(), points.get(i + 1).getPosition(), point)) {
                return true;
            }
        }
        return MathHelper.isPointOnSegment(points.get(0).getPosition(), points.get(points.size() - 1).getPosition(), point);
    }

    @Override
    public boolean contains(double x, double y) {
        for (int i = 0; i < points.size() - 1; i++) {
            if (MathHelper.isPointOnSegment(points.get(i).getPosition(), points.get(i + 1).getPosition(), new Vec2(x, y))) {
                return true;
            }
        }
        return MathHelper.isPointOnSegment(points.get(0).getPosition(), points.get(points.size() - 1).getPosition(), new Vec2(x, y));
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
        double cache = MathHelper.distanceToSegment(points.get(0).getPosition(), points.get(points.size() - 1).getPosition(), point);
        if (cache < minDist) {
            minDist = cache;
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
        double cache = MathHelper.distanceToSegment(points.get(0).getPosition(), points.get(points.size() - 1).getPosition(), new Vec2(x, y));
        if (cache < minDist) {
            minDist = cache;
        }
        return minDist;
    }

    @Override
    public void rebuildProperties() {
        recalculateAngles();
        super.rebuildProperties();
        properties.add(new DisplayProperty(Component.translatable(getPropertyName("area")), () -> Component.number(getArea())));
        properties.add(new PropertyGroup(Component.translatable(getPropertyName("angles")), angles));
    }

    @Override
    public double getPerimeter() {
        Vec2 prevPoint = points.get(0).getPosition();
        double totalLength = 0;
        for (PointProvider i : points) {
            totalLength += i.getPosition().distanceTo(prevPoint);
            prevPoint = i.getPosition();
        }
        return totalLength + prevPoint.distanceTo(points.get(0).getPosition());
    }

    @Override
    public void addPoint(PointProvider point) {
        super.addPoint(point);
        recalculateAngles();
    }

    private void recalculateAngles() {
        angles.clear();
        PointProvider[] pts = points.toArray(new PointProvider[0]);
        // for each angle in the polygon, add a new display property consisting of:
        // - a translation string, with 3 adjacent points' names as arguments
        // - the angle, formatted as one
        angles.add(new DisplayProperty(Component.translatable(getPropertyName("angle_value"), pts[1].getNameComponent(), pts[0].getNameComponent(), pts[pts.length - 1].getNameComponent()), () -> Component.angle(MathHelper.angle(pts[1].getPosition(), pts[0].getPosition(), pts[pts.length - 1].getPosition()))));
        for (int i = 1; i < pts.length - 1; i++) {
            final int j = i;
            angles.add(new DisplayProperty(Component.translatable(getPropertyName("angle_value"), pts[j-1].getNameComponent(), pts[j].getNameComponent(), pts[j+1].getNameComponent()), () -> Component.angle(MathHelper.angle(pts[j-1].getPosition(), pts[j].getPosition(), pts[j+1].getPosition()))));
        }
        angles.add(new DisplayProperty(Component.translatable(getPropertyName("angle_value"), pts[pts.length - 2].getNameComponent(), pts[pts.length - 1].getNameComponent(), pts[0].getNameComponent()), () -> Component.angle(MathHelper.angle(pts[pts.length - 2].getPosition(), pts[pts.length - 1].getPosition(), pts[0].getPosition()))));
    }

    public double getArea() {
        double xa = 0;
        double ya = 0;
        for (int i = this.points.size() - 1; i > 0; i--) {
            Vec2 pos1 = this.points.get(i).getPosition();
            Vec2 pos2 = this.points.get(i - 1).getPosition();
            xa += pos1.x * pos2.y;
            ya = pos1.y * pos2.x;
        }
        return Math.abs((xa - ya) / 2);
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        super.render(drawer, selection, font, board);
        Color lineColor = this.getColor(selection);
        Vec2 a, b;
        a = points.get(0).getPosition();
        b = points.get(points.size() - 1).getPosition();
        drawer.line(board.bx(a.x), board.by(a.y), board.bx(b.x), board.by(b.y), lineColor, getThickness(board.getScale()));
    }

    @Override
    public String getTypeName() {
        String simple = "shape.dynamic_planimetry.polygon." + points.size();
        if (DynamicPlanimetry.getInstance().getCurrentLanguage().hasKey(simple)) {
            return simple;
        }
        return super.getTypeName();
    }

    @Override
    public String getPropertyName(String postfix) {
        return "property.dynamic_planimetry.polygon." + postfix;
    }

    @Override
    public ShapeDeserializer<?> type() {
        return ShapeData.POLYGON;
    }

    public static Polygon readNbt(CompoundTag nbt, LoadingContext context) {
        return new Polygon(context.getDrawing(), pointsFromNbt(nbt, context));
    }
}

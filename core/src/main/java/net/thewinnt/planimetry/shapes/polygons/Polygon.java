package net.thewinnt.planimetry.shapes.polygons;

import java.util.Collection;

import com.badlogic.gdx.graphics.Color;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.lines.MultiPointLine;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Polygon extends MultiPointLine {
    public Polygon(Drawing drawing, PointProvider... points) {
        super(drawing, points);
    }

    public Polygon(Drawing drawing, Collection<PointProvider> points) {
        super(drawing, points);
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
    public Collection<Property<?>> getProperties() {
        var prev = super.getProperties();
        prev.add(new DisplayProperty(Component.literal("Площадь"), () -> Component.number(getArea())));
        return prev;
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
        Color lineColor = switch (selection) {
            default -> Theme.current().shape();
            case HOVERED -> Theme.current().shapeHovered();
            case SELECTED -> Theme.current().shapeSelected();
        };
        Vec2 a, b;
        a = points.get(0).getPosition();
        b = points.get(points.size() - 1).getPosition();
        drawer.line(board.bx(a.x), board.by(a.y), board.bx(b.x), board.by(b.y), lineColor, getThickness(board.getScale()));
    }

    @Override
    public String getTypeName() {
        return "Многоугольник ";
    }

    @Override
    public ShapeDeserializer<?> getDeserializer() {
        return ShapeData.POLYGON;
    }

    public static Polygon readNbt(CompoundTag nbt, LoadingContext context) {
        return new Polygon(context.getDrawing(), pointsFromNbt(nbt, context));
    }
}

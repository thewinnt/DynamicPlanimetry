package net.thewinnt.planimetry.shapes.lines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import dev.dewy.nbt.tags.array.LongArrayTag;
import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.EnclosingProperty;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class MultiPointLine extends Shape {
    public final List<PointProvider> points;

    public MultiPointLine(PointProvider... points) {
        this.points = new ArrayList<>(points.length);
        for (PointProvider i : points) {
            this.points.add(i);
        }
    }

    public MultiPointLine(Collection<PointProvider> points) {
        this.points = new ArrayList<>(points);
    }

    public void addPoint(PointProvider point) {
        this.points.add(point);
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
            double cache = MathHelper.distanceToLine(points.get(i).getPosition(), points.get(i + 1).getPosition(), point);
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
            double cache = MathHelper.distanceToLine(points.get(i).getPosition(), points.get(i + 1).getPosition(), new Vec2(x, y));
            if (cache < minDist) {
                minDist = cache;
            }
        }
        return minDist;
    }

    @Override
    public Collection<Property<?>> getProperties() {
        List<Property<?>> properties = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            properties.add(new EnclosingProperty("Точка " + i, points.get(i).getProperties()));
        }
        return properties;
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        Color lineColor = switch (selection) {
            default -> DynamicPlanimetry.COLOR_SHAPE;
            case HOVERED -> DynamicPlanimetry.COLOR_SHAPE_HOVER;
            case SELECTED -> DynamicPlanimetry.COLOR_SHAPE_SELECTED;
        };
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

    /**
     * Returns whether this particular ordered set of points can be converted into this
     * polygon.
     * @param points A set of points, as if this was just 
     * @return
     */
    public boolean validArrangement(List<PointProvider> points) {
        return true;
    }

    @Override
    public String getName() {
        StringBuilder builder = new StringBuilder();
        for (PointProvider i : this.points) {
            builder.append(i.getName());
        }
        return builder.toString();
    }

    @Override
    public String getTypeName() {
        return "Ломаная";
    }

    @Override
    public ShapeDeserializer<?> getDeserializer() {
        return ShapeData.POLYGONAL_CHAIN;
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
        LongArrayTag points = nbt.getLongArray("points");
        List<PointProvider> output = new ArrayList<>();
        for (long i : points) {
            output.add((PointProvider)context.resolveShape(i));
        }
        return output;
    }

    public static MultiPointLine readNbt(CompoundTag nbt, LoadingContext context) {
        return new MultiPointLine(pointsFromNbt(nbt, context));
    }
}

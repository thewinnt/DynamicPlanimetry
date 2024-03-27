package net.thewinnt.planimetry.shapes.lines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import dev.dewy.nbt.tags.array.LongArrayTag;
import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.properties.types.EnclosingProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class MultiPointLine extends Shape {
    public final List<PointProvider> points;

    public MultiPointLine(Drawing drawing, PointProvider... points) {
        super(drawing);
        this.points = new ArrayList<>(points.length);
        for (PointProvider i : points) {
            this.points.add(i);
            this.addDependency(i);
            i.addDepending(this);
        }
    }

    public MultiPointLine(Drawing drawing, Collection<PointProvider> points) {
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
        for (PointProvider i : points) {
            properties.add(new EnclosingProperty(i.getName(), i.getProperties()));
        }
        properties.add(new DisplayProperty(Component.translatable("property.polygonal_chain.length"), () -> Component.number(getPerimeter())));
        return properties;
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
        Color lineColor = switch (selection) {
            default -> Theme.current().shape();
            case HOVERED -> Theme.current().shapeHovered();
            case SELECTED -> Theme.current().shapeSelected();
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

    @Override
    public Component getName() {
        if (nameOverride != null) return nameOverride;
        ArrayList<Component> output = new ArrayList<>();
        for (PointProvider i : this.points) {
            output.add(i.getNameComponent());
        }
        return Component.translatable(getTypeName(), output.toArray());
    }

    @Override
    public void replaceShape(Shape old, Shape neo) {
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i) == old) {
                points.set(i, (PointProvider)neo);
                break;
            }
        }
    }

    @Override
    public String getTypeName() {
        return "shape.polygonal_chain";
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
        return new MultiPointLine(context.getDrawing(), pointsFromNbt(nbt, context));
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
}

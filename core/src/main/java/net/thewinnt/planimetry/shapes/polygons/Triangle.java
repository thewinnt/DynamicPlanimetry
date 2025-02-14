package net.thewinnt.planimetry.shapes.polygons;

import java.util.List;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.shapes.point.PointProvider;

public class Triangle extends Polygon {
    private final PointProvider a;
    private final PointProvider b;
    private final PointProvider c;

    public Triangle(Drawing drawing, PointProvider a, PointProvider b, PointProvider c) {
        super(drawing, List.of(a, b, c));
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Triangle(Polygon triangle) {
        super(triangle.getDrawing(), triangle.points);
        this.a = triangle.points.get(0);
        this.b = triangle.points.get(1);
        this.c = triangle.points.get(2);
    }

    @Override
    public void addPoint(PointProvider point) {
        throw new UnsupportedOperationException("Triangles can only have 3 points");
    }

    @Override
    public double getArea() {
        return MathHelper.distanceToLine(a.getPosition(), b.getPosition(), c.getPosition()) * a.getPosition().distanceTo(b.getPosition()) / 2.0;
    }

    @Override
    public String getTypeName() {
        return "shape.polygon.3";
    }

    @Override
    public ShapeDeserializer<?> type() {
        return ShapeData.TRIANGLE;
    }

    public boolean isEquilateral() {
        return MathHelper.roughlyEquals(a.distanceToMouse(b.getPosition(), null), a.distanceToMouse(c.getPosition(), null)) &&
               MathHelper.roughlyEquals(a.distanceToMouse(b.getPosition(), null), b.distanceToMouse(c.getPosition(), null));
    }

    public static Triangle readNbt(CompoundTag nbt, LoadingContext context) {
        var points = pointsFromNbt(nbt, context);
        return new Triangle(context.getDrawing(), points.get(0), points.get(1), points.get(2));
    }
}

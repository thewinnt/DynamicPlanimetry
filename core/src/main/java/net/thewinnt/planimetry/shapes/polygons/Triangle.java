package net.thewinnt.planimetry.shapes.polygons;

import java.util.Collection;
import java.util.List;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.text.Component;

public class Triangle extends Polygon {
    private final PointProvider a;
    private final PointProvider b;
    private final PointProvider c;

    private final DisplayProperty angleA;
    private final DisplayProperty angleB;
    private final DisplayProperty angleC;

    public Triangle(Drawing drawing, PointProvider a, PointProvider b, PointProvider c) {
        super(drawing, List.of(a, b, c));
        this.a = a;
        this.b = b;
        this.c = c;
        this.angleA = new DisplayProperty(Component.of(Component.literal("Угол "), a.getName()), () -> Component.number(Math.toDegrees(MathHelper.angle(b.getPosition(), a.getPosition(), c.getPosition()))));
        this.angleB = new DisplayProperty(Component.of(Component.literal("Угол "), b.getName()), () -> Component.number(Math.toDegrees(MathHelper.angle(a.getPosition(), b.getPosition(), c.getPosition()))));
        this.angleC = new DisplayProperty(Component.of(Component.literal("Угол "), c.getName()), () -> Component.number(Math.toDegrees(MathHelper.angle(a.getPosition(), c.getPosition(), b.getPosition()))));
    }

    public Triangle(Polygon triangle) {
        super(triangle.getDrawing(), triangle.points);
        this.a = triangle.points.get(0);
        this.b = triangle.points.get(1);
        this.c = triangle.points.get(2);
        this.angleA = new DisplayProperty(Component.of(Component.literal("Угол "), a.getName()), () -> Component.number(Math.toDegrees(MathHelper.angle(b.getPosition(), a.getPosition(), c.getPosition()))));
        this.angleB = new DisplayProperty(Component.of(Component.literal("Угол "), b.getName()), () -> Component.number(Math.toDegrees(MathHelper.angle(a.getPosition(), b.getPosition(), c.getPosition()))));
        this.angleC = new DisplayProperty(Component.of(Component.literal("Угол "), c.getName()), () -> Component.number(Math.toDegrees(MathHelper.angle(a.getPosition(), c.getPosition(), b.getPosition()))));
    }

    @Override
    public void addPoint(PointProvider point) {
        throw new UnsupportedOperationException("Triangles can only have 3 points");
    }

    @Override
    public double getArea() {
        return MathHelper.distanceToLine(a.getPosition(), b.getPosition(), c.getPosition());
    }

    @Override
    public Collection<Property<?>> getProperties() {
        var prev = super.getProperties();
        prev.add(angleA);
        prev.add(angleB);
        prev.add(angleC);
        return prev;
    }

    @Override
    public String getTypeName() {
        return "Треугольник ";
    }
    
    @Override
    public ShapeDeserializer<?> getDeserializer() {
        return ShapeData.TRIANGLE;
    }

    public static Triangle readNbt(CompoundTag nbt, LoadingContext context) {
        var points = pointsFromNbt(nbt, context);
        return new Triangle(context.getDrawing(), points.get(0), points.get(1), points.get(2));
    }
}

package net.thewinnt.planimetry;

import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.Registry;
import net.thewinnt.planimetry.shapes.Circle;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.Shape.ShapeDeserializer;
import net.thewinnt.planimetry.shapes.display.angle.PointAngleMarker;
import net.thewinnt.planimetry.shapes.lines.*;
import net.thewinnt.planimetry.shapes.point.*;
import net.thewinnt.planimetry.shapes.polygons.*;

public class ShapeData {
    // POINTS
    public static final ShapeDeserializer<PointProvider> POINT = register("point", PointProvider::readNbt);

    // LINES
    public static final ShapeDeserializer<InfiniteLine> INFINITE_LINE = register("infinite_line", InfiniteLine::readNbt);
    public static final ShapeDeserializer<LineSegment> LINE_SEGMENT = register("line_segment", LineSegment::readNbt);
    public static final ShapeDeserializer<Ray> RAY = register("ray", Ray::readNbt);

    // POLYGONS
    public static final ShapeDeserializer<PolygonalChain> POLYGONAL_CHAIN = register("polygonal_chain", PolygonalChain::readNbt);
    public static final ShapeDeserializer<Polygon> POLYGON = register("polygon", Polygon::readNbt);
    public static final ShapeDeserializer<Polygon> TRIANGLE = register("triangle", Triangle::readNbt);

    // CIRCLES
    public static final ShapeDeserializer<Circle> CIRCLE = register("circle", Circle::readNbt);

    // ANGLE MARKERS
    public static final ShapeDeserializer<PointAngleMarker> POINT_ANGLE_MARKER = register("point_angle_marker", PointAngleMarker::readNbt);

    public static <T extends Shape> ShapeDeserializer<T> register(String name, ShapeDeserializer<T> deserializer) {
        return Registry.register(Registries.SHAPE_TYPE, deserializer, new Identifier(name));
    }

    public static ShapeDeserializer<?> getDeserializer(Identifier name) {
        return Registries.SHAPE_TYPE.get(name);
    }

    public static Identifier getShapeType(ShapeDeserializer<?> deserializer) {
        return Registries.SHAPE_TYPE.getName(deserializer);
    }

    public static Polygon asSpecificPolygon(Polygon generic) {
        return switch (generic.points.size()) {
            case 3 -> new Triangle(generic);
            case 4 -> new Tetragon(generic);
            default -> generic;
        };
    }

    public static String polygonName(int limit) {
        if (limit <= 4) {
            return "shape.factory.polygon.sized." + limit;
        }
        return "shape.factory.polygon.sized.n";
    }

    public static void init() {}
}

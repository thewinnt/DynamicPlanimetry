package net.thewinnt.planimetry;

import net.thewinnt.planimetry.shapes.Circle;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.Shape.ShapeDeserializer;
import net.thewinnt.planimetry.shapes.lines.AngledInfiniteLine;
import net.thewinnt.planimetry.shapes.lines.InfiniteLine;
import net.thewinnt.planimetry.shapes.lines.LineSegment;
import net.thewinnt.planimetry.shapes.lines.MultiPointLine;
import net.thewinnt.planimetry.shapes.lines.ParallelInfiniteLine;
import net.thewinnt.planimetry.shapes.lines.Ray;
import net.thewinnt.planimetry.shapes.point.MousePoint;
import net.thewinnt.planimetry.shapes.point.Point;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.shapes.point.relative.TangentOffsetPoint;
import net.thewinnt.planimetry.shapes.polygons.Polygon;
import net.thewinnt.planimetry.shapes.polygons.Triangle;
import net.thewinnt.planimetry.util.HashBiMap;

public class ShapeData {
    private static final HashBiMap<String, ShapeDeserializer<?>> SHAPE_TYPES = new HashBiMap<>();

    // POINTS
    public static final ShapeDeserializer<Point> POINT_SIMPLE = register("point_simple", Point::readNbt);
    public static final ShapeDeserializer<MousePoint> MOUSE_POINT = register("mouse_point", MousePoint::readNbt);
    public static final ShapeDeserializer<PointReference> POINT_REFERENCE = register("point_reference", PointReference::readNbt);
    public static final ShapeDeserializer<TangentOffsetPoint> TANGENT_OFFSET_POINT = register("tangent_offset_point", TangentOffsetPoint::readNbt);

    // LINES
    public static final ShapeDeserializer<InfiniteLine> INFINITE_LINE = register("infinite_line", InfiniteLine::readNbt);
    public static final ShapeDeserializer<LineSegment> LINE_SEGMENT = register("line_segment", LineSegment::readNbt);
    public static final ShapeDeserializer<Ray> RAY = register("ray", Ray::readNbt);
    public static final ShapeDeserializer<ParallelInfiniteLine> PARALLEL_INFINITE_LINE = register("parallel_infinite_line", ParallelInfiniteLine::readNbt);
    public static final ShapeDeserializer<AngledInfiniteLine> ANGLED_INFINITE_LINE = register("angled_infinite_line", AngledInfiniteLine::readNbt);

    // POLYGONS
    public static final ShapeDeserializer<MultiPointLine> POLYGONAL_CHAIN = register("polygonal_chain", MultiPointLine::readNbt);
    public static final ShapeDeserializer<Polygon> POLYGON = register("polygon", Polygon::readNbt);
    public static final ShapeDeserializer<Polygon> TRIANGLE = register("triangle", Triangle::readNbt);

    // CIRCLES
    public static final ShapeDeserializer<Circle> CIRCLE = register("circle", Circle::readNbt);

    public static <T extends Shape> ShapeDeserializer<T> register(String name, ShapeDeserializer<T> deserializer) {
        SHAPE_TYPES.put(name, deserializer);
        return deserializer;
    }

    public static ShapeDeserializer<?> getDeserializer(String name) {
        return SHAPE_TYPES.get(name);
    }

    public static String getShapeType(ShapeDeserializer<?> deserializer) {
        return SHAPE_TYPES.getKey(deserializer);
    }

    public static Polygon asSpecificPolygon(Polygon generic) {
        return switch (generic.points.size()) {
            case 3 -> new Triangle(generic);
            default -> generic;
        };
    }
}

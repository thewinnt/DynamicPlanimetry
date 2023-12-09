package net.thewinnt.planimetry.math;

import net.thewinnt.planimetry.DynamicPlanimetry;

public class MathHelper {
    public static final double HALF_PI = Math.PI / 2;

    public static boolean roughlyEquals(double a, double b) {
        return a - b < Math.pow(2, DynamicPlanimetry.SETTINGS.getMathPrecision());
    }

    public static Vec2 continueFromTan(Vec2 from, double tan, double distance) {
        return new Vec2(from.x + Math.cos(Math.atan(tan)) * distance, from.y + Math.sin(Math.atan(tan)) * distance);
    }
    
    public static Vec2 perpendicular(Vec2 point, double slope, double distance) {
        return new Vec2(point.x + Math.cos(Math.atan(slope) + HALF_PI) * distance, point.y + Math.sin(Math.atan(slope) + HALF_PI) * distance);
    }

    public static boolean isPointOnSegment(Vec2 a, Vec2 b, Vec2 point) {
        return roughlyEquals(a.distanceToSqr(point) + b.distanceToSqr(point), a.distanceToSqr(b));
    }

    public static double distanceToLine(Vec2 a, Vec2 b, Vec2 point) {
        return Math.abs((b.x - a.x)*(a.y - point.y) - (a.x - point.x)*(b.y - a.y)) / a.distanceTo(b);
    }

    public static double distanceToSegment(Vec2 a, Vec2 b, Vec2 point) {
        double distance = Math.abs((b.x - a.x)*(a.y - point.y) - (a.x - point.x)*(b.y - a.y)) / a.distanceTo(b);
        double slope = (a.y - b.y) / (a.x - b.x);
        if (!isPointOnSegment(a, b, perpendicular(point, slope, distance)) && !isPointOnSegment(a, b, perpendicular(point, slope, -distance))) {
            return Math.min(a.distanceTo(point), b.distanceTo(point));
        } else {
            return distance;
        }
    }
}

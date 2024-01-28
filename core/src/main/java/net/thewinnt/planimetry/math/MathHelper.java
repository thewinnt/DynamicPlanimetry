package net.thewinnt.planimetry.math;

import net.thewinnt.planimetry.DynamicPlanimetry;

/** A class containing some utility math functions, mostly geometry-related. */
public class MathHelper {
    public static final double HALF_PI = Math.PI / 2;
    public static final double RADIANS_TO_GRADIANS = 200 / Math.PI;

    public static boolean roughlyEquals(double a, double b) {
        return Math.abs(a - b) < Math.pow(2, DynamicPlanimetry.SETTINGS.getMathPrecision());
    }

    public static Vec2 continueFromTan(Vec2 from, double tan, double distance) {
        return new Vec2(from.x + Math.cos(Math.atan(tan)) * distance, from.y + Math.sin(Math.atan(tan)) * distance);
    }

    public static Vec2 continueFromAngle(Vec2 from, double angle, double distance) {
        return new Vec2(from.x + Math.cos(angle) * distance, from.y + Math.sin(angle) * distance);
    }
    
    public static Vec2 perpendicular(Vec2 point, double slope, double distance) {
        return new Vec2(point.x + Math.cos(Math.atan(slope) + HALF_PI) * distance, point.y + Math.sin(Math.atan(slope) + HALF_PI) * distance);
    }

    public static boolean isPointOnSegment(Vec2 a, Vec2 b, Vec2 point) {
        return roughlyEquals(a.distanceTo(point) + b.distanceTo(point), a.distanceTo(b));
    }

    public static double distanceToLine(Vec2 a, Vec2 b, Vec2 point) {
        return Math.abs((b.x - a.x)*(a.y - point.y) - (a.x - point.x)*(b.y - a.y)) / a.distanceTo(b);
    }

    public static double distanceToSegment(Vec2 a, Vec2 b, Vec2 point) {
        double distance = distanceToLine(a, b, point);
        double slope = (a.y - b.y) / (a.x - b.x);
        if (!isPointOnSegment(a, b, perpendicular(point, slope, distance)) && !isPointOnSegment(a, b, perpendicular(point, slope, -distance))) {
            return Math.min(a.distanceTo(point), b.distanceTo(point));
        } else {
            return distance;
        }
    }

    public static double getSlope(Vec2 a, Vec2 b) {
        return (a.y - b.y) / (a.x - b.x);
    }

    public static double angle(Vec2 end1, Vec2 center, Vec2 end2) {
        Vec2 angle1 = end1.subtract(center);
        Vec2 angle2 = end2.subtract(center);
        return Math.acos(angle1.dot(angle2) / (angle1.length() * angle2.length()));
    }
}

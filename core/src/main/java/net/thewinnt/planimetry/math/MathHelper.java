package net.thewinnt.planimetry.math;

import net.thewinnt.planimetry.Settings;

/** A class containing some utility math functions, mostly geometry-related. */
public class MathHelper {
    public static final double QUARTER_PI = Math.PI / 4;
    public static final double HALF_PI = Math.PI / 2;
    public static final double DOUBLE_PI = Math.PI * 2;
    public static final double RADIANS_TO_GRADIANS = 200 / Math.PI;
    public static final double GRADIANS_TO_RADIANS = Math.PI / 200;

    public static boolean roughlyEquals(double a, double b) {
        a = Double.longBitsToDouble(Double.doubleToRawLongBits(a) & 0xffffffffffff0000l);
        b = Double.longBitsToDouble(Double.doubleToRawLongBits(b) & 0xffffffffffff0000l);
        return (Double.doubleToRawLongBits(Math.abs(a - b)) & 0xffffffffffff0000l) == 0;
        // return Math.abs(a - b) < Math.pow(2, DynamicPlanimetry.SETTINGS.getMathPrecision());
    }

    public static boolean veryRoughlyEquals(double a, double b) {
        double factor = Math.pow(10, -Settings.get().getDisplayPresicion());
        return Math.abs(a - b) < factor;
        // return Math.abs(a - b) < Math.pow(2, DynamicPlanimetry.SETTINGS.getMathPrecision());
    }

    public static boolean roughlyEquals(Vec2 a, Vec2 b) {
        return roughlyEquals(a.x, b.x) && roughlyEquals(a.y, b.y);
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

    public static double distanceToLineSigned(Vec2 a, Vec2 b, Vec2 point) {
        return ((b.x - a.x)*(a.y - point.y) - (a.x - point.x)*(b.y - a.y)) / a.distanceTo(b);
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
        if (center.equals(Vec2.ZERO)) {
            return Math.acos(end1.dot(end2) / (end1.length() * end2.length())); // optimize for zero
        }
        Vec2 angle1 = end1.subtract(center);
        Vec2 angle2 = end2.subtract(center);
        return Math.acos(angle1.dot(angle2) / (angle1.length() * angle2.length()));
    }

    public static boolean areParallel(Vec2 aa, Vec2 ab, Vec2 ba, Vec2 bb) {
        return roughlyEquals(getSlope(ba, bb), getSlope(aa, ab));
    }

    public static double polarAngle(Vec2 center, Vec2 point) {
        Vec2 zero = point.subtract(center);
        return Math.atan2(zero.y, zero.x);
    }

    public static double normalizeAngle(double angle) {
        if (angle > Math.PI) {
            angle %= DOUBLE_PI;
            if (angle > Math.PI) return angle - DOUBLE_PI;
            return angle;
        };
        if (angle < -Math.PI) {
            angle %= DOUBLE_PI;
            if (angle < -Math.PI) return angle + DOUBLE_PI;
        }
        return angle;
    }

    public static boolean isAngleBetween(double a, double b, double test) {
        double max = Math.max(a, b); // > 0
        double min = Math.min(a, b); // < 0
        if (a > 0 && b > 0) return min < test && test < max;
        if (a < 0 && b < 0) return min < test && test < max;
        if (max - min < Math.PI) return min < test && test < max; // 0 arc
        return test > max || test < min; // pi arc
    }
}

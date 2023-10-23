package net.thewinnt.planimetry.math;

public class MathHelper {
    public static final double HALF_PI = Math.PI / 2;

    public static Vec2 continueFromTan(Vec2 from, double tan, double distance) {
        return new Vec2(from.x + Math.cos(Math.atan(tan)) * distance, from.y + Math.sin(Math.atan(tan)) * distance);
    }
    
    public static Vec2 perpendicular(Vec2 point, double slope, double distance) {
        return new Vec2(point.x + Math.cos(Math.atan(slope) + HALF_PI) * distance, point.y + Math.sin(Math.atan(slope) + HALF_PI) * distance);
    }
}

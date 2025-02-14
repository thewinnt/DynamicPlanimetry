package net.thewinnt.planimetry.math;

import java.util.Optional;

import net.thewinnt.planimetry.Settings;

public interface SegmentLike {
    default boolean contains(Vec2 point) {
        Vec2 a = point1();
        Vec2 b = point2();
        return a.distanceTo(point) + b.distanceTo(point) - a.distanceTo(b) < Math.pow(2, Settings.get().getMathPrecision());
    }
    default double distanceTo(Vec2 point) {
        Vec2 a = this.point1();
        Vec2 b = this.point2();
        double distance = Math.abs((b.x - a.x)*(a.y - point.y) - (a.x - point.x)*(b.y - a.y)) / a.distanceTo(b);
        double slope = this.getSlope();
        if (!this.contains(MathHelper.perpendicular(point, slope, distance)) && !this.contains(MathHelper.perpendicular(point, slope, -distance))) {
            return Math.min(a.distanceTo(point), b.distanceTo(point));
        } else {
            return distance;
        }
    }
    
    default double getSlope() {
        Vec2 a = point1();
        Vec2 b = point2();
        return (b.y - a.y) / (b.x - a.x);
    }

    Vec2 point1();
    Vec2 point2();

    default Optional<Vec2> intersection(SegmentLike other) {
        Vec2 a = this.point1();
        Vec2 b = this.point2();
        Vec2 c = other.point1();
        Vec2 d = other.point2();
        double denom = (a.x - b.x) * (c.y - d.y) - (a.y - b.y) * (c.x - d.x);
        if (MathHelper.roughlyEquals(denom, 0)) return Optional.empty();
        double tx = (a.x * b.y - a.y * b.x) * (c.x - d.x) - (a.x - b.x) * (c.x * d.y - c.y * d.x) / denom;
        double ty = (a.x * b.y - a.y * b.x) * (c.y - d.y) - (a.y - b.y) * (c.x * d.y - c.y * d.x) / denom;
        return Optional.of(new Vec2(tx, ty)).filter(other::contains);
    }
}

package net.thewinnt.planimetry.math;

public record Segment(Vec2 a, Vec2 b) implements SegmentLike {
    @Override
    public Vec2 point1() {
        return a;
    }

    @Override
    public Vec2 point2() {
        return b;
    }
}

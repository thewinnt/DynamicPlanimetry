package net.thewinnt.planimetry.math;

import com.badlogic.gdx.math.Rectangle;

public class AABB {
    public final Vec2 min;
    public final Vec2 max;
    private Segment[] cachedLineSegments;

    public AABB(Vec2 a, Vec2 b) {
        this.min = a.withX(Math.min(a.x, b.x)).withY(Math.min(a.y, b.y));
        this.max = b.withX(Math.max(a.x, b.x)).withY(Math.max(a.y, b.y));
    }

    public AABB(Vec2 point) {
        this.min = point;
        this.max = point;
    }

    public boolean contains(Vec2 point) {
        return min.x <= point.x && point.x <= max.x && min.y <= point.y && point.y <= max.y;
    }

    public double getMinX() {
        return min.x;
    }

    public double getMaxX() {
        return max.x;
    }

    public double getMinY() {
        return min.y;
    }

    public double getMaxY() {
        return max.y;
    }

    public double getWidth() {
        return max.x - min.x;
    }

    public double getHeight() {
        return max.y - min.y;
    }

    public Rectangle toGdxRect() {
        return new Rectangle((float)min.x, (float)min.y, (float)getWidth(), (float)getHeight());
    }

    public AABB expand(double x, double y) {
        return new AABB(min.add(-x / 2, -y / 2), max.add(x / 2, y / 2));
    }

    public SegmentLike[] asLineSegments() {
        if (cachedLineSegments == null) {
            cachedLineSegments = new Segment[]{
                new Segment(min, min.withY(max.y)),
                new Segment(min.withY(max.y), max),
                new Segment(max, max.withY(min.y)),
                new Segment(min, min.withX(max.x))
            };
        }
        return cachedLineSegments;
    }
}

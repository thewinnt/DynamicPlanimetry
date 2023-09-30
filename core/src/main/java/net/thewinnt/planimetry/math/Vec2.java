package net.thewinnt.planimetry.math;

import com.badlogic.gdx.math.Vector2;

/** An immutable 2D vector */
public class Vec2 {
    public final double x;
    public final double y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2(Vec2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public double lengthSqr() {
        return this.x * this.x + this.y * this.y;
    }

    public Vec2 add(Vec2 other) {
        return new Vec2(x + other.x, y + other.y);
    }

    public Vec2 add(double x, double y) {
        return new Vec2(x + this.x, y + this.y);
    }

    public Vec2 mul(Vec2 other) {
        return new Vec2(x * other.x, y * other.y);
    }

    public Vec2 mul(double x, double y) {
        return new Vec2(x * this.x, y * this.y);
    }

    public Vec2 mul(double factor) {
        return new Vec2(x * factor, y * factor);
    }

    public Vec2 negate() {
        return new Vec2(-x, -y);
    }

    public double distanceToSqr(Vec2 other) {
        return (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y);
    }

    public double distanceToSqr(double x, double y) {
        return (x - this.x) * (x - this.x) + (y - this.y) * (y - this.y);
    }

    public double distanceTo(Vec2 other) {
        return Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
    }

    public double distanceTo(double x, double y) {
        return Math.sqrt((x - this.x) * (x - this.x) + (y - this.y) * (y - this.y));
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Vec2 other && other.x == this.x && other.y == this.y;
    }

    public Vector2 toVector2f() {
        return new Vector2((float)x, (float)y);
    }
}

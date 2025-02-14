package net.thewinnt.planimetry.shapes.polygons;

import java.util.List;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.Point;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Tetragon extends Polygon {
    private final PointProvider a;
    private final PointProvider b;
    private final PointProvider c;
    private final PointProvider d;
    private final TetragonType type;

    public Tetragon(Drawing drawing, PointProvider a, PointProvider b, PointProvider c, PointProvider d) {
        super(drawing, List.of(a, b, c, d));
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.type = TetragonType.FREEFORM;
    }

    public Tetragon(Polygon tetragon) {
        super(tetragon.getDrawing(), tetragon.points);
        this.a = tetragon.points.get(0);
        this.b = tetragon.points.get(1);
        this.c = tetragon.points.get(2);
        this.d = tetragon.points.get(3);
        this.type = TetragonType.FREEFORM;
    }

    private Tetragon(Drawing drawing, PointProvider a, PointProvider b, PointProvider c, PointProvider d, TetragonType tetragonType) {
        super(drawing, List.of(a, b, c, d));
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        if (tetragonType.validate(a.getPosition(), b.getPosition(), c.getPosition(), d.getPosition())) {
            this.type = tetragonType;
        } else {
            this.type = TetragonType.FREEFORM;
        }
    }

    public Tetragon(Drawing drawing, PointProvider a, PointProvider b, PointProvider c) {
        super(drawing, List.of(a, b, c, new Point(drawing, c.getPosition().subtract(b.getPosition().subtract(a.getPosition())))));
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = this.points.get(3);
        this.type = TetragonType.PARALLELOGRAM; // they don't do anything for now
    }

    @Override
    public void addPoint(PointProvider point) {
        throw new UnsupportedOperationException("Tetragons can only have 4 points");
    }

    @Override
    public double getArea() {
        switch (type) {
            case FREEFORM:
            case PARALLELOGRAM:
                Vec2 diagonal1 = this.c.getPosition().subtract(this.a.getPosition());
                Vec2 diagonal2 = this.d.getPosition().subtract(this.b.getPosition());
                return diagonal1.length() * diagonal2.length() * Math.sin(MathHelper.angle(diagonal1, Vec2.ZERO, diagonal2)) / 2;
            case RHOMBUS:
                Vec2 diag1 = this.c.getPosition().subtract(this.a.getPosition());
                Vec2 diag2 = this.d.getPosition().subtract(this.b.getPosition());
                return diag1.length() * diag2.length() / 2;
            case RECTANGLE:
                return this.a.getPosition().distanceTo(this.b.getPosition()) * this.a.getPosition().distanceTo(this.c.getPosition());
            case SQUARE:
                return this.a.getPosition().distanceToSqr(this.b.getPosition());
            case TRAPEZOID:
                if (MathHelper.areParallel(this.a.getPosition(), this.b.getPosition(), this.c.getPosition(), this.d.getPosition())) {
                    return (this.a.getPosition().distanceTo(this.b.getPosition()) + this.c.getPosition().distanceTo(this.d.getPosition())) / 2 * MathHelper.distanceToLine(this.a.getPosition(), this.b.getPosition(), this.d.getPosition());
                } else {
                    return (this.a.getPosition().distanceTo(this.d.getPosition()) + this.c.getPosition().distanceTo(this.b.getPosition())) / 2 * MathHelper.distanceToLine(this.a.getPosition(), this.d.getPosition(), this.b.getPosition());
                }
            default:
                return Double.NaN;
        }
    }

    @Override
    public String getTypeName() {
        return "shape.polygon.4";
    }

    @Override
    public ShapeDeserializer<?> type() {
        return ShapeData.TRIANGLE;
    }

    public boolean isEquilateral() {
        return MathHelper.roughlyEquals(a.distanceToMouse(b.getPosition(), null), a.distanceToMouse(c.getPosition(), null)) &&
               MathHelper.roughlyEquals(a.distanceToMouse(b.getPosition(), null), b.distanceToMouse(c.getPosition(), null));
    }

    public static Tetragon readNbt(CompoundTag nbt, LoadingContext context) {
        var points = pointsFromNbt(nbt, context);
        return new Tetragon(context.getDrawing(), points.get(0), points.get(1), points.get(2), points.get(3));
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        switch (this.type) {
            case PARALLELOGRAM:
                ((Point)this.d).setPosition(c.getPosition().subtract(b.getPosition().subtract(a.getPosition())));
                break;
            default:
                break;
        }
        super.render(drawer, selection, font, board);
    }

    public enum TetragonType {
        FREEFORM {
            @Override
            public boolean validate(Vec2 a, Vec2 b, Vec2 c, Vec2 d) {
                // whatever really
                return true;
            }
        },
        TRAPEZOID {
            @Override
            public boolean validate(Vec2 a, Vec2 b, Vec2 c, Vec2 d) {
                // only 2 sides are roughly parallel
                return MathHelper.areParallel(a, b, c, d) != MathHelper.areParallel(a, d, b, c);
            }
        },
        PARALLELOGRAM {
            @Override
            public boolean validate(Vec2 a, Vec2 b, Vec2 c, Vec2 d) {
                // both opposite side pairs are parallel
                return MathHelper.areParallel(a, b, c, d) && MathHelper.areParallel(a, d, b, c);
            }
        },
        RECTANGLE {
            @Override
            public boolean validate(Vec2 a, Vec2 b, Vec2 c, Vec2 d) {
                // both sides parallel and there's a right angle
                return MathHelper.areParallel(a, b, c, d) && MathHelper.areParallel(a, d, b, c) && MathHelper.roughlyEquals(MathHelper.angle(b, a, c), MathHelper.HALF_PI);
            }
        },
        SQUARE {
            @Override
            public boolean validate(Vec2 a, Vec2 b, Vec2 c, Vec2 d) {
                // all sides are equal and there's a right angle
                return a.distanceToSqr(b) == b.distanceToSqr(c) && b.distanceToSqr(c) == c.distanceToSqr(d) && MathHelper.roughlyEquals(MathHelper.angle(b, a, c), MathHelper.HALF_PI);
            }
        },
        RHOMBUS {
            @Override
            public boolean validate(Vec2 a, Vec2 b, Vec2 c, Vec2 d) {
                // all sides are equal
                return a.distanceToSqr(b) == b.distanceToSqr(c) && b.distanceToSqr(c) == c.distanceToSqr(d);
            }
        };

        public abstract boolean validate(Vec2 a, Vec2 b, Vec2 c, Vec2 d);
    }
}

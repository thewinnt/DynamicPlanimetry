package net.thewinnt.planimetry.shapes.display.angle;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.math.AABB;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.SegmentLike;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.Size;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.properties.types.BooleanProperty;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class AngleMarker extends Shape {
    protected final DisplayProperty value = new DisplayProperty(Component.translatable("property.dynamic_planimetry.angle_marker.generic.value"), () -> Component.angle(getAngle()));
    protected final BooleanProperty displayValue = new BooleanProperty(Component.translatable("property.dynamic_planimetry.angle_marker.generic.show_value"), true);

    public AngleMarker(Drawing drawing) {
        super(drawing);
    }

    public abstract Vec2 getStartPoint();
    public abstract double getAngleA();
    public abstract double getAngleB();
    public abstract double getAngle();

    @Override public boolean canMove() { return false; }
    @Override public boolean contains(Vec2 point) { return false; }
    @Override public boolean contains(double x, double y) { return false; }
    @Override public void move(Vec2 delta) {}
    @Override public void move(double dx, double dy) {}
    @Override public boolean intersects(AABB aabb) { return false; }
    @Override public Collection<Vec2> intersections(Shape other) { return List.of(); }
    @Override public Collection<Vec2> intersections(SegmentLike other) { return List.of(); }
    @Override public Collection<SegmentLike> asSegments() { return List.of(); }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        Vec2 start = getStartPoint();
        point = point.lerp(start, (30 / board.getScale()) / point.distanceTo(start));
        double distance = point.distanceTo(start);
        if (MathHelper.isAngleBetween(getAngleA(), getAngleB(), MathHelper.angleTo(start, point))) return distance;
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return distanceToMouse(new Vec2(x, y), board);
    }

    @Override
    public void rebuildProperties() {
        this.properties.clear();
        this.properties.add(value);
        this.properties.add(displayValue);
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        // init variables
        float angleA = (float)getAngleA();
        float angleB = (float)getAngleB();
        float angle = angleA - angleB;
        double angFull = getAngle();
        Vec2 center = getStartPoint();
        float x = board.bx(center.x);
        float y = board.by(center.y);
        Color color = this.getColor(selection);
        drawer.setColor(color);

        // detect center angle
        double centerAngle;
        if (angle < 0) {
            centerAngle = angleA - angle / 2;
        } else if (angle < Math.PI && angleB >= angleA) {
            centerAngle = angleA + (Math.PI - angle) / 2;
        } else if (angle < Math.PI && angleB < angleA) {
            centerAngle = angleB + angle / 2;
        } else {
            centerAngle = angleA + (MathHelper.DOUBLE_PI - angle) / 2;
        }
        if (centerAngle > Math.PI) centerAngle = centerAngle - MathHelper.DOUBLE_PI;

        // draw arc
        if (MathHelper.veryRoughlyEquals(angFull, MathHelper.HALF_PI)) {
            Vec2 targPoint = board.boardToGlobal(MathHelper.continueFromAngle(center, centerAngle, 30 / board.getScale() * Math.sqrt(2)));
            Vec2 p1 = board.boardToGlobal(MathHelper.continueFromAngle(center, angleA, 30 / board.getScale()));
            Vec2 p2 = board.boardToGlobal(MathHelper.continueFromAngle(center, angleB, 30 / board.getScale()));
            drawer.path(Array.with(p1.toVector2f(), targPoint.toVector2f(), p2.toVector2f()), getThickness(board.getScale()), true);
        } else {
            if (angle < 0) {
                drawer.arc(x, y, 30, angleA, -angle, getThickness(board.getScale()));
            } else if (angle < Math.PI && angleB >= angleA) {
                drawer.arc(x, y, 30, angleA, (float)(Math.PI - angle), getThickness(board.getScale()));
            } else if (angle < Math.PI && angleB < angleA) {
                drawer.arc(x, y, 30, angleB, angle, getThickness(board.getScale()));
            } else {
                drawer.arc(x, y, 30, angleA, (float)(MathHelper.DOUBLE_PI - angle), getThickness(board.getScale()));
            }
        }

        // draw text
        if (displayValue.getValue()) {
            Component marker = Component.angle(angFull);
            Vec2 pos = MathHelper.continueFromAngle(new Vec2(x, y), centerAngle, 30);
            Vec2 size = marker.getSize(font, Size.SMALL);
            double yMul, xMul;
            // offset the text for beauty
            if (centerAngle > MathHelper.QUARTER_PI && centerAngle <= Math.PI * 3 / 4) {
                xMul = -Math.sin(centerAngle - MathHelper.QUARTER_PI);
                yMul = 1;
            } else if (centerAngle > Math.PI * 3 / 4 || centerAngle < -Math.PI * 3 / 4) {
                xMul = -1;
                yMul = (Math.sin(centerAngle + MathHelper.QUARTER_PI) + 1);
            } else if (centerAngle < MathHelper.QUARTER_PI && centerAngle > -MathHelper.QUARTER_PI) {
                xMul = 0;
                yMul = (Math.sin(centerAngle) * Math.sqrt(2) + 1) / 2;
            } else {
                xMul = Math.sin(centerAngle + MathHelper.QUARTER_PI);
                yMul = 0;
            }
            Vec2 pos2 = pos.add(size.x * xMul, size.y * yMul);
            marker.draw(drawer.getBatch(), font, Size.SMALL, Theme.current().textAngleMarker(), (float)pos2.x, (float)pos2.y);

            // debug data
            if (DynamicPlanimetry.isDebug()) {
                font.getFont(30, Color.SLATE).draw(drawer.getBatch(), "1/4 pi: " + MathHelper.QUARTER_PI, x, y + 25);
                font.getFont(30, Color.SLATE).draw(drawer.getBatch(), "3/4 pi: " + MathHelper.QUARTER_PI * 3, x, y + 50);
                font.getFont(30, Color.SLATE).draw(drawer.getBatch(), "5/4 pi: " + MathHelper.QUARTER_PI * 5, x, y + 75);
                font.getFont(30, Color.SLATE).draw(drawer.getBatch(), "7/4 pi: " + MathHelper.QUARTER_PI * 7, x, y + 100);
                font.getFont(30, Color.SLATE).draw(drawer.getBatch(), "angle draw: " + centerAngle, x, y + 125);
                Vec2 pos3 = MathHelper.continueFromAngle(pos, centerAngle, 48);
                drawer.line(x, y, (float)pos3.x, (float)pos3.y, Color.SLATE, 4);
                drawer.line((float)pos.x, (float)pos.y, (float)(pos.x + size.x * xMul), (float)pos.y, Color.RED, 2);
                drawer.line((float)pos.x, (float)pos.y, (float)pos.x, (float)(pos.y + size.y * yMul), Color.GREEN, 2);
            }
        }
    }
}

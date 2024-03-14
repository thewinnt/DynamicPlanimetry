package net.thewinnt.planimetry.shapes.display.angle;

import com.badlogic.gdx.graphics.Color;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.Theme;
import net.thewinnt.planimetry.ui.StyleSet.Size;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public abstract class AngleMarker extends Shape {
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

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        return Double.POSITIVE_INFINITY; // TODO distance to mouse
        // double dirPoint = MathHelper.polarAngle(getStartPoint(), point);
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return Double.POSITIVE_INFINITY; // TODO distance to mouse
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
        drawer.setColor(Theme.current().angleMarker());

        // draw arc
        double centerAngle;
        if (angle < 0) {
            drawer.arc(x, y, 24, angleA, -angle, getThickness(board.getScale()));
            centerAngle = angleA - angle / 2;
        } else if (angle < Math.PI && angleB >= angleA) {
            drawer.arc(x, y, 24, angleA, (float)(Math.PI - angle), getThickness(board.getScale()));
            centerAngle = angleA + (Math.PI - angle) / 2;
        } else if (angle < Math.PI && angleB < angleA) {
            drawer.arc(x, y, 24, angleB, angle, getThickness(board.getScale()));
            centerAngle = angleB + angle / 2;
        } else {
            drawer.arc(x, y, 24, angleA, (float)(MathHelper.DOUBLE_PI - angle), getThickness(board.getScale()));
            centerAngle = angleA + (MathHelper.DOUBLE_PI - angle) / 2;
        }
        
        // draw text
        Component marker = Component.angle(angFull);
        Vec2 pos = MathHelper.continueFromAngle(new Vec2(x, y), centerAngle, 12);
        Vec2 size = marker.getSize(font, Size.SMALL);
        double yMul, xMul;
        if (centerAngle > MathHelper.QUARTER_PI && centerAngle <= Math.PI * 3 / 4) {
            xMul = Math.sin(centerAngle - MathHelper.QUARTER_PI);
            yMul = 1;
        } else if (centerAngle > Math.PI * 3 / 4 || centerAngle < -Math.PI * 3 / 4) {
            xMul = 1;
            yMul = (Math.sin(centerAngle) * Math.sqrt(2) + 1) / 2;
        } else if (centerAngle < MathHelper.QUARTER_PI && centerAngle > -MathHelper.QUARTER_PI) {
            xMul = 0;
            yMul = Math.sin(centerAngle) * Math.sqrt(2);
        } else {
            xMul = Math.sin(centerAngle + MathHelper.QUARTER_PI);
            yMul = 0;
        }
        Vec2 pos2 = pos.add(size.x * xMul, size.y * yMul);
        marker.draw(drawer.getBatch(), font, Size.SMALL, Theme.current().textAngleMarker(), (float)pos2.x, (float)pos2.y);

        // debug data
        if (DynamicPlanimetry.isDebug()) {
            font.getFont(30, Color.SLATE).draw(drawer.getBatch(), "angle a: " + getAngleA(), x, y + 25);
            font.getFont(30, Color.SLATE).draw(drawer.getBatch(), "angle b: " + getAngleB(), x, y + 50);
            font.getFont(30, Color.SLATE).draw(drawer.getBatch(), "angle full: " + getAngle(), x, y + 75);
            font.getFont(30, Color.SLATE).draw(drawer.getBatch(), "angle res: " + angle, x, y + 100);
            Vec2 pos3 = MathHelper.continueFromAngle(pos, centerAngle, 48);
            drawer.line(x, y, (float)pos3.x, (float)pos3.y, Color.SLATE, 4);
            drawer.line((float)pos.x, (float)pos.y, (float)(pos.x + size.x * xMul), (float)pos.y, Color.RED, 2);
            drawer.line((float)pos.x, (float)pos.y, (float)pos.x, (float)(pos.y + size.y * yMul), Color.GREEN, 2);
        }
    }
}

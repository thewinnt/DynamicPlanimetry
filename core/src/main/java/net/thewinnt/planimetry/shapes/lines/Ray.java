package net.thewinnt.planimetry.shapes.lines;

import java.util.Random;
import java.util.function.DoubleFunction;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Ray extends Line {
    private boolean startFromA = new Random().nextBoolean();
    private double lastDistanceA = -1;
    private double lastDistanceB = -1;
    private Vec2 perp1 = Vec2.ZERO;
    private Vec2 perp2 = Vec2.ZERO;
    private double rawDistance = -1;
    private double dot = -1;

    public Ray(PointReference a, PointReference b) {
        super(a, b);
    }

    public Ray(PointProvider a, PointProvider b) {
        super(a, b);
    }

    @Override
    public boolean contains(double x, double y) {
        double dot;
        if (startFromA) {
            dot = new Vec2(x, y).subtract(a.getPosition()).dot(b.getPosition());
        } else {
            dot = new Vec2(x, y).subtract(b.getPosition()).dot(a.getPosition());
        }
        // TODO just test if B is between A and C
        return dot > 0 && a.getPosition().distanceTo(x, y) + b.getPosition().distanceTo(x, y) - a.getPosition().distanceTo(b.getPosition()) < Math.pow(2, -23);
    }

    @Override
    public boolean contains(Vec2 point) {
        double dot;
        if (startFromA) {
            dot = point.subtract(a.getPosition()).dot(b.getPosition());
        } else {
            dot = point.subtract(b.getPosition()).dot(a.getPosition());
        }
        return dot > 0 && a.getPosition().distanceTo(point) + b.getPosition().distanceTo(point) - a.getPosition().distanceTo(b.getPosition()) < Math.pow(2, -23);
    }

    private double containTest(Vec2 point) {
        // return a.getPosition().distanceTo(point) + b.getPosition().distanceTo(point) - a.getPosition().distanceTo(b.getPosition());
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        return (a.x - point.x)*(point.y - b.y) - (point.x - b.x)*(a.y - point.y);
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        double distance = Math.abs((b.x - a.x)*(a.y - point.y) - (a.x - point.x)*(b.y - a.y)) / a.distanceTo(b);
        rawDistance = distance;
        double slope = Math.tan(Math.atan(this.getSlope()) + Math.PI / 2);
        lastDistanceA = this.containTest(point.continueFromTan(slope, distance));
        lastDistanceB = this.containTest(point.continueFromTan(slope, -distance));
        perp1 = point.continueFromTan(slope, distance);
        perp2 = point.continueFromTan(slope, -distance);
        dot = point.subtract(a).dot(b);
        if (!this.contains(point.continueFromTan(slope, distance)) && !this.contains(point.continueFromTan(slope, -distance))) {
            return Math.min(a.distanceTo(point), b.distanceTo(point));
        } else {
            return distance;
        }
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        return distanceToMouse(new Vec2(x, y), board);
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        DoubleFunction<Double> formula = compileFormula();
        Color lineColor = switch (selection) {
            default -> DynamicPlanimetry.COLOR_SHAPE;
            case HOVERED -> DynamicPlanimetry.COLOR_SHAPE_HOVER;
            case SELECTED -> DynamicPlanimetry.COLOR_SHAPE_SELECTED;
        };
        Vec2 a, b;
        if (startFromA) {
            a = this.a.getPosition();
            b = this.b.getPosition();
        } else {
            a = this.b.getPosition();
            b = this.a.getPosition();
        }
        if (a.x == b.x) {
            float targHeight = a.y < b.y ? board.getY() : board.getY() + board.getHeight();
            drawer.line(board.bx(a.x), board.by(a.y), board.bx(b.x), targHeight, lineColor, (float)Math.min(Math.max(1, board.getScale() / 2), 4));
        } else if (a.x < b.x) {
            drawer.line(board.bx(a.x), board.by(a.y), board.getX() + board.getWidth(), board.by(formula.apply(board.maxX())), lineColor, (float)Math.min(Math.max(1, board.getScale() / 2), 4));
        } else {
            drawer.line(board.getX(), board.by(formula.apply(board.minX())), board.bx(a.x), board.by(a.y), lineColor, (float)Math.min(Math.max(1, board.getScale() / 2), 4));
        }
        if (selection == SelectionStatus.SELECTED) {
            if (!board.getShapes().contains(this.a)) {
                this.a.render(drawer, SelectionStatus.NONE, font, board);
            }
            if (!board.getShapes().contains(this.b)) {
                this.b.render(drawer, SelectionStatus.NONE, font, board);
            }
        }
        if (DynamicPlanimetry.DEBUG_MODE) {
            int mx = Gdx.input.getX();
            int my = Gdx.input.getY();
            Vec2 mouse = new Vec2(board.xb(mx), board.yb(my));
            font.getFont(40, lineColor).draw(drawer.getBatch(), String.valueOf(distanceToMouse(mouse, board)), (float)board.bx(a.x) + 5, (float)board.by(a.y) - 5);
            font.getFont(40, lineColor).draw(drawer.getBatch(), String.valueOf(rawDistance), (float)board.bx(a.x) + 5, (float)board.by(a.y) - 30);
            font.getFont(40, lineColor).draw(drawer.getBatch(), String.valueOf(startFromA), (float)board.bx(a.x) + 5, (float)board.by(a.y) - 55);
            font.getFont(40, Color.SKY).draw(drawer.getBatch(), String.valueOf(lastDistanceA), (float)board.bx(a.x) + 5, (float)board.by(a.y) - 80);
            font.getFont(40, Color.MAROON).draw(drawer.getBatch(), String.valueOf(lastDistanceB), (float)board.bx(a.x) + 5, (float)board.by(a.y) - 105);
            font.getFont(40, lineColor).draw(drawer.getBatch(), String.valueOf(dot), (float)board.bx(a.x) + 5, (float)board.by(a.y) - 130);
            drawer.line(mx, Gdx.graphics.getHeight() - my, board.bx(perp1.x), board.by(perp1.y), Color.SKY, 4);
            drawer.filledCircle(board.bx(perp1.x), board.by(perp1.y), 8, Color.SKY);
            drawer.line(mx, Gdx.graphics.getHeight() - my, board.bx(perp2.x), board.by(perp2.y), Color.MAROON, 4);
            drawer.filledCircle(board.bx(perp2.x), board.by(perp2.y), 8, Color.MAROON);
        }
    }
}

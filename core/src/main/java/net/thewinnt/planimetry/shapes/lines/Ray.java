package net.thewinnt.planimetry.shapes.lines;

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
    private boolean startFromA = true;

    public Ray(PointReference a, PointReference b) {
        super(a, b);
    }

    public Ray(PointProvider a, PointProvider b) {
        super(a, b);
    }

    @Override
    public boolean contains(double x, double y) {
        // TODO sides
        return a.getPosition().distanceTo(x, y) + b.getPosition().distanceTo(x, y) == a.getPosition().distanceTo(b.getPosition());
    }

    @Override
    public boolean contains(Vec2 point) {
        // TODO sides
        return a.getPosition().distanceTo(point) + b.getPosition().distanceTo(point) == a.getPosition().distanceTo(b.getPosition());
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        double slope = getSlope();
        if (Double.isNaN(slope)) {
            return Math.abs(point.x - a.getPosition().x);
        } else if (slope > 1) {
            double offset = compileFormula().apply(0);
            DoubleFunction<Double> xFromY = y -> (y - offset) / slope;
            return Math.abs(xFromY.apply(point.y) - point.x);
        }
        return Math.abs(compileFormula().apply(point.x) - point.y);
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        double slope = getSlope();
        if (Double.isNaN(slope)) {
            return Math.abs(x - a.getPosition().x);
        } else if (slope > 1) {
            double offset = compileFormula().apply(0);
            DoubleFunction<Double> xFromY = yc -> (yc - offset) / slope;
            return Math.abs(xFromY.apply(y) - x);
        }
        return Math.abs(compileFormula().apply(x) - y);
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        DoubleFunction<Double> formula = compileFormula();
        Color lineColor = switch (selection) {
            default -> DynamicPlanimetry.COLOR_SHAPE;
            case HOVERED -> DynamicPlanimetry.COLOR_SHAPE_HOVER;
            case SELECTED -> DynamicPlanimetry.COLOR_SHAPE_SELECTED;
        };
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        if (a.x == b.x) {
            drawer.line(board.bx(a.x), board.getY(), board.bx(b.x), board.getY() + board.getHeight(), lineColor, (float)Math.min(Math.max(1, board.getScale() / 2), 4));
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
        int mx = Gdx.input.getX();
        int my = Gdx.input.getY();
        Vec2 mouse = new Vec2(board.xb(mx), board.yb(my));
        font.getFont(40, lineColor).draw(drawer.getBatch(), String.valueOf(distanceToMouse(mouse, board)), (float)board.bx(a.x) + 5, (float)board.by(a.y) - 5);
    }
}

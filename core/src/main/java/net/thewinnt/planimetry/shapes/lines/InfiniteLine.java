package net.thewinnt.planimetry.shapes.lines;

import java.util.function.DoubleFunction;

import com.badlogic.gdx.graphics.Color;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

/** An infinite straight line, built using two points. */
public class InfiniteLine extends Line {
    public InfiniteLine(PointProvider a, PointProvider b) {
        super(a, b);
    }

    public DoubleFunction<Double> compileFormula() {
        // y = kx+b
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        if (a.x == b.x) return x -> Double.NaN;
        double k = (a.y - b.y) / (a.x - b.x);
        return x -> k * (x - a.x) + a.y;
    }

    public double getSlope() {
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        if (a.x == b.x) return Double.NaN;
        double k = (a.y - b.y) / (a.x - b.x);
        return k;
    }

    @Override
    public boolean contains(Vec2 point) {
        if (Double.isNaN(getSlope())) return point.x == a.getPosition().x;
        return compileFormula().apply(point.x) == point.y;
    }

    @Override
    public boolean contains(double x, double y) {
        if (Double.isNaN(getSlope())) return x == a.getPosition().x;
        return compileFormula().apply(x) == y;
    }

    @Override
    public boolean containsRough(Vec2 point) {
        double slope = getSlope();
        if (Double.isNaN(slope)) {
            return Math.abs(point.x - a.getPosition().x) <= 2;
        } else if (slope > 1) {
            double offset = compileFormula().apply(0);
            DoubleFunction<Double> xFromY = y -> (y - offset) / slope;
            return Math.abs(xFromY.apply(point.y) - point.x) <= 2;
        }
        return Math.abs(compileFormula().apply(point.x) - point.y) <= 2;
    }

    @Override
    public boolean containsRough(double x, double y) {
        double slope = getSlope();
        if (Double.isNaN(slope)) {
            return Math.abs(x - a.getPosition().x) <= 2;
        } else if (slope > 1) {
            double offset = compileFormula().apply(0);
            DoubleFunction<Double> xFromY = yc -> (yc - offset) / slope;
            return Math.abs(xFromY.apply(y) - x) <= 2;
        }
        return Math.abs(compileFormula().apply(x) - y) <= 2;
    }

    @Override
    public void render(ShapeDrawer drawer, boolean selected, FontProvider font, DrawingBoard board) {
        DoubleFunction<Double> formula = compileFormula();
        Color lineColor = selected ? DynamicPlanimetry.COLOR_SELECTION : Color.BLACK;
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        if (a.x == b.x) {
            drawer.line(board.bx(a.x), board.getY(), board.bx(b.x), board.getY() + board.getHeight(), lineColor, (float)Math.min(Math.max(1, board.getScale()), 4));
        } else {
            drawer.line(board.getX(), board.by(formula.apply(board.minX())), board.getX() + board.getWidth(), board.by(formula.apply(board.maxX())), lineColor, (float)Math.min(Math.max(1, board.getScale()), 4));
        }
        drawer.setColor(0, 0.5f, 1, 1);
        drawer.circle(board.bx(a.x), board.by(a.y), (float)Math.min(Math.max(3, board.getScale()), 8), 2);
        drawer.setColor(0.5f, 0.5f, 1, 1);
        drawer.circle(board.bx(b.x), board.by(b.y), (float)Math.min(Math.max(3, board.getScale()), 8), 2);
    }
}

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
        double k = (a.y - b.y) / (a.x - b.x);
        return x -> k * (x - a.x) + a.y;
    }

    @Override
    public boolean contains(Vec2 point) {
        return compileFormula().apply(point.x) == point.y;
    }

    @Override
    public boolean contains(double x, double y) {
        return compileFormula().apply(x) == y;
    }

    @Override
    public boolean containsRough(Vec2 point) {
        return Math.abs(compileFormula().apply(point.x) - point.y) <= 2;
    }

    @Override
    public boolean containsRough(double x, double y) {
        return Math.abs(compileFormula().apply(x) - y) <= 2;
    }

    @Override
    public void render(ShapeDrawer drawer, boolean selected, FontProvider font, DrawingBoard board) {
        DoubleFunction<Double> formula = compileFormula();
        Color lineColor = selected ? DynamicPlanimetry.COLOR_SELECTION : Color.BLACK;
        drawer.line(board.getX(), board.by(formula.apply(board.minX())), board.getX() + board.getWidth(), board.by(formula.apply(board.maxX())), lineColor, (float)Math.min(Math.max(1, board.getScale()), 4));
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        drawer.setColor(0, 0.5f, 1, 1);
        drawer.circle(board.bx(a.x), board.by(a.y), (float)Math.min(Math.max(3, board.getScale()), 8), 2);
        drawer.setColor(0.5f, 0.5f, 1, 1);
        drawer.circle(board.bx(b.x), board.by(b.y), (float)Math.min(Math.max(3, board.getScale()), 8), 2);
    }
}

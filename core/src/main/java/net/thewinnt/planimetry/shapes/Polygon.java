package net.thewinnt.planimetry.shapes;

import java.util.Collection;

import com.badlogic.gdx.graphics.Color;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.lines.MultiPointLine;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Polygon extends MultiPointLine {
    public Polygon(PointProvider... points) {
        super(points);
    }

    public Polygon(Collection<PointProvider> points) {
        super(points);
    }

    @Override
    public boolean contains(Vec2 point) {
        for (int i = 0; i < points.size() - 1; i++) {
            if (MathHelper.isPointOnSegment(points.get(i).getPosition(), points.get(i + 1).getPosition(), point)) {
                return true;
            }
        }
        return MathHelper.isPointOnSegment(points.get(0).getPosition(), points.get(points.size() - 1).getPosition(), point);
    }

    @Override
    public boolean contains(double x, double y) {
        for (int i = 0; i < points.size() - 1; i++) {
            if (MathHelper.isPointOnSegment(points.get(i).getPosition(), points.get(i + 1).getPosition(), new Vec2(x, y))) {
                return true;
            }
        }
        return MathHelper.isPointOnSegment(points.get(0).getPosition(), points.get(points.size() - 1).getPosition(), new Vec2(x, y));
    }

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < points.size() - 1; i++) {
            double cache = MathHelper.distanceToLine(points.get(i).getPosition(), points.get(i + 1).getPosition(), point);
            if (cache < minDist) {
                minDist = cache;
            }
        }
        double cache = MathHelper.distanceToLine(points.get(0).getPosition(), points.get(points.size() - 1).getPosition(), point);
        if (cache < minDist) {
            minDist = cache;
        }
        return minDist;
    }

    @Override
    public double distanceToMouse(double x, double y, DrawingBoard board) {
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < points.size() - 1; i++) {
            double cache = MathHelper.distanceToLine(points.get(i).getPosition(), points.get(i + 1).getPosition(), new Vec2(x, y));
            if (cache < minDist) {
                minDist = cache;
            }
        }
        double cache = MathHelper.distanceToLine(points.get(0).getPosition(), points.get(points.size() - 1).getPosition(), new Vec2(x, y));
        if (cache < minDist) {
            minDist = cache;
        }
        return minDist;
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        super.render(drawer, selection, font, board);
        Color lineColor = switch (selection) {
            default -> DynamicPlanimetry.COLOR_SHAPE;
            case HOVERED -> DynamicPlanimetry.COLOR_SHAPE_HOVER;
            case SELECTED -> DynamicPlanimetry.COLOR_SHAPE_SELECTED;
        };
        Vec2 a, b;
        a = points.get(0).getPosition();
        b = points.get(points.size() - 1).getPosition();
        drawer.line(board.bx(a.x), board.by(a.y), board.bx(b.x), board.by(b.y), lineColor, getThickness(board.getScale()));
    }
}

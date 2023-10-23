package net.thewinnt.planimetry.shapes.lines;

import com.badlogic.gdx.graphics.Color;

import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class LineSegment extends Line {
    public LineSegment(PointReference a, PointReference b) {
        super(a, b);
    }

    public LineSegment(PointProvider a, PointProvider b) {
        super(a, b);
    }

    @Override
    public boolean contains(double x, double y) {
        return a.getPosition().distanceTo(x, y) + b.getPosition().distanceTo(x, y) - a.getPosition().distanceTo(b.getPosition()) < Math.pow(2, -23);
    }

    @Override
    public boolean contains(Vec2 point) {
        return a.getPosition().distanceTo(point) + b.getPosition().distanceTo(point) - a.getPosition().distanceTo(b.getPosition()) < Math.pow(2, -23);
    }   

    @Override
    public double distanceToMouse(Vec2 point, DrawingBoard board) {
        Vec2 a = this.a.getPosition();
        Vec2 b = this.b.getPosition();
        double distance = Math.abs((b.x - a.x)*(a.y - point.y) - (a.x - point.x)*(b.y - a.y)) / a.distanceTo(b);
        double slope = Math.tan(Math.atan(this.getSlope()) + Math.PI / 2);
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
        Color lineColor = switch (selection) {
            default -> DynamicPlanimetry.COLOR_SHAPE;
            case HOVERED -> DynamicPlanimetry.COLOR_SHAPE_HOVER;
            case SELECTED -> DynamicPlanimetry.COLOR_SHAPE_SELECTED;
        };
        Vec2 a, b;
        a = this.a.getPosition();
        b = this.b.getPosition();
        drawer.line(board.bx(a.x), board.by(a.y), board.bx(b.x), board.by(b.y), lineColor, (float)Math.min(Math.max(1, board.getScale() / 2), 4));
        if (selection == SelectionStatus.SELECTED) {
            if (!board.getShapes().contains(this.a)) {
                this.a.render(drawer, SelectionStatus.NONE, font, board);
            }
            if (!board.getShapes().contains(this.b)) {
                this.b.render(drawer, SelectionStatus.NONE, font, board);
            }
        }
    }
}

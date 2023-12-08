package net.thewinnt.planimetry.shapes.factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.lines.InfiniteLine;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.shapes.lines.LineSegment;
import net.thewinnt.planimetry.shapes.lines.Ray;
import net.thewinnt.planimetry.shapes.point.MousePoint;
import net.thewinnt.planimetry.shapes.point.Point;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;

public class LineFactory extends ShapeFactory {
    private final LineType type;
    private PointReference point1;
    private PointReference point2;
    private boolean addedPoint1;

    public LineFactory(DrawingBoard board, LineType type) {
        super(board);
        this.point1 = new PointReference(new MousePoint(board.getDrawing()));
        this.type = type;
        this.addedPoint1 = false;
    }

    public LineFactory(DrawingBoard board, LineType type, PointProvider point1) {
        super(board);
        this.type = type;
        if (point1 == null) {
            this.point1 = new PointReference(new MousePoint(board.getDrawing()));
            this.addedPoint1 = false;
        } else {
            this.point1 = new PointReference(point1);
            this.point2 = new PointReference(new MousePoint(board.getDrawing()));
            this.addShape(this.type.create(board.getDrawing(), new PointReference(point1), point2));
            this.addedPoint1 = true;
        }
    }
    
    @Override
    public boolean click(InputEvent event, double x, double y) {
        this.board.setSelection(null);
        if (point2 == null) {
            PointProvider p1 = (PointProvider) board.getHoveredShape(Gdx.input.getX(), Gdx.input.getY(), shape -> shape instanceof PointProvider);
            if (p1 != null) {
                this.point1.setPoint(p1);
            } else {
                this.point1.setPoint(new Point(board.getDrawing(), new Vec2(x, y)));
            }
            this.point2 = new PointReference(new MousePoint(board.getDrawing()));
        } else if (point2.getPoint() instanceof MousePoint) {
            PointProvider p2 = (PointProvider) board.getHoveredShape(Gdx.input.getX(), Gdx.input.getY(), shape -> shape instanceof PointProvider);
            if (p2 != null) {
                this.point2.setPoint(p2);
            } else {
                this.point2.setPoint(new Point(board.getDrawing(), new Vec2(x, y)));
            }
            this.addShape(point2);
            return true;
        }
        if (!this.addedPoint1) {
            this.addShape(this.type.create(board.getDrawing(), point1, point2));
            this.addShape(point1);
            addedPoint1 = true;
        }
        return false;
    }

    @Override
    public boolean isDone() {
        return point2 != null && point2.getPoint() instanceof Point;
    }

    public static enum LineType {
        INFINITE(InfiniteLine::new),
        RAY(Ray::new),
        SEGMENT(LineSegment::new);

        public final LineConstructor<?> factory;
        private LineType(LineConstructor<?> factory) {
            this.factory = factory;
        }

        public Line create(Drawing drawing, PointProvider a, PointProvider b) {
            return this.factory.create(drawing, a, b);
        }
    }

    @FunctionalInterface
    public static interface LineConstructor<T extends Line> {
        T create(Drawing drawing, PointProvider a, PointProvider b);
    }
}

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
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.ComponentRepresentable;
import net.thewinnt.planimetry.ui.text.LiteralComponent;

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

    public static enum LineType implements ComponentRepresentable {
        INFINITE(InfiniteLine::new, Component.literal("Прямая")),
        RAY(Ray::new, Component.literal("Луч")),
        SEGMENT(LineSegment::new, Component.literal("Отрезок"));

        public final LineConstructor<?> factory;
        public final Component name;

        private LineType(LineConstructor<?> factory, Component name) {
            this.factory = factory;
            this.name = name;
        }

        public Line create(Drawing drawing, PointProvider a, PointProvider b) {
            return this.factory.create(drawing, a, b);
        }

        @Override
        public Component toComponent() {
            return name;
        }
    }

    @FunctionalInterface
    public static interface LineConstructor<T extends Line> {
        T create(Drawing drawing, PointProvider a, PointProvider b);
    }

    @Override
    public Component getName() {
        return (LiteralComponent)type.name;
    }
}

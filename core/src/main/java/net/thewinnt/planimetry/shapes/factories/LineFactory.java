package net.thewinnt.planimetry.shapes.factories;

import java.util.function.BiFunction;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.lines.InfiniteLine;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.shapes.point.MousePoint;
import net.thewinnt.planimetry.shapes.point.Point;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;

public class LineFactory extends ShapeFactory {
    private final LineType type;
    private PointReference point1;
    private PointReference point2;
    private boolean addedPoint1; // TODO this

    public LineFactory(DrawingBoard board, LineType type) {
        super(board);
        this.point1 = new PointReference(new MousePoint(board));
        this.type = type;
        this.addedPoint1 = false;
    }

    public LineFactory(DrawingBoard board, LineType type, PointProvider point1) {
        super(board);
        this.point1 = new PointReference(point1);
        this.point2 = new PointReference(new MousePoint(board));
        this.type = type;
        this.addedPoint1 = true;
        this.board.addShape(this.type.create(new PointReference(point1), point2));
    }
    
    @Override
    public boolean click(InputEvent event, double x, double y) {
        if (point2 == null) {
            this.point1.setPoint(new Point(new Vec2(x, y)));
            this.point2 = new PointReference(new MousePoint(board));
        } else if (point2.getPoint() instanceof MousePoint) {
            this.point2.setPoint(new Point(new Vec2(x, y)));
            this.board.addShape(point2);
            return true;
        }
        if (!this.addedPoint1) {
            this.board.addShape(this.type.create(point1, point2));
            this.board.addShape(point1);
            addedPoint1 = true;
        }
        return false;
    }

    @Override
    public boolean isDone() {
        return point2 != null && point2.getPoint() instanceof Point;
    }

    public static enum LineType {
        INFINITE(InfiniteLine::new);

        public final BiFunction<PointProvider, PointProvider, Line> factory;
        private LineType(BiFunction<PointProvider, PointProvider, Line> factory) {
            this.factory = factory;
        }

        public Line create(PointProvider a, PointProvider b) {
            return this.factory.apply(a, b);
        }
    }
}

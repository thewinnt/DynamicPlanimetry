package net.thewinnt.planimetry.shapes.factories;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.shapes.Circle;
import net.thewinnt.planimetry.shapes.lines.LineSegment;
import net.thewinnt.planimetry.shapes.point.MousePoint;
import net.thewinnt.planimetry.shapes.point.Point;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;

public class CircleFactory extends ShapeFactory {
    private PointReference point = null;
    private boolean addCenter = true;
    private boolean addRadius;
    private boolean addSecondaryPoint;
    private boolean keepRadius = true;

    public CircleFactory(DrawingBoard board) {
        super(board);
    }
    
    @Override
    public boolean click(InputEvent event, double x, double y) {
        if (this.point == null) {
            this.point = new PointReference(new MousePoint());
            PointProvider center = getOrCreatePoint(x, y);
            this.addShape(new Circle(center, point, keepRadius));
            if (addCenter) this.addShape(center);
            if (addSecondaryPoint || addRadius) this.addShape(point);
            if (addRadius) this.addShape(new LineSegment(center, point));
            return false;
        } else if (this.point.getPoint() instanceof MousePoint) {
            this.point.setPoint(getOrCreatePoint(x, y));
        }
        return true;
    }

    @Override
    public boolean isDone() {
        return this.point != null && this.point.getPoint() instanceof Point;
    }

    public boolean getAddCenter() {
        return addCenter;
    }

    public CircleFactory setAddCenter(boolean addCenter) {
        this.addCenter = addCenter;
        return this;
    }

    public boolean getAddRadius() {
        return addRadius;
    }

    public CircleFactory setAddRadius(boolean addRadius) {
        this.addRadius = addRadius;
        return this;
    }

    public boolean getAddSecondaryPoint() {
        return addSecondaryPoint;
    }

    public CircleFactory setAddSecondaryPoint(boolean addSecondaryPoint) {
        this.addSecondaryPoint = addSecondaryPoint;
        return this;
    }

    public boolean getKeepRadius() {
        return keepRadius;
    }

    public CircleFactory setKeepRadius(boolean keepRadius) {
        this.keepRadius = keepRadius;
        return this;
    }
}

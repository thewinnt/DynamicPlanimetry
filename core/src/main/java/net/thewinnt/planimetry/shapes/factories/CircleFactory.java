package net.thewinnt.planimetry.shapes.factories;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.definition.point.placement.MousePlacement;
import net.thewinnt.planimetry.shapes.Circle;
import net.thewinnt.planimetry.shapes.lines.LineSegment;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class CircleFactory extends ShapeFactory {
    private PointProvider point = null;
    private boolean addCenter = true;
    private boolean addRadius;
    private boolean keepRadius = true;

    public CircleFactory(DrawingBoard board) {
        super(board);
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        if (this.point == null) {
            PointProvider center = getOrCreatePoint(x, y);
            this.point = PointProvider.mouse(board.getDrawing());
            this.addShape(new Circle(board.getDrawing(), center, point, keepRadius));
            if (addCenter) this.addShape(center);
            if (addRadius) this.addShape(new LineSegment(board.getDrawing(), center, point));
            return false;
        } else if (this.point.getPlacement() instanceof MousePlacement) {
            this.point.freeze();
            this.point.setShouldRender(false);
            this.addShape(point);
        }
        return true;
    }

    @Override
    public boolean isDone() {
        return this.point != null && !(this.point.getPlacement() instanceof MousePlacement);
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

    public boolean getKeepRadius() {
        return keepRadius;
    }

    public CircleFactory setKeepRadius(boolean keepRadius) {
        this.keepRadius = keepRadius;
        return this;
    }

    @Override
    public Component getName() {
        return Component.translatable("shape.factory.circle");
    }

    @Override
    public Collection<Component> getActionHint() {
        if (point == null) {
            return List.of(Component.translatable("shape.factory.hint.circle.choose_center"));
        } else {
            return List.of(Component.translatable("shape.factory.hint.circle.choose_radius"));
        }
    }
}

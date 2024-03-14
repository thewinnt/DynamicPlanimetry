package net.thewinnt.planimetry.shapes.factories;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.shapes.display.angle.PointAngleMarker;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class PointAngleMarkerFactory extends ShapeFactory {
    private PointProvider a;
    private PointProvider b;
    private boolean done = false;

    public PointAngleMarkerFactory(DrawingBoard board) {
        super(board);
    }
    
    @Override
    public boolean click(InputEvent event, double x, double y) {
        if (a == null) {
            a = getOrCreatePoint(x, y);
        } else if (b == null) {
            b = getOrCreatePoint(x, y);
        } else {
            addShape(new PointAngleMarker(board.getDrawing(), a, b, getOrCreatePoint(x, y)));
            done = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public Component getName() {
        return Component.translatable("shape.factory.angle_marker.point");
    }
}

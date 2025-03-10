package net.thewinnt.planimetry.shapes.factories;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

// import net.thewinnt.planimetry.shapes.lines.AngledInfiniteLine;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.shapes.point.MousePoint;
import net.thewinnt.planimetry.shapes.point.PointReference;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class AngledLineFactory extends ShapeFactory {
    // private final Line newLine;
    private final PointReference point;
    private final Component name;
    private boolean isDone;

    public AngledLineFactory(DrawingBoard board, Line originalLine, double angle) {
        super(board);
        // TODO angled lines
        this.point = new PointReference(createMousePoint());
        // this.newLine = new AngledInfiniteLine(board.getDrawing(), originalLine, point, Settings.get().toRadians(angle));
        this.name = Component.translatable("shape.factory.angled_line", originalLine.getName());
        // addShape(newLine);
        addShape(point);
    }

    protected MousePoint createMousePoint() {
        return new MousePoint(board.getDrawing());
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        this.point.setPoint(getOrCreatePoint(x, y));
        isDone = true;
        return true;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public Component getName() {
        return name;
    }

    @Override
    public Collection<Component> getActionHint() {
        return List.of(Component.translatable("shape.factory.hint.angled_line.click"));
    }
}

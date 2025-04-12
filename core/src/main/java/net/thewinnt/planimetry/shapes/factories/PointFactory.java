package net.thewinnt.planimetry.shapes.factories;

import java.util.Collection;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import net.thewinnt.planimetry.definition.point.placement.MousePlacement;
import net.thewinnt.planimetry.definition.point.placement.StaticPlacement;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.text.Component;

public class PointFactory extends ShapeFactory {
    private final PointProvider point;

    public PointFactory(DrawingBoard board) {
        super(board);
        this.point = PointProvider.mouse(board.getDrawing());
        this.addShape(point);
    }

    @Override
    public boolean click(InputEvent event, double x, double y) {
        this.point.setPlacement(new StaticPlacement(new Vec2(x, y)));
        return true;
    }

    @Override
    public boolean isDone() {
        return !(point.getPlacement() instanceof MousePlacement);
    }

    @Override
    public Component getName() {
        return Component.translatable("shape.factory.point");
    }

    @Override
    public Collection<Component> getActionHint() {
        return List.of(Component.translatable("shape.factory.hint.point.place"));
    }
}

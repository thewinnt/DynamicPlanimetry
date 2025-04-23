package net.thewinnt.planimetry.definition.point.placement;

import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.ui.GuiTheme;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.Formatting;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ShapeSlavePlacement extends PointPlacement {
    private final Shape parent;
    private final Supplier<Vec2> positionSupplier;

    public ShapeSlavePlacement(Shape parent, Supplier<Vec2> positionSupplier) {
        this.parent = parent;
        this.positionSupplier = positionSupplier;
    }

    @Override
    public Vec2 get() {
        return positionSupplier.get();
    }

    @Override
    public void move(Vec2 delta) {}

    @Override
    public void move(double dx, double dy) {}

    @Override
    public boolean canMove() {
        return false;
    }

    @Override
    public Collection<Property<?>> properties() {
        return List.of(
            new DisplayProperty(Component.translatable("shape.slave", new Formatting(GuiTheme.current().textWarning(), true)))
        );
    }

    @Override
    public PointPlacementType<?> type() {
        return null;
    }

    @Override
    public List<Shape> dependencies() {
        return List.of();
    }
}

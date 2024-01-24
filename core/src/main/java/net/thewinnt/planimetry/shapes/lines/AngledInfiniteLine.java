package net.thewinnt.planimetry.shapes.lines;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.relative.TangentOffsetPoint;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.ShapeProperty;
import net.thewinnt.planimetry.ui.properties.types.EnclosingProperty;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.ui.text.LiteralComponent;
import net.thewinnt.planimetry.ui.text.NameComponent;

public class AngledInfiniteLine extends InfiniteLine {
    public static final LiteralComponent SOURCE_PROPERTY = Component.literal("Исходная прямая");
    public static final LiteralComponent ANGLE_PROPERTY = Component.literal("Угол к прямой");
    public static final LiteralComponent HELPER_POINT = Component.literal("Вспомогательная точка");
    public static final NameComponent DUMMY_NAME = new NameComponent(0, 0, 0);
    private final ShapeProperty sourceProperty;
    private final NumberProperty angleProperty;
    private final Consumer<Vec2> movementListener;
    private Line base;
    private double angle;
    private PointProvider point;

    public AngledInfiniteLine(Drawing drawing, Line baseLine, PointProvider point, double angleDeg) {
        super(drawing, point, new TangentOffsetPoint(drawing, point, Math.tan(Math.atan(baseLine.getSlope()) + angleDeg), 1, DUMMY_NAME));
        this.movementListener = delta -> ((TangentOffsetPoint)b.getPoint()).setAngle(Math.tan(Math.atan(getBase().getSlope()) + getAngle()));
        this.b.getPoint().setNameOverride(HELPER_POINT);
        this.base = baseLine;
        this.point = point;
        this.angle = angleDeg;
        base.a.addMovementListener(movementListener);
        base.b.addMovementListener(movementListener);
        point.addMovementListener(movementListener);
        this.sourceProperty = new ShapeProperty(SOURCE_PROPERTY, drawing, baseLine, shape -> shape != this && shape instanceof Line && !(shape instanceof AngledInfiniteLine));
        this.sourceProperty.addValueChangeListener(shape -> setBase((Line)shape)); // we know shape is a line, because we filtered it beforehand
        this.angleProperty = new NumberProperty(ANGLE_PROPERTY, Math.toDegrees(angle));
        this.angleProperty.addValueChangeListener(newAngle -> {
            AngledInfiniteLine.this.angle = Math.toRadians(newAngle);
            ((TangentOffsetPoint)b.getPoint()).setAngle(Math.tan(Math.atan(base.getSlope()) + angle));
        });
    }

    public Line getBase() {
        return base;
    }

    public double getAngle() {
        return angle;
    }

    public void setBase(Line base) {
        this.base.a.removeMovementListener(movementListener);
        this.base.b.removeMovementListener(movementListener);
        this.point.removeMovementListener(movementListener);
        this.base = base;
        base.a.addMovementListener(movementListener);
        base.b.addMovementListener(movementListener);
        point.addMovementListener(movementListener);
        movementListener.accept(null);
    }

    @Override
    public Collection<Property<?>> getProperties() {
        return List.of(sourceProperty, angleProperty, new EnclosingProperty(point.getName(), point.getProperties()));
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
    CompoundTag nbt = new CompoundTag();
        nbt.putLong("base", base.getId());
        context.addShape(base);
        nbt.putLong("point", point.getId());
        context.addShape(point);
        nbt.putDouble("angle", angle);
        return nbt;
    }

    @Override
    public ShapeDeserializer<AngledInfiniteLine> getDeserializer() {
        return ShapeData.ANGLED_INFINITE_LINE;
    }

    public static AngledInfiniteLine readNbt(CompoundTag nbt, LoadingContext context) {
        Line base = (Line)context.resolveShape(nbt.getLong("base").getValue());
        PointProvider point = (PointProvider)context.resolveShape(nbt.getLong("point").getValue());
        double angle = nbt.getDouble("angle").doubleValue();
        return new AngledInfiniteLine(context.getDrawing(), base, point, angle);
    }
}

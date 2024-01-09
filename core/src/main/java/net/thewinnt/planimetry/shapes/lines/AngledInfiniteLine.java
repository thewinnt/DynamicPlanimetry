package net.thewinnt.planimetry.shapes.lines;

import java.util.Collection;
import java.util.List;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.relative.TangentOffsetPoint;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.properties.types.EnclosingProperty;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.text.Component;

public class AngledInfiniteLine extends InfiniteLine {
    private final DisplayProperty sourceProperty;
    private final NumberProperty angleProperty;
    private Line base;
    private double angle;
    private PointProvider point;

    public AngledInfiniteLine(Drawing drawing, Line baseLine, PointProvider point, double angleDeg) {
        super(drawing, point, new TangentOffsetPoint(drawing, point, Math.tan(Math.atan(baseLine.getSlope()) + angleDeg), 1));
        this.base = baseLine;
        this.point = point;
        this.angle = angleDeg;
        base.a.addMovementListener(delta -> ((TangentOffsetPoint)b.getPoint()).setAngle(Math.tan(Math.atan(base.getSlope()) + angle)));
        base.b.addMovementListener(delta -> ((TangentOffsetPoint)b.getPoint()).setAngle(Math.tan(Math.atan(base.getSlope()) + angle)));
        point.addMovementListener(delta -> ((TangentOffsetPoint)b.getPoint()).setAngle(Math.tan(Math.atan(base.getSlope()) + angle)));
        this.sourceProperty = new DisplayProperty(Component.literal("Исходная прямая"), () -> base.getName());
        this.angleProperty = new NumberProperty(Component.literal("Угол к прямой"), Math.toDegrees(angle));
        this.angleProperty.addValueChangeListener(newAngle -> {
            AngledInfiniteLine.this.angle = Math.toRadians(newAngle);
            ((TangentOffsetPoint)b.getPoint()).setAngle(Math.tan(Math.atan(base.getSlope()) + angle));
        });
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

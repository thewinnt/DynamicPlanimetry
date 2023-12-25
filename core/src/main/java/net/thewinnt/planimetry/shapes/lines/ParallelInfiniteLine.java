package net.thewinnt.planimetry.shapes.lines;

import java.util.Collection;
import java.util.List;

import dev.dewy.nbt.tags.collection.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.shapes.point.relative.TangentOffsetPoint;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.text.Component;

public class ParallelInfiniteLine extends InfiniteLine {
    private final DisplayProperty sourceProperty;
    private final NumberProperty offsetProperty;
    private Line base;
    private double offset;

    public ParallelInfiniteLine(Drawing drawing, Line base, double offset) {
        super(drawing, new TangentOffsetPoint(drawing, base.a, Math.tan(Math.atan(base.getSlope()) + MathHelper.HALF_PI), offset), new TangentOffsetPoint(drawing, base.b, Math.tan(Math.atan(base.getSlope()) + MathHelper.HALF_PI), offset));
        this.base = base;
        this.offset = offset;
        TangentOffsetPoint a = (TangentOffsetPoint)this.a.getPoint();
        TangentOffsetPoint b = (TangentOffsetPoint)this.b.getPoint();
        base.a.addMovementListener(delta -> {
            a.setAngle(Math.tan(Math.atan(base.getSlope()) + MathHelper.HALF_PI));
            b.setAngle(Math.tan(Math.atan(base.getSlope()) + MathHelper.HALF_PI));
        });
        base.b.addMovementListener(delta -> {
            a.setAngle(Math.tan(Math.atan(base.getSlope()) + MathHelper.HALF_PI));
            b.setAngle(Math.tan(Math.atan(base.getSlope()) + MathHelper.HALF_PI));
        });
        this.sourceProperty = new DisplayProperty(Component.literal("Исходная прямая"), () -> base.getName());
        this.offsetProperty = new NumberProperty(Component.literal("Расстояние"), offset);
        this.offsetProperty.addValueChangeListener(newValue -> setOffset(newValue));
    }

    @Override
    public Collection<Property<?>> getProperties() {
        return List.of(sourceProperty, offsetProperty);
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("base", base.getId());
        nbt.putDouble("offset", offset);
        return nbt;
    }

    @Override
    public ShapeDeserializer<ParallelInfiniteLine> getDeserializer() {
        return ShapeData.PARALLEL_INFINITE_LINE;
    }

    public static ParallelInfiniteLine readNbt(CompoundTag nbt, LoadingContext context) {
        Line base = (Line)context.resolveShape(nbt.getLong("base").getValue());
        double offset = nbt.getDouble("offset").doubleValue();
        return new ParallelInfiniteLine(context.getDrawing(), base, offset);
    }

    public void setOffset(double offset) {
        this.offset = offset;
        ((TangentOffsetPoint)this.a.getPoint()).setOffset(offset);
        ((TangentOffsetPoint)this.b.getPoint()).setOffset(offset);
    }
}

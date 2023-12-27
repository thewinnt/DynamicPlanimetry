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
import net.thewinnt.planimetry.ui.text.Component;

public class ParallelInfiniteLine extends InfiniteLine {
    private final DisplayProperty sourceProperty;
    private Line base;
    private PointProvider point;

    public ParallelInfiniteLine(Drawing drawing, Line base, PointProvider point) {
        super(drawing, point, new TangentOffsetPoint(drawing, point, base.getSlope(), 1));
        this.base = base;
        this.point = point;
        TangentOffsetPoint b = (TangentOffsetPoint)this.b.getPoint();
        base.a.addMovementListener(delta -> b.setAngle(base.getSlope()));
        base.b.addMovementListener(delta -> b.setAngle(base.getSlope()));
        point.addMovementListener(delta -> b.setAngle(base.getSlope()));
        this.sourceProperty = new DisplayProperty(Component.literal("Исходная прямая"), () -> base.getName());
    }

    @Override
    public Collection<Property<?>> getProperties() {
        return List.of(sourceProperty, new EnclosingProperty(point.getName(), point.getProperties()));
    }

    @Override
    public CompoundTag writeNbt(SavingContext context) {
    CompoundTag nbt = new CompoundTag();
        nbt.putLong("base", base.getId());
        context.addShape(base);
        nbt.putLong("point", point.getId());
        context.addShape(point);
        return nbt;
    }

    @Override
    public ShapeDeserializer<ParallelInfiniteLine> getDeserializer() {
        return ShapeData.PARALLEL_INFINITE_LINE;
    }

    public static ParallelInfiniteLine readNbt(CompoundTag nbt, LoadingContext context) {
        Line base = (Line)context.resolveShape(nbt.getLong("base").getValue());
        PointProvider point = (PointProvider)context.resolveShape(nbt.getLong("point").getValue());
        return new ParallelInfiniteLine(context.getDrawing(), base, point);
    }
}

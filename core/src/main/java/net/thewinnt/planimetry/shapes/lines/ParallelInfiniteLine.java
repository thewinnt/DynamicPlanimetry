package net.thewinnt.planimetry.shapes.lines;

import java.util.Collection;
import java.util.List;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.ShapeData;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.shapes.point.relative.TangentOffsetPoint;
import net.thewinnt.planimetry.ui.DrawingBoard;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.DisplayProperty;
import net.thewinnt.planimetry.ui.properties.types.EnclosingProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.util.FontProvider;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class ParallelInfiniteLine extends InfiniteLine {
    private final DisplayProperty sourceProperty;
    private Line base;
    private PointProvider point;

    public ParallelInfiniteLine(Drawing drawing, Line base, PointProvider point) {
        super(drawing, point, new TangentOffsetPoint(drawing, point, base.getSlope(), 1));
        this.base = base;
        this.point = point;
        this.sourceProperty = new DisplayProperty(Component.translatable("property.parallel_line.source"), () -> base.getName());
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
        Line base = (Line)context.resolveShape(nbt.getLong("base"));
        PointProvider point = (PointProvider)context.resolveShape(nbt.getLong("point"));
        return new ParallelInfiniteLine(context.getDrawing(), base, point);
    }

    @Override
    public void render(ShapeDrawer drawer, SelectionStatus selection, FontProvider font, DrawingBoard board) {
        ((TangentOffsetPoint)this.b.getPoint()).setAngle(this.base.getSlope());
        super.render(drawer, selection, font, board);
    }

    @Override
    public void move(Vec2 delta) {
        this.a.move(delta);
    }

    @Override
    public void move(double dx, double dy) {
        this.a.move(dx, dy);
    }
}

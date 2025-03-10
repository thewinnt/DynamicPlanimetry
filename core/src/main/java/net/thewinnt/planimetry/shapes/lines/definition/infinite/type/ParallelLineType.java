package net.thewinnt.planimetry.shapes.lines.definition.infinite.type;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.shapes.lines.definition.infinite.InfiniteLineDefinition;
import net.thewinnt.planimetry.shapes.lines.definition.infinite.InfiniteLineType;
import net.thewinnt.planimetry.shapes.lines.definition.infinite.ParallelLineDefinition;
import net.thewinnt.planimetry.shapes.point.PointProvider;

public class ParallelLineType implements InfiniteLineType<ParallelLineDefinition> {
    public static final ParallelLineType INSTANCE = new ParallelLineType();

    private ParallelLineType() {}

    @Override
    public CompoundTag writeNbt(InfiniteLineDefinition object, SavingContext context) {
        ParallelLineDefinition definition = (ParallelLineDefinition) object;
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("line", context.addShape(definition.getLine()));
        nbt.putLong("point", context.addShape(definition.getPoint()));
        return nbt;
    }

    public ParallelLineDefinition fromNbt(CompoundTag nbt, LoadingContext context) {
        Line line = context.resolveShape(nbt.getLong("line"));
        PointProvider point = context.resolveShape(nbt.getLong("point"));
        return new ParallelLineDefinition(line, point);
    };

    @Override
    public ParallelLineDefinition convert(InfiniteLineDefinition other, Drawing drawing) {
        return new ParallelLineDefinition((Line) drawing.getRandom(t -> t instanceof Line && t != other.getSource()), drawing.getNearestPoint(other.point1()));
    }
}

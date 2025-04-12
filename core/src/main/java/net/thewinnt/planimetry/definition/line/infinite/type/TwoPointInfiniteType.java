package net.thewinnt.planimetry.definition.line.infinite.type;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineDefinition;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineType;
import net.thewinnt.planimetry.definition.line.infinite.impl.TwoPointInfiniteLine;

public class TwoPointInfiniteType implements InfiniteLineType<TwoPointInfiniteLine> {
    public static final TwoPointInfiniteType INSTANCE = new TwoPointInfiniteType();

    private TwoPointInfiniteType() {}

    @Override
    public TwoPointInfiniteLine fromNbt(CompoundTag nbt, LoadingContext context) {
        return TwoPointInfiniteLine.fromNbt(nbt, context);
    }

    @Override
    public CompoundTag writeNbt(InfiniteLineDefinition definition, SavingContext context) {
        return ((TwoPointInfiniteLine)definition).writeNbt(context);
    }

    @Override
    public TwoPointInfiniteLine convert(InfiniteLineDefinition other, Drawing drawing) {
        Vec2 point1 = other.point1();
        Vec2 point2 = other.point2();
        return new TwoPointInfiniteLine(drawing.getNearestPoint(point1.x, point1.y), drawing.getNearestPoint(point2.x, point2.y));
    }
}

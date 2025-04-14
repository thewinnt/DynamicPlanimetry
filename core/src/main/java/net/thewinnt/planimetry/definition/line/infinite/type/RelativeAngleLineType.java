package net.thewinnt.planimetry.definition.line.infinite.type;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineDefinition;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineType;
import net.thewinnt.planimetry.definition.line.infinite.impl.RelativeAngleLineDefinition;
import net.thewinnt.planimetry.shapes.lines.Line;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.type.AngleValue;

public class RelativeAngleLineType implements InfiniteLineType<RelativeAngleLineDefinition> {
    public static final RelativeAngleLineType INSTANCE = new RelativeAngleLineType();

    private RelativeAngleLineType() {}

    @Override
    public CompoundTag writeNbt(InfiniteLineDefinition object, SavingContext context) {
        RelativeAngleLineDefinition definition = (RelativeAngleLineDefinition) object;
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("line", context.addShape(definition.getLine()));
        nbt.putLong("point", context.addShape(definition.getPoint()));
        nbt.put("angle", DynamicValue.toNbt(definition.getAngle(), context));
        return nbt;
    }

    public RelativeAngleLineDefinition fromNbt(CompoundTag nbt, LoadingContext context) {
        Line line = context.resolveShape(nbt.getLong("line"));
        PointProvider point = context.resolveShape(nbt.getLong("point"));
        DynamicValue angle = DynamicValue.fromNbt(nbt.getCompoundTag("angle"), context);
        return new RelativeAngleLineDefinition(line, point, angle);
    };

    @Override
    public RelativeAngleLineDefinition convert(InfiniteLineDefinition other, Drawing drawing) {
        return new RelativeAngleLineDefinition((Line) drawing.getRandom(t -> t instanceof Line && t != other.getSource()), drawing.getNearestPoint(other.point1()), new AngleValue(0));
    }
}

package net.thewinnt.planimetry.definition.line.infinite.type;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.definition.line.infinite.impl.AngleBasedLineDefinition;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineDefinition;
import net.thewinnt.planimetry.definition.line.infinite.InfiniteLineType;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.type.AngleValue;
import net.thewinnt.planimetry.value.type.ConstantValue;

public class AngleLineType implements InfiniteLineType<AngleBasedLineDefinition> {
    public static final AngleLineType INSTANCE = new AngleLineType();

    private AngleLineType() {}

    @Override
    public CompoundTag writeNbt(InfiniteLineDefinition object, SavingContext context) {
        AngleBasedLineDefinition definition = (AngleBasedLineDefinition) object;
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("point", context.addShape(definition.getPoint()));
        nbt.put("angle", DynamicValue.toNbt(definition.getAngle(), context));
        return nbt;
    }

    public AngleBasedLineDefinition fromNbt(CompoundTag nbt, LoadingContext context) {
        PointProvider point = context.resolveShape(nbt.getLong("point"));
        DynamicValue angle = DynamicValue.fromNbt(nbt.getCompoundTag("angle"), context);
        return new AngleBasedLineDefinition(point, angle);
    }

    @Override
    public AngleBasedLineDefinition convert(InfiniteLineDefinition other, Drawing drawing) {
        return new AngleBasedLineDefinition(other.getBasePoint(), new AngleValue(MathHelper.angleTo(other.point1(), other.point2())));
    }
}

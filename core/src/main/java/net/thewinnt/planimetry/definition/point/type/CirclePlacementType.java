package net.thewinnt.planimetry.definition.point.type;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.definition.point.ValueContext;
import net.thewinnt.planimetry.definition.point.placement.CirclePlacement;
import net.thewinnt.planimetry.shapes.Circle;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.type.AngleValue;
import net.thewinnt.planimetry.value.type.ConstantValue;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class CirclePlacementType implements PointPlacementType<CirclePlacement> {
    public static final CirclePlacementType INSTANCE = new CirclePlacementType();

    private CirclePlacementType() {}

    @Override
    public @Nullable CirclePlacement createRandom(Random random, ValueContext context) {
        return null;
    }

    @Override
    public CirclePlacement convert(PointPlacement other, Drawing drawing) {
        return new CirclePlacement((Circle)drawing.getRandom(shape -> shape instanceof Circle), new AngleValue(0));
    }

    @Override
    public CirclePlacement fromNbt(CompoundTag nbt, LoadingContext context) {
        Circle circle = context.resolveShape(nbt.getLong("circle"));
        DynamicValue angle = DynamicValue.fromNbt(nbt.getCompoundTag("angle"), context);
        return new CirclePlacement(circle, angle);
    }

    @Override
    public CompoundTag writeNbt(PointPlacement object, SavingContext context) {
        CirclePlacement placement = (CirclePlacement) object;
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("circle", context.addShape(placement.getCircle()));
        nbt.put("angle", DynamicValue.toNbt(placement.getAngle(), context));
        return nbt;
    }
}

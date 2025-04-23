package net.thewinnt.planimetry.definition.point.type;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.DynamicPlanimetry;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.definition.point.ValueContext;
import net.thewinnt.planimetry.definition.point.placement.LerpPlacement;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.util.Util;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.type.ConstantValue;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LerpPlacementType implements PointPlacementType<LerpPlacement> {
    public static final LerpPlacementType INSTANCE = new LerpPlacementType();

    private LerpPlacementType() {}

    @Override
    public @Nullable LerpPlacement createRandom(Random random, ValueContext context) {
        return null;
    }

    @Override
    public LerpPlacement convert(PointPlacement other, Drawing drawing) {
        if (drawing.getPoints().size() >= 3) {
            List<PointProvider> points = new ArrayList<>(drawing.getPoints());
            points.remove(other.getSource());
            Util.shuffle(points, DynamicPlanimetry.RANDOM);
            return new LerpPlacement(points.get(0), points.get(1), new ConstantValue(0.5));
        } else {
            return null;
        }
    }

    @Override
    public LerpPlacement fromNbt(CompoundTag nbt, LoadingContext context) {
        PointProvider a = context.resolveShape(nbt.getLong("a"));
        PointProvider b = context.resolveShape(nbt.getLong("b"));
        DynamicValue delta = DynamicValue.fromNbt(nbt.getCompoundTag("delta"), context);
        return new LerpPlacement(a, b, delta);
    }

    @Override
    public CompoundTag writeNbt(PointPlacement object, SavingContext context) {
        LerpPlacement placement = (LerpPlacement) object;
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("a", context.addShape(placement.getA()));
        nbt.putLong("b", context.addShape(placement.getB()));
        nbt.put("delta", DynamicValue.toNbt(placement.getDelta(), context));
        return nbt;
    }
}

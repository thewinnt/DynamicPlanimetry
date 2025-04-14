package net.thewinnt.planimetry.definition.point.type;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.definition.point.ValueContext;
import net.thewinnt.planimetry.definition.point.placement.CalculatablePlacement;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.type.ConstantValue;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class CalculatablePlacementType implements PointPlacementType<CalculatablePlacement> {
    public static final CalculatablePlacementType INSTANCE = new CalculatablePlacementType();

    private CalculatablePlacementType() {}

    @Override
    public @Nullable CalculatablePlacement createRandom(Random random, ValueContext context) {
        return null;
    }

    @Override
    public CalculatablePlacement convert(PointPlacement other, Drawing drawing) {
        Vec2 pos = other.getSource().getPosition();
        return new CalculatablePlacement(new ConstantValue(pos.x), new ConstantValue(pos.y));
    }

    @Override
    public CalculatablePlacement fromNbt(CompoundTag nbt, LoadingContext context) {
        DynamicValue x = DynamicValue.fromNbt(nbt.getCompoundTag("x"), context);
        DynamicValue y = DynamicValue.fromNbt(nbt.getCompoundTag("y"), context);
        return new CalculatablePlacement(x, y);
    }

    @Override
    public CompoundTag writeNbt(PointPlacement object, SavingContext context) {
        CalculatablePlacement placement = (CalculatablePlacement) object;
        CompoundTag nbt = new CompoundTag();
        nbt.put("x", DynamicValue.toNbt(placement.getX(), context));
        nbt.put("y", DynamicValue.toNbt(placement.getY(), context));
        return nbt;
    }
}

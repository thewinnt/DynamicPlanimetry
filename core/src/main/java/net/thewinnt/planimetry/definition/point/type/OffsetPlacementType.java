package net.thewinnt.planimetry.definition.point.type;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.definition.point.ValueContext;
import net.thewinnt.planimetry.definition.point.placement.OffsetPlacement;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.type.ConstantValue;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class OffsetPlacementType implements PointPlacementType<OffsetPlacement> {
    public static final OffsetPlacementType INSTANCE = new OffsetPlacementType();

    private OffsetPlacementType() {}

    @Override
    public @Nullable OffsetPlacement createRandom(Random random, ValueContext context) {
        return null;
    }

    @Override
    public OffsetPlacement convert(PointPlacement other, Drawing drawing) {
        PointProvider point = (PointProvider) drawing.getRandom(shape -> shape instanceof PointProvider p && p != other.getSource());
        if (point == null) return null;
        Vec2 pos = other.get().subtract(point.getPosition());
        return new OffsetPlacement(point, new ConstantValue(pos.x), new ConstantValue(pos.y));
    }

    @Override
    public OffsetPlacement fromNbt(CompoundTag nbt, LoadingContext context) {
        PointProvider point = context.resolveShape(nbt.getLong("point"));
        DynamicValue offsetX = DynamicValue.fromNbt(nbt.getCompoundTag("offset_x"), context);
        DynamicValue offsetY = DynamicValue.fromNbt(nbt.getCompoundTag("offset_y"), context);
        return new OffsetPlacement(point, offsetX, offsetY);
    }

    @Override
    public CompoundTag writeNbt(PointPlacement object, SavingContext context) {
        OffsetPlacement placement = ((OffsetPlacement) object);
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("point", context.addShape(placement.getPoint()));
        nbt.put("offset_x", DynamicValue.toNbt(placement.getOffsetX(), context));
        nbt.put("offset_y", DynamicValue.toNbt(placement.getOffsetY(), context));
        return nbt;
    }
}

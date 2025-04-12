package net.thewinnt.planimetry.definition.point.type;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.NbtUtil;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.definition.point.PointPlacement;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.definition.point.PointPlacementType;
import net.thewinnt.planimetry.definition.point.ValueContext;
import net.thewinnt.planimetry.definition.point.placement.StaticPlacement;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class StaticPlacementType implements PointPlacementType<StaticPlacement> {
    public static final StaticPlacementType INSTANCE = new StaticPlacementType();

    private StaticPlacementType() {}

    @Override
    public StaticPlacement fromNbt(CompoundTag nbt, LoadingContext context) {
        return new StaticPlacement(NbtUtil.readVec2(nbt, "position"));
    }

    @Override
    public CompoundTag writeNbt(PointPlacement value, SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        NbtUtil.writeVec2(nbt, value.get(), "position");
        return nbt;
    }

    @Override
    public StaticPlacement convert(PointPlacement other, Drawing drawing) {
        return new StaticPlacement(other.get());
    }

    @Override
    public @Nullable StaticPlacement createRandom(Random random, ValueContext context) {
        return new StaticPlacement(new Vec2(random.nextDouble() * 10, random.nextDouble() * 10));
    }
}

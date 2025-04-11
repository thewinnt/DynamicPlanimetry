package net.thewinnt.planimetry.point.type;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.NbtUtil;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.point.PointPlacementType;
import net.thewinnt.planimetry.point.ValueContext;
import net.thewinnt.planimetry.point.placement.StaticPlacement;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class StaticPlacementType implements PointPlacementType<StaticPlacement> {
    public static final StaticPlacementType INSTANCE = new StaticPlacementType();

    private StaticPlacementType() {}

    @Override
    public StaticPlacement fromNbt(CompoundTag nbt) {
        return new StaticPlacement(NbtUtil.readVec2(nbt, "position"));
    }

    @Override
    public CompoundTag toNbt(StaticPlacement value) {
        CompoundTag nbt = new CompoundTag();
        NbtUtil.writeVec2(nbt, value.get(null), "position");
        return nbt;
    }

    @Override
    public @Nullable StaticPlacement createRandom(Random random, ValueContext context) {
        return new StaticPlacement(new Vec2(random.nextDouble() * 10, random.nextDouble() * 10));
    }
}

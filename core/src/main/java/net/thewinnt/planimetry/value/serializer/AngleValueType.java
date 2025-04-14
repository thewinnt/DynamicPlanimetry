package net.thewinnt.planimetry.value.serializer;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.util.Util;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.AngleValue;

public class AngleValueType implements DynamicValueType<AngleValue> {
    public static final AngleValueType INSTANCE = new AngleValueType();

    private AngleValueType() {}

    @Override
    public AngleValue fromNbt(CompoundTag nbt, LoadingContext context) {
        return new AngleValue(nbt.getDouble("value"));
    }

    @Override
    public CompoundTag toNbt(AngleValue value, SavingContext context) {
        return Util.make(new CompoundTag(), tag -> tag.putDouble("value", value.value()));
    }

    @Override
    public AngleValue create(Drawing drawing) {
        return new AngleValue(0);
    }
}

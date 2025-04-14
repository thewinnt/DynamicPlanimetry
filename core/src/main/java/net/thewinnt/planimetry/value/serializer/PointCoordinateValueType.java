package net.thewinnt.planimetry.value.serializer;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.type.PointCoordinateValue;

public class PointCoordinateValueType implements DynamicValueType<PointCoordinateValue> {
    public static final PointCoordinateValueType INSTANCE = new PointCoordinateValueType();

    private PointCoordinateValueType() {}

    @Override
    public PointCoordinateValue fromNbt(CompoundTag nbt, LoadingContext context) {
        PointProvider point = context.resolveShape(nbt.getLong("point"));
        PointCoordinateValue.Coordinate coordinate = PointCoordinateValue.Coordinate.valueOf(nbt.getString("coordinate"));
        return new PointCoordinateValue(point, coordinate);
    }

    @Override
    public CompoundTag toNbt(PointCoordinateValue value, SavingContext context) {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("point", context.addShape(value.getPoint()));
        nbt.putString("coordinate", value.getCoordinate().name());
        return nbt;
    }

    @Override
    public PointCoordinateValue create(Drawing drawing) {
        return new PointCoordinateValue((PointProvider) drawing.getRandom(shape -> shape instanceof PointProvider), PointCoordinateValue.Coordinate.X);
    }
}

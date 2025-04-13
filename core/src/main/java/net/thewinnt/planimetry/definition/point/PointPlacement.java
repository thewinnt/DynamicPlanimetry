package net.thewinnt.planimetry.definition.point;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.data.registry.Holder;
import net.thewinnt.planimetry.data.registry.Identifier;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.TagKey;
import net.thewinnt.planimetry.math.Vec2;
import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertySupplier;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.List;

public abstract class PointPlacement implements PropertySupplier {
    protected PointProvider source;

    public abstract Vec2 get();
    public abstract void move(Vec2 delta);
    public abstract void move(double dx, double dy);
    public abstract boolean canMove();
    public abstract Collection<Property<?>> properties();
    public abstract PointPlacementType<?> type();
    public abstract List<Shape> dependencies();

    public final CompoundTag toNbt(SavingContext context) {
        CompoundTag nbt = this.type().writeNbt(this, context);
        nbt.putString("type", Registries.POINT_PLACEMENT_TYPE.getName(this.type()).toString());
        return nbt;
    }

    public final boolean is(PointPlacementType<?> type) {
        return this.type() == type;
    }

    public final boolean is(Holder<PointPlacementType<?>> type) {
        return this.type() == type.value();
    }

    public final boolean is(TagKey<PointPlacementType<?>> tag) {
        return Registries.POINT_PLACEMENT_TYPE.wrapAsHolder(this.type()).is(tag);
    }

    public PointProvider getSource() {
        return source;
    }

    @ApiStatus.Internal
    public void setSource(PointProvider source) {
        this.source = source;
    }

    public static PointPlacement fromNbt(CompoundTag nbt, LoadingContext context) {
        Identifier id = new Identifier(nbt.getString("type"));
        PointPlacementType<?> type = Registries.POINT_PLACEMENT_TYPE.get(id);
        return type.fromNbt(nbt, context);
    }
}

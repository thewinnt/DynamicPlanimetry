package net.thewinnt.planimetry.value.type;

import net.thewinnt.planimetry.math.MathHelper;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.serializer.PointDirectionValueType;
import net.thewinnt.planimetry.value.serializer.PointDistanceValueType;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class PointDirectionValue implements DynamicValue {
    private PointProvider a;
    private PointProvider b;

    public PointDirectionValue(PointProvider a, PointProvider b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public double get() {
        return MathHelper.angleTo(a.getPosition(), b.getPosition());
    }

    @Override
    public @NotNull DynamicValueType<? extends DynamicValue> type() {
        return PointDirectionValueType.INSTANCE;
    }

    @Override
    public Stream<PointProvider> dependencies() {
        return Stream.of(a, b);
    }

    @Override
    public DynamicValue clone() {
        try {
            return (DynamicValue) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("We're Cloneable, but don't support cloning", e);
        }
    }

    @Override
    public Collection<Property<?>> properties() {
        return List.of(
            PropertyHelper.swappablePoint(a, t -> a = t, List.of(b), false, "value.dynamic_planimetry.direction.a"),
            PropertyHelper.swappablePoint(b, t -> b = t, List.of(a), false, "value.dynamic_planimetry.direction.b")
        );
    }

    public PointProvider getA() {
        return a;
    }

    public PointProvider getB() {
        return b;
    }
}

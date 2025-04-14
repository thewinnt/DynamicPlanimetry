package net.thewinnt.planimetry.value.type;

import net.thewinnt.planimetry.Settings;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;
import net.thewinnt.planimetry.value.serializer.AngleValueType;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

public final class AngleValue implements DynamicValue {
    private final NumberProperty property;
    private double value;

    public AngleValue(double value) {
        // TODO implement restrictable number
        this.value = value;
        this.property = PropertyHelper.angle(this.translationKey("value"), value, t -> this.value = t);
    }

    @Override
    public double get() {
        return value;
    }

    public double value() {
        return value;
    }

    @Override
    public Collection<Property<?>> properties() {
        return List.of(property);
    }

    @Override
    public @NotNull DynamicValueType<?> type() {
        return AngleValueType.INSTANCE;
    }

    @Override
    public DynamicValue clone() {
        return new AngleValue(value);
    }

    @Override
    public Stream<PointProvider> dependencies() {
        return Stream.empty();
    }

    @Override
    public DynamicValue add(double delta) {
        value += delta;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AngleValue that = (AngleValue) o;
        return Double.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}

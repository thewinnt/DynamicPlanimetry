package net.thewinnt.planimetry.value.type;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.thewinnt.planimetry.point.ValueContext;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.types.NumberProperty;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;

public final class ConstantValue implements DynamicValue {
    private final NumberProperty property;
    private double value;

    public ConstantValue(double value) {
        // TODO implement others
        // TODO implement restrictable number
        this.value = value;
        this.property = new NumberProperty(Component.translatable(this.translationKey("value")), value);
        this.property.addValueChangeListener(t -> ConstantValue.this.value = t);
    }

    @Override
    public double get(ValueContext context) {
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
        return DynamicValueType.CONSTANT;
    }

    @Override
    public DynamicValue clone() {
        return new ConstantValue(value);
    }

    @Override
    public @Nullable Stream<PointProvider> dependencies() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantValue that = (ConstantValue) o;
        return Double.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}

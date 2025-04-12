package net.thewinnt.planimetry.value.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.Stream;

import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.text.Component;
import org.jetbrains.annotations.NotNull;

import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;

public record DoubleArgumentValue(DynamicValue arg1, DynamicValue arg2, DoubleBinaryOperator operation) implements DynamicValue {

    @Override
    public double get() {
        return operation.applyAsDouble(arg1.get(), arg2.get());
    }

    @Override
    public Collection<Property<?>> appendProperties(Collection<Property<?>> prefix) {
        prefix.add(new PropertyGroup(Component.translatable("dynamic_value.dynamic_planimetry.double_arg.arg1"), arg1.appendProperties(new ArrayList<>())));
        prefix.add(new PropertyGroup(Component.translatable("dynamic_value.dynamic_planimetry.double_arg.arg2"), arg2.appendProperties(new ArrayList<>())));
        return prefix;
    }

    @Override
    @NotNull
    public DynamicValueType<? extends DynamicValue> type() {
        return DynamicValueType.DOUBLE_ARGUMENT.get(operation);
    }

    @Override
    public Stream<PointProvider> dependencies() {
        return Stream.concat(arg1.dependencies(), arg2.dependencies());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleArgumentValue that = (DoubleArgumentValue) o;
        return operation == that.operation && Objects.equals(arg1, that.arg1) && Objects.equals(arg2, that.arg2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, arg1, arg2);
    }

    @Override
    public DynamicValue clone() {
        return new DoubleArgumentValue(arg1.clone(), arg2.clone(), operation);
    }
}

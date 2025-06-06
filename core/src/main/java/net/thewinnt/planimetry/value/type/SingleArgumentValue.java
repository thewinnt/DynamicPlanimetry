package net.thewinnt.planimetry.value.type;

import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

public final class SingleArgumentValue implements DynamicValue {
    private final DoubleUnaryOperator operation;
    private DynamicValue input;

    public SingleArgumentValue(DynamicValue input, DoubleUnaryOperator operation) {
        this.input = input;
        this.operation = operation;
    }

    @Override
    public double get() {
        return operation.applyAsDouble(input().get());
    }

    @Override
    public Collection<Property<?>> properties() {
        return List.of(PropertyHelper.dynamicValue(input, t -> input = t, "value.dynamic_planimetry.single_arg.arg"));
    }

    @Override
    public @NotNull DynamicValueType<? extends DynamicValue> type() {
        return DynamicValueType.SINGLE_ARGUMENT.get(operation);
    }

    @Override
    public @Nullable Stream<PointProvider> dependencies() {
        return Stream.empty();
    }

    @Override
    public DynamicValue clone() {
        return new SingleArgumentValue(input.clone(), operation);
    }

    public DynamicValue input() {
        return input;
    }

    public DoubleUnaryOperator operation() {
        return operation;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SingleArgumentValue) obj;
        return Objects.equals(this.input, that.input) &&
            Objects.equals(this.operation, that.operation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, operation);
    }

    @Override
    public String toString() {
        return "SingleArgumentValue[" +
            "input=" + input + ", " +
            "operation=" + operation + ']';
    }

}

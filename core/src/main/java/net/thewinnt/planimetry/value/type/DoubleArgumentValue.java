package net.thewinnt.planimetry.value.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.Stream;

import net.thewinnt.planimetry.ui.properties.PropertyHelper;
import net.thewinnt.planimetry.ui.properties.types.PropertyGroup;
import net.thewinnt.planimetry.ui.text.Component;
import org.jetbrains.annotations.NotNull;

import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;

public final class DoubleArgumentValue implements DynamicValue {
    private DynamicValue arg1;
    private DynamicValue arg2;
    private final DoubleBinaryOperator operation;

    public DoubleArgumentValue(DynamicValue arg1, DynamicValue arg2, DoubleBinaryOperator operation) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operation = operation;
    }

    @Override
    public double get() {
        return operation.applyAsDouble(arg1.get(), arg2.get());
    }

    @Override
    public Collection<Property<?>> properties() {
        return List.of(
            PropertyHelper.dynamicValue(arg1, t -> arg1 = t, "value.dynamic_planimetry.double_arg.arg1"),
            PropertyHelper.dynamicValue(arg2, t -> arg2 = t, "value.dynamic_planimetry.double_arg.arg2")
        );
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

    public DynamicValue arg1() {
        return arg1;
    }

    public DynamicValue arg2() {
        return arg2;
    }

    @Override
    public DynamicValue add(double delta) {
        if (this.type() == DynamicValueType.ADD) {
            if (arg2 instanceof ConstantValue) {
                arg2.add(delta);
                return this;
            } else if (arg1 instanceof ConstantValue) {
                arg1.add(delta);
                return this;
            } else {
                return DynamicValue.super.add(delta);
            }
        } else if (this.type() == DynamicValueType.SUBTRACT) {
            if (arg2 instanceof ConstantValue) {
                arg2.add(-delta);
                return this;
            } else if (arg1 instanceof ConstantValue) {
                arg1.add(delta);
                return this;
            } else {
                return DynamicValue.super.add(delta);
            }
        } else {
            return DynamicValue.super.add(delta);
        }
    }

    @Override
    public String toString() {
        return "DoubleArgumentValue[" +
                   "arg1=" + arg1 + ", " +
                   "arg2=" + arg2 + ", " +
                   "operation=" + operation + ']';
    }

}

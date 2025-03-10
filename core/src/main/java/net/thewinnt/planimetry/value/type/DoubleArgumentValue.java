package net.thewinnt.planimetry.value.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import net.thewinnt.planimetry.point.ValueContext;
import net.thewinnt.planimetry.shapes.point.PointProvider;
import net.thewinnt.planimetry.ui.properties.Property;
import net.thewinnt.planimetry.value.DynamicValue;
import net.thewinnt.planimetry.value.DynamicValueType;

public class DoubleArgumentValue implements DynamicValue {
    // TODO figure out properties
    private final Operation operation;
    private DynamicValue arg1;
    private DynamicValue arg2;

    public DoubleArgumentValue(DynamicValue arg1, DynamicValue arg2, Operation operation) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operation = operation;
    }

    @Override
    public double get(ValueContext context) {
        return operation.apply(arg1.get(context), arg2.get(context));
    }

    @Override
    public Collection<Property<?>> properties() {
        ArrayList<Property<?>> properties = new ArrayList<>();
        properties.addAll(arg1.properties());
        properties.addAll(arg2.properties());
        return properties;
    }

    @Override
    @NotNull
    public DynamicValueType<? extends DynamicValue> type() {
        // TODO Auto-generated method stub
        return null;
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

    public enum Operation {
        ADD(Double::sum),
        SUBTRACT((a, b) -> a - b),
        MUL((a, b) -> a * b),
        DIV((a, b) -> a / b),
        POW(Math::pow),
        MAX(Math::max),
        MIN(Math::min),
        ATAN2(Math::atan2);

        private final DoubleBinaryOperator function;

        Operation(DoubleBinaryOperator function) {
            this.function = function;
        }

        public double apply(double a, double b) {
            return function.applyAsDouble(a, b);
        }
    }
}

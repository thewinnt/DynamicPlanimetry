package net.thewinnt.planimetry.value;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.Drawing;
import net.thewinnt.planimetry.data.LoadingContext;
import net.thewinnt.planimetry.data.SavingContext;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.Registry;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.serializer.ConstantValueType;
import net.thewinnt.planimetry.value.serializer.DoubleArgumentValueType;
import net.thewinnt.planimetry.value.serializer.PointCoordinateValueType;
import net.thewinnt.planimetry.value.serializer.PointDistanceValueType;
import net.thewinnt.planimetry.value.serializer.SingleArgumentValueType;
import net.thewinnt.planimetry.value.type.ConstantValue;
import net.thewinnt.planimetry.value.type.PointCoordinateValue;
import net.thewinnt.planimetry.value.type.PointDistanceValue;

public interface DynamicValueType<T extends DynamicValue> {
    Map<DoubleUnaryOperator, SingleArgumentValueType> SINGLE_ARGUMENT = new HashMap<>();
    Map<DoubleBinaryOperator, DoubleArgumentValueType> DOUBLE_ARGUMENT = new HashMap<>();

    DynamicValueType<ConstantValue> CONSTANT = register("constant", ConstantValueType.INSTANCE);
    SingleArgumentValueType SQUARE = registerSingleArg("square", t -> t * t);
    SingleArgumentValueType CUBE = registerSingleArg("cube", t -> t * t * t);
    SingleArgumentValueType SQRT = registerSingleArg("sqrt", Math::sqrt);
    SingleArgumentValueType CBRT = registerSingleArg("cbrt", Math::cbrt);
    SingleArgumentValueType ABS = registerSingleArg("abs", Math::abs);
    SingleArgumentValueType SIN = registerSingleArg("sin", Math::sin);
    SingleArgumentValueType COS = registerSingleArg("cos", Math::cos);
    SingleArgumentValueType TAN = registerSingleArg("tan", Math::tan);
    SingleArgumentValueType TANH = registerSingleArg("tanh", Math::tanh);
    SingleArgumentValueType ASIN = registerSingleArg("asin", Math::asin);
    SingleArgumentValueType ACOS = registerSingleArg("acos", Math::acos);
    SingleArgumentValueType ATAN = registerSingleArg("atan", Math::atan);
    DoubleArgumentValueType ADD = registerDoubleArg("add", Double::sum);
    DoubleArgumentValueType SUBTRACT = registerDoubleArg("subtract", (a, b) -> a - b);
    DoubleArgumentValueType MUL = registerDoubleArg("mul", (a, b) -> a * b);
    DoubleArgumentValueType DIV = registerDoubleArg("div", (a, b)-> a / b);
    DoubleArgumentValueType MOD = registerDoubleArg("mod", (a, b)-> a % b);
    DoubleArgumentValueType POW = registerDoubleArg("pow", Math::pow);
    DoubleArgumentValueType MIN = registerDoubleArg("min", Math::min);
    DoubleArgumentValueType MAX = registerDoubleArg("max", Math::max);
    DoubleArgumentValueType ATAN2 = registerDoubleArg("atan2", Math::atan2);
    DynamicValueType<PointCoordinateValue> COORDINATE = register("coordinate", PointCoordinateValueType.INSTANCE);
    DynamicValueType<PointDistanceValue> DISTANCE = register("distance", PointDistanceValueType.INSTANCE);

    T fromNbt(CompoundTag nbt, LoadingContext context);
    CompoundTag toNbt(T value, SavingContext context);
    T create(Drawing drawing);

    @SuppressWarnings("unchecked")
    default CompoundTag toNbtUnchecked(DynamicValue value, SavingContext context) {
        return toNbt((T) value, context);
    }

    default Component name() {
        return Component.translatable(Registries.DYNAMIC_VALUE_TYPE.getName(this).toLanguageKey("value_type"));
    }

    static <T extends DynamicValue> DynamicValueType<T> register(String id, DynamicValueType<T> type) {
        return Registry.register(Registries.DYNAMIC_VALUE_TYPE, type, id);
    }

    static SingleArgumentValueType registerSingleArg(String id, DoubleUnaryOperator operation) {
        SingleArgumentValueType type = new SingleArgumentValueType(operation);
        SINGLE_ARGUMENT.put(operation, type);
        return Registry.register(Registries.DYNAMIC_VALUE_TYPE, type, id);
    }

    static DoubleArgumentValueType registerDoubleArg(String id, DoubleBinaryOperator operation) {
        DoubleArgumentValueType type = new DoubleArgumentValueType(operation);
        DOUBLE_ARGUMENT.put(operation, type);
        return Registry.register(Registries.DYNAMIC_VALUE_TYPE, type, id);
    }

    static void init() {}
}

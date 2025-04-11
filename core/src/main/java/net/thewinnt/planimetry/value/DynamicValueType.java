package net.thewinnt.planimetry.value;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import net.querz.nbt.tag.CompoundTag;
import net.thewinnt.planimetry.data.registry.Registries;
import net.thewinnt.planimetry.data.registry.Registry;
import net.thewinnt.planimetry.point.ValueContext;
import net.thewinnt.planimetry.ui.text.Component;
import net.thewinnt.planimetry.value.serializer.ConstantValueType;
import net.thewinnt.planimetry.value.serializer.DoubleArgumentValueType;
import net.thewinnt.planimetry.value.serializer.SingleArgumentValueType;
import net.thewinnt.planimetry.value.type.ConstantValue;

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
    DoubleArgumentValueType MIN = registerDoubleArg("pow", Math::min);
    DoubleArgumentValueType MAX = registerDoubleArg("pow", Math::max);
    DoubleArgumentValueType ATAN2 = registerDoubleArg("pow", Math::atan2);

    T fromNbt(CompoundTag tag);
    CompoundTag toNbt(T value);

    @SuppressWarnings("unchecked")
    default CompoundTag toNbtUnchecked(DynamicValue value) {
        return toNbt((T) value);
    }

    /**
     * Returns a random value in some reasonable range, if the implementer felt like it.
     * @param outMin the preferred minimum value
     * @param outMax the preferred maximum value
     * @see net.thewinnt.planimetry.screen.MainMenuScreen
     */
    default Optional<DynamicValue> createRandom(Random random, double outMin, double outMax, ValueContext context) {
        return Optional.empty();
    }

    default Component name() {
        return Component.translatable(Registries.DYNAMIC_VALUE_TYPES.getName(this).toLanguageKey("value_type"));
    }

    static <T extends DynamicValue> DynamicValueType<T> register(String id, DynamicValueType<T> type) {
        return Registry.register(Registries.DYNAMIC_VALUE_TYPES, type, id);
    }

    static SingleArgumentValueType registerSingleArg(String id, DoubleUnaryOperator operation) {
        return Registry.register(Registries.DYNAMIC_VALUE_TYPES, new SingleArgumentValueType(operation), id);
    }

    static DoubleArgumentValueType registerDoubleArg(String id, DoubleBinaryOperator operation) {
        return Registry.register(Registries.DYNAMIC_VALUE_TYPES, new DoubleArgumentValueType(operation), id);
    }

    static void init() {}
}

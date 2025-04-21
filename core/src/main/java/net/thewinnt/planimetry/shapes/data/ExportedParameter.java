package net.thewinnt.planimetry.shapes.data;

import net.thewinnt.planimetry.shapes.Shape;
import net.thewinnt.planimetry.value.DynamicValue;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ExportedParameter<T extends Shape> {
    private final T shape;
    private final Supplier<DynamicValue> getter;
    private final Consumer<DynamicValue> setter;
    private boolean locked;

    public ExportedParameter(T shape, Supplier<DynamicValue> getter, Consumer<DynamicValue> setter, boolean locked) {
        this.shape = shape;
        this.getter = getter;
        this.setter = setter;
        this.locked = locked;
    }

    public double get() {
        return getter.get().get();
    }

    public DynamicValue value() {
        return getter.get();
    }

    public void set(T shape, DynamicValue value) {
        if (!locked) {
            setter.accept(value);
        }
    }

    /** When true, the parameter is read-only, and {@link #set(Shape, DynamicValue)} calls will be ignored */
    public boolean isLocked() {
        return locked;
    }

    public void unlock() {
        locked = false;

    }


}

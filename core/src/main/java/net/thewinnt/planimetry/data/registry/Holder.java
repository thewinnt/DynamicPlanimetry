package net.thewinnt.planimetry.data.registry;

public interface Holder<T> {
    Identifier id();
    T value();

    record Direct<T>(Identifier id, T value) implements Holder<T> {}
}

package net.thewinnt.planimetry.data.registry;

public interface MutableRegistry<T> extends Registry<T> {
    T register(Identifier id, T element);
    void freeze();
    boolean isFrozen();
}

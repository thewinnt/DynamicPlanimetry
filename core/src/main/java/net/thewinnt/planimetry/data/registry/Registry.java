package net.thewinnt.planimetry.data.registry;

import java.util.stream.Stream;

public interface Registry<T> {
    T byId(int id);
    T byName(Identifier id);
    Identifier getName(T element);
    int getId(T element);
    Stream<Holder<T>> stream();

    public static <T> T register(Registry<T> registry, T element, Identifier id) {
        return ((MutableRegistry<T>) registry).register(id, element);
    }

    public static <T> T register(Registry<T> registry, T element, String id) {
        return register(registry, element, new Identifier(id));
    }
}

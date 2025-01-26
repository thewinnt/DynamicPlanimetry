package net.thewinnt.planimetry.data.registry;

import java.util.Iterator;
import java.util.stream.Stream;

public interface Registry<T> {
    Identifier id();
    T byId(int id);
    T byName(Identifier id);
    Identifier getName(T element);
    int getId(T element);
    Stream<T> stream();
    Iterable<T> elements();
    Iterable<Identifier> ids();

    public static <T> T register(Registry<? super T> registry, T element, Identifier id) {
        return ((MutableRegistry<T>) registry).register(id, element);
    }

    public static <T> T register(Registry<? super T> registry, T element, String id) {
        return register(registry, element, new Identifier(id));
    }
}

package net.thewinnt.planimetry.data.registry;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface Registry<T> {
    Identifier id();
    T get(int id);
    T get(Identifier id);
    Identifier getName(T element);
    int getId(T element);
    boolean contains(Identifier id);
    Stream<T> stream();
    Stream<Holder<T>> holders();
    Iterable<T> elements();
    Iterable<Identifier> ids();
    Holder<T> wrapAsHolder(T element);
    Holder<T> getHolder(Identifier id);
    Holder<T> getHolder(int id);
    List<T> getElementsOfTag(TagKey<?> tag);
    List<Identifier> getTagContents(TagKey<?> tag);
    Set<TagKey<T>> getAllTags();

    @SuppressWarnings("unchecked")
    public static <T> T register(Registry<? super T> registry, T element, Identifier id) {
        return ((MutableRegistry<T>) registry).register(id, element);
    }

    public static <T> T register(Registry<? super T> registry, T element, String id) {
        return register(registry, element, new Identifier(id));
    }
}

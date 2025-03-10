package net.thewinnt.planimetry.data.registry;

import java.util.HashSet;
import java.util.Set;

public class Holder<T> {
    private final Set<TagKey<?>> tags = new HashSet<>();
    private T value;

    Holder() {
        this.value = null;
    }

    Holder(T value) {
        this.value = value;
    }

    public boolean isBound() {
        return this.value != null;
    }

    public T value() {
        if (this.value == null) {
            throw new IllegalStateException("Trying to access unbound holder");
        }
        return value;
    }

    public boolean is(Holder<? super T> other) {
        return this.value == other.value;
    }

    public boolean is(T value) {
        return this.value == value;
    }

    public boolean is(TagKey<? super T> tag) {
        return this.tags.contains(tag);
    }

    void clearTags() {
        this.tags.clear();
    }

    void addTag(TagKey<?> tag) {
        this.tags.add(tag);
    }
}

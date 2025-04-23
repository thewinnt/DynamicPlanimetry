package net.thewinnt.planimetry.data.registry;

import java.util.List;
import java.util.Map;

public interface MutableRegistry<T> extends Registry<T> {
    T register(Identifier id, T element);
    void freeze();
    boolean isFrozen();
    void reloadTags(Map<TagKey<?>, List<Identifier>> tags);
    void appendTag(TagKey<T> tag, List<Identifier> elements);
}

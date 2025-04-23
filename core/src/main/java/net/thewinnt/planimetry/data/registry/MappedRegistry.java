package net.thewinnt.planimetry.data.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class MappedRegistry<T> implements MutableRegistry<T> {
    // TODO minecraft-like registries
    // TODO plugin system
    public final Identifier id;
    protected final Map<Identifier, Holder<T>> elementByName = new HashMap<>();
    protected final Map<T, Identifier> names = new HashMap<>();
    protected final Int2ObjectMap<Holder<T>> elementById = new Int2ObjectOpenHashMap<>();
    protected final Object2IntMap<T> ids = new Object2IntOpenHashMap<>();
    protected final Map<T, Holder<T>> holders = new HashMap<>();
    protected final Map<TagKey<T>, List<T>> tags = new HashMap<>();
    protected boolean frozen;
    private int idMapper = 0;

    public MappedRegistry(Identifier id) {
        this.id = id;
    }

    @Override
    public T register(Identifier id, T element) {
        validateWrite();
        Holder<T> holder = new Holder<>(element);
        this.elementByName.put(id, holder);
        this.elementById.put(idMapper, holder);
        this.names.put(element, id);
        this.ids.put(element, idMapper);
        this.holders.put(element, holder);
        idMapper++;
        return element;
    }

    @Override
    public void freeze() {
        this.frozen = true;
    }

    @Override
    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public T get(int id) {
        return this.elementById.get(id).value();
    }

    @Override
    public T get(Identifier id) {
        return this.elementByName.get(id).value();
    }

    @Override
    public Identifier getName(T element) {
        return this.names.get(element);
    }

    @Override
    public int getId(T element) {
        return this.ids.getOrDefault(element, -1);
    }

    private void validateWrite() {
        if (frozen) throw new IllegalStateException("Registry already frozen!");
    }

    @Override
    public boolean contains(Identifier id) {
        return this.elementByName.containsKey(id);
    }

    @Override
    public Stream<T> stream() {
        return elementById.values().stream().map(Holder::value);
    }

    @Override
    public Iterable<T> elements() {
        return this.holders.keySet();
    }

    @Override
    public Iterable<Identifier> ids() {
        return this.elementByName.keySet();
    }

    @Override
    public Holder<T> getHolder(int id) {
        return this.elementById.get(id);
    }

    @Override
    public Holder<T> getHolder(Identifier id) {
        return this.elementByName.get(id);
    }

    @Override
    public Stream<Holder<T>> holders() {
        return this.holders.values().stream();
    }

    @Override
    public Holder<T> wrapAsHolder(T element) {
        return this.holders.getOrDefault(element, new Holder<>(element));
    }

    @Override
    public List<T> getElementsOfTag(TagKey<?> tag) {
        return this.tags.get(tag);
    }

    @Override
    public List<Identifier> getTagContents(TagKey<?> tag) {
        return this.tags.get(tag).stream().map(this::getName).toList();
    }

    @Override
    public Set<TagKey<T>> getAllTags() {
        return this.tags.keySet();
    }

    @Override
    public Identifier id() {
        return id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void reloadTags(Map<TagKey<?>, List<Identifier>> tags) {
        this.tags.clear();
        for (Holder<T> i : this.holders.values()) {
            i.clearTags();
        }
        for (var i : tags.entrySet()) {
            if (!i.getKey().registry().equals(this.id)) continue;
            List<T> elements = new ArrayList<>();
            for (Identifier id : i.getValue()) {
                this.getHolder(id).addTag(i.getKey());
                elements.add(this.get(id));
            }
            this.tags.put((TagKey<T>) i.getKey(), elements);
        }
    }

    @Override
    public void appendTag(TagKey<T> tag, List<Identifier> elements) {
        if (!tag.registry().equals(this.id)) return;
        List<T> objects = this.tags.computeIfAbsent(tag, r -> new ArrayList<>());
        for (Identifier id : elements) {
            this.getHolder(id).addTag(tag);
            objects.add(this.get(id));
        }
        this.tags.put(tag, objects);
    }
}

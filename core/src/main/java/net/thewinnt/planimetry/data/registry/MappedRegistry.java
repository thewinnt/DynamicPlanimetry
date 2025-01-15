package net.thewinnt.planimetry.data.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class MappedRegistry<T> implements MutableRegistry<T> {
    // TODO minecraft-like registries
    // TODO plugin system
    protected final Map<Identifier, T> elementByName = new HashMap<>();
    protected final Map<T, Identifier> names = new HashMap<>();
    protected final Int2ObjectMap<T> elementById = new Int2ObjectOpenHashMap<>();
    protected final Object2IntMap<T> ids = new Object2IntOpenHashMap<>();
    protected boolean frozen;
    private int idMapper = 0;

    public MappedRegistry() {}

    @Override
    public T register(Identifier id, T element) {
        validateWrite();
        this.elementByName.put(id, element);
        this.elementById.put(idMapper, element);
        this.names.put(element, id);
        this.ids.put(element, idMapper);
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
    public T byId(int id) {
        return this.elementById.get(id);
    }

    @Override
    public T byName(Identifier id) {
        return this.elementByName.get(id);
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
    public Stream<T> stream() {
        return elementById.values().stream();
    }
}

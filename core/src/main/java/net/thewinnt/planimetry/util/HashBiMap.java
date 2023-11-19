package net.thewinnt.planimetry.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A lazily made bidirectional map. Use {@link #getKey(Object)} to easily get a key for the specified value.
 */
public class HashBiMap<K, V> implements Map<K, V> {
    private final HashMap<K, V> normal;
    private final HashMap<V, K> reverse;

    public HashBiMap() {
        normal = new HashMap<>();
        reverse = new HashMap<>();
    }

    public HashBiMap(int initCapacity) {
        normal = new HashMap<>(initCapacity);
        reverse = new HashMap<>(initCapacity);
    }

    public HashBiMap(int initCapacity, float loadFactor) {
        normal = new HashMap<>(initCapacity, loadFactor);
        reverse = new HashMap<>(initCapacity, loadFactor);
    }

    public HashBiMap(Map<? extends K, ? extends V> from) {
        normal = new HashMap<>(from);
        reverse = new HashMap<>();
        for (var i : from.entrySet()) {
            reverse.put(i.getValue(), i.getKey());
        }
    }

    @Override
    public void clear() {
        normal.clear();
        reverse.clear();
    }

    @Override
    public Set<K> keySet() {
        return normal.keySet();
    }

    @Override
    public Collection<V> values() {
        return normal.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return normal.entrySet();
    }

    @Override
    public boolean containsKey(Object key) {
        return normal.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return normal.containsValue(value);
    }

    @Override
    public V put(K key, V value) {
        reverse.put(value, key);
        return normal.put(key, value);
    }

    @Override
    public V get(Object key) {
        return normal.get(key);
    }

    public K getKey(V value) {
        return reverse.get(value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (var i : m.entrySet()) {
            this.put(i.getKey(), i.getValue());
        }
    }

    @Override
    public boolean isEmpty() {
        return normal.isEmpty();
    }

    @Override
    public V remove(Object key) {
        V output = normal.remove(key);
        reverse.remove(output);
        return output;
    }

    @Override
    public int size() {
        return normal.size();
    }
}

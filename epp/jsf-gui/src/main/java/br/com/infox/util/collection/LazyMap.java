package br.com.infox.util.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.com.infox.constants.WarningConstants;

/**
 * @author gabriel
 *
 * @param <K>
 * @param <V>
 */
public class LazyMap<K, V> implements Map<K, V> {

    private Map<K, V> wrapped;
    private Factory<K, V> factory;

    public LazyMap(Map<K, V> wrapped, Factory<K, V> factory) {
        this.wrapped = wrapped;
        this.factory = factory;
    }

    public LazyMap(Factory<K, V> factory) {
        this.wrapped = new HashMap<>();
        this.factory = factory;
    }

    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return wrapped.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return wrapped.containsValue(value);
    }

    @Override
    @SuppressWarnings(WarningConstants.UNCHECKED)
    public V get(Object key) {
        if (containsKey(key)) {
            return wrapped.get(key);
        }
        V value = factory.create((K) key);
        put((K) key, value);
        return value;
    }

    @Override
    public V put(K key, V value) {
        return wrapped.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return wrapped.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        wrapped.putAll(m);
    }

    @Override
    public void clear() {
        wrapped.clear();
    }

    @Override
    public Set<K> keySet() {
        return wrapped.keySet();
    }

    @Override
    public Collection<V> values() {
        return wrapped.values();
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return wrapped.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return wrapped.equals(o);
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }
}

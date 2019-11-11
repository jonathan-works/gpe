package br.com.infox.util.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DefaultMap<K> implements Map<K, K> {

    private Map<K, K> wrapped;
    
    public DefaultMap(Map<K, K> wrapped) {
        this.wrapped = wrapped;
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

    @SuppressWarnings("unchecked")
    @Override
    public K get(Object key) {
        if (wrapped.containsKey(key)) {
            return wrapped.get(key);
        }
        return (K) key;
    }

    @Override
    public K put(K key, K value) {
        return wrapped.put(key, value);
    }

    @Override
    public K remove(Object key) {
        return wrapped.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends K> m) {
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
    public Collection<K> values() {
        return wrapped.values();
    }

    @Override
    public Set<java.util.Map.Entry<K, K>> entrySet() {
        return wrapped.entrySet();
    }
}

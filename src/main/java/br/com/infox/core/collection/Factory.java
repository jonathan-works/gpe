package br.com.infox.core.collection;

public interface Factory<K, V> {
    V create(K key);
}

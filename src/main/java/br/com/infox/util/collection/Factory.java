package br.com.infox.util.collection;

public interface Factory<K, V> {
    V create(K key);
}

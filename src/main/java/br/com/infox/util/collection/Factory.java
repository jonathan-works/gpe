package br.com.infox.util.collection;

/**
 * @author gabriel
 *
 * @param <K>
 * @param <V>
 */
public interface Factory<K, V> {
    V create(K key);
}

package com.valdisdot.util.commons;

import java.util.Map;
import java.util.Objects;

/**
 * A simple implementation of the {@link Map.Entry} interface representing a key-value pair.
 * This class is immutable for the key but allows mutable values, enabling its use in various
 * data structures where a key-value pair is needed.
 *
 * @param <V> the type of the value stored in this tuple
 */
public class Tuple<V> implements Map.Entry<String, V> {
    private final String key;
    private V value = null;

    /**
     * Constructs a {@code Tuple} with the specified key and value.
     *
     * @param key   the key of the tuple
     * @param value the value of the tuple
     */
    public Tuple(String key, V value) {
        this.key = Objects.requireNonNull(key, "Tuple key is null");
        this.value = Objects.requireNonNull(value, "Tuple value is null");
    }

    /**
     * Returns the key corresponding to this entry.
     *
     * @return the key of the tuple
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * Returns the value corresponding to this entry.
     *
     * @return the value of the tuple
     */
    @Override
    public V getValue() {
        return value;
    }

    /**
     * Replaces the value corresponding to this entry with the specified value.
     *
     * @param value the new value to be stored in the tuple
     * @return the previous value associated with the key
     */
    @Override
    public V setValue(V value) {
        try {
            return this.value;
        } finally {
            this.value = Objects.requireNonNull(value, "Tuple value is null");
        }
    }
}

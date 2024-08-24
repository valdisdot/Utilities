package com.valdisdot.util.vaadin.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.valdisdot.util.vaadin.helper.PropertiesRegister;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Objects;

/**
 * A Vaadin component that supports container key registration and management.
 * This class provides methods for registering container keys and retrieving key information and forces the user to implement the component initialization logic.
 */
public abstract class Container extends Composite<Component> {
    private PropertiesRegister propertiesRegister;

    /**
     * Constructs a {@code Container} with the specified {@link PropertiesRegister}.
     * Basically, the constructor is used for dependency injection.
     *
     * @param propertiesRegister the {@link PropertiesRegister} to be used
     */
    @Autowired
    public Container(PropertiesRegister propertiesRegister) {
        setPropertiesRegister(propertiesRegister);
    }

    /**
     * Default constructor for {@code Container} for inheritance.
     */
    protected Container() {
    }

    /**
     * Retrieves the key associated with this container.
     * The key is fetched from the {@link PropertiesRegister}.
     *
     * @return the key of this container
     * @throws NullPointerException if {@link PropertiesRegister} is {@code null} or there is no property.
     */
    public String getKey() {
        return Objects.requireNonNull(propertiesRegister, "propertiesRegister is null").getPropertyKeyFor(this.getClass()).orElseThrow(NullPointerException::new);
    }

    /**
     * Retrieves the key associated with this container, including optional suffixes.
     * The key is fetched from the {@link PropertiesRegister}.
     *
     * @param suffixes optional suffixes to append to the key
     * @return the key of this container with the specified suffixes
     * @throws NullPointerException if {@link PropertiesRegister} is {@code null} or there is no property.
     */
    public String getKey(String... suffixes) {
        return Objects.requireNonNull(propertiesRegister, "propertiesRegister is null").getPropertyKeyFor(this.getClass(), suffixes).orElseThrow(NullPointerException::new);
    }

    /**
     * Retrieves the {@link PropertiesRegister} used by this container.
     * This method is for use by subclasses.
     *
     * @return the {@link PropertiesRegister} used by this container
     */
    protected PropertiesRegister getPropertiesRegister() {
        return propertiesRegister;
    }

    /**
     * Sets the {@link PropertiesRegister} used for container key management.
     * This method is used for Spring dependency injection and inheritance (post-construct DI).
     *
     * @param propertiesRegister the {@link PropertiesRegister} to set
     */
    @Autowired
    protected void setPropertiesRegister(PropertiesRegister propertiesRegister) {
        this.propertiesRegister = Objects.requireNonNull(propertiesRegister, "PropertiesRegister is null");
        this.propertiesRegister.registerComponent(this.getClass());
    }

    /**
     * Optionally overridden by inheritors to provide a CSS structure for the container.
     * <p>
     * This method can be used to return a CSS string that represents the visual styling of the container.
     * By default, it returns an empty string, which means no specific CSS is applied.
     * </p>
     *
     * @return a {@code String} representing the CSS structure of the container
     */
    protected String getCssString() {
        return "";
    }

    /**
     * Initializes the content of this container by creating and configuring the {@link Component}.
     * This method is abstract, requiring inheriting classes to provide the implementation.
     *
     * @return the configured {@link Component} to be used as the content of this container
     */
    @Override
    protected abstract Component initContent();

    /**
     * A simple implementation of the {@link Map.Entry} interface representing a key-value pair.
     * This class is immutable for the key but allows mutable values, enabling its use in various
     * data structures where a key-value pair is needed.
     *
     * @param <V> the type of the value stored in this tuple
     */
    public static class Tuple<V> implements Map.Entry<String, V> {
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
}

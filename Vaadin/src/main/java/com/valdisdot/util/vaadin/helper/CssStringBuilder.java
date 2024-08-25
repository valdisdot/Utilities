package com.valdisdot.util.vaadin.helper;

import java.util.*;
import java.util.stream.Stream;

/**
 * A builder class for creating CSS structures programmatically.
 * The {@code CssBuilder} class allows the definition of CSS classes, their properties,
 * and hierarchical relationships between them.
 *
 * <p>The class provides methods for adding CSS properties, creating child classes,
 * and generating a string representation of the entire CSS structure.</p>
 */
public class CssStringBuilder implements Comparable<CssStringBuilder> {
    private String className;
    private TreeMap<String, String> properties = new TreeMap<>();
    private CssStringBuilder parent;
    private TreeSet<CssStringBuilder> children = new TreeSet<>();

    /**
     * Constructs a {@code CssBuilder} with the specified class name.
     * The class name is sanitized by removing invalid characters.
     *
     * @param className the name of the CSS class
     */
    public CssStringBuilder(String className) {
        this.className = className.replaceAll("(^\\\\d|[^a-zA-Z0-9_-]|^-\\\\d|^-|^-.*-$|(?<=\\\\s)-(?=\\\\s))", "");
    }

    /**
     * Adds a CSS property to this class.
     * The property is added only if both the key and value are non-null and non-blank.
     *
     * @param key the CSS property name
     * @param value the CSS property value
     * @return this {@code CssBuilder} instance for method chaining
     */
    public CssStringBuilder put(String key, String value) {
        if (key != null && value != null && !key.isBlank() && !value.isBlank()) properties.put(key, value);
        return this;
    }

    /**
     * Adds multiple CSS properties to this class from the provided map.
     * Each key-value pair in the map represents a CSS property name and its corresponding value.
     * The properties are added only if the map is non-null. The property is added only if both the key and value are non-null and non-blank.
     *
     * @param values a map containing CSS property names and their corresponding values
     * @return this {@code CssBuilder} instance for method chaining
     */
    public CssStringBuilder put(Map<String, String> values) {
        if (values != null) values.forEach(this::put);
        return this;
    }


    /**
     * Creates a child CSS class under the current class.
     * The child class name must be non-null and non-blank.
     *
     * @param childrenClassName the name of the child CSS class
     * @return the newly created {@code CssBuilder} instance representing the child class
     * @throws IllegalArgumentException if the {@code childrenClassName} is null or blank
     */
    public CssStringBuilder conceive(String childrenClassName) {
        if (childrenClassName == null || childrenClassName.isBlank())
            throw new IllegalArgumentException("Children class is empty");
        CssStringBuilder child = new CssStringBuilder(childrenClassName);
        children.add(child);
        child.parent = this;
        return child;
    }

    /**
     * Returns the parent CSS class of the current class.
     *
     * @return the parent {@code CssBuilder} instance
     * @throws NullPointerException if the current class is the root and has no parent
     */
    public CssStringBuilder getParent() {
        return Objects.requireNonNull(parent, "The element is root itself");
    }

    /**
     * Returns a string representation of the entire CSS structure starting from the root.
     * The structure includes all child classes and their properties.
     *
     * @return the CSS structure as a string
     */
    @Override
    public String toString() {
        CssStringBuilder root = this;
        while (root.parent != null) root = root.parent;
        StringBuilder res = new StringBuilder();
        toStringHelper(root, new LinkedList<>(), res);
        return res.toString();
    }

    /**
     * Helper method to recursively build the string representation of the CSS structure.
     *
     * @param current the current {@code CssBuilder} being processed
     * @param previous a collection of previous {@code CssBuilder} instances in the hierarchy
     * @param buffer the {@code StringBuilder} used to accumulate the CSS structure
     */
    private void toStringHelper(CssStringBuilder current, Collection<CssStringBuilder> previous, StringBuilder buffer) {
        previous.stream().flatMap(b -> Stream.of(".", b.className, " ")).forEach(buffer::append);
        buffer.append(".").append(current.className).append(" {\n");
        current.properties.entrySet().stream().flatMap(e -> Stream.of("\t", e.getKey(), ": ", e.getValue(), ";\n")).forEach(buffer::append);
        buffer.append("}\n");
        if (!current.children.isEmpty()) {
            previous.add(current);
            for (CssStringBuilder child : current.children) toStringHelper(child, previous, buffer);
            previous.remove(current);
        }
    }

    /**
     * Compares this {@code CssBuilder} to another based on the class name.
     *
     * @param o the other {@code CssBuilder} to be compared
     * @return a negative integer, zero, or a positive integer as this object's class name
     *         is less than, equal to, or greater than the specified object's class name
     */
    @Override
    public int compareTo(CssStringBuilder o) {
        return this.className.compareTo(o.className);
    }

    /**
     * Checks if this {@code CssBuilder} is equal to another object.
     *
     * @param o the object to compare with
     * @return {@code true} if this object is equal to the specified object, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CssStringBuilder that = (CssStringBuilder) o;
        return Objects.equals(className, that.className);
    }

    /**
     * Returns the hash code for this {@code CssBuilder}.
     *
     * @return the hash code based on the class name
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(className);
    }
}

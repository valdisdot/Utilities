package com.valdisdot.util.vaadin.helper;

import java.util.*;
import java.util.stream.Stream;

/**
 * A builder class for creating CSS structures programmatically.
 * The {@code CssStringBuilder} class allows the definition of CSS classes, their properties,
 * and hierarchical relationships between them.
 *
 * <p>The class provides methods for adding CSS properties, creating child classes,
 * and generating a string representation of the entire CSS structure.</p>
 */
public class CssStringBuilder implements Comparable<CssStringBuilder> {
    private final String elementName;
    private String pseudoClass;
    private boolean isTag;
    private boolean isId;
    private boolean isDirectChild;
    private final TreeMap<String, String> properties = new TreeMap<>();
    private CssStringBuilder parent;
    private final LinkedHashSet<CssStringBuilder> children = new LinkedHashSet<>();

    /**
     * Constructs a {@code CssStringBuilder} with the specified element name.
     * The element name is sanitized to ensure it is a valid CSS selector.
     *
     * @param elementName the name of the CSS element
     * @throws IllegalArgumentException if the {@code elementName} is null or blank
     */
    private CssStringBuilder(String elementName) {
        if (elementName == null || elementName.isBlank()) throw new IllegalArgumentException("Element name is empty");
        this.elementName = elementName.replaceAll("(^\\\\d|[^a-zA-Z0-9_-]|^-\\\\d|^-|^-.*-$|(?<=\\\\s)-(?=\\\\s))", "");
    }

    /**
     * Creates a {@code CssStringBuilder} for a tag selector.
     *
     * @param tagName the name of the tag
     * @return a new {@code CssStringBuilder} instance for the tag selector
     */
    public static CssStringBuilder tagSelector(String tagName) {
        CssStringBuilder res = new CssStringBuilder(tagName);
        res.isTag = true;
        return res;
    }

    /**
     * Creates a {@code CssStringBuilder} for an ID selector.
     *
     * @param id the ID of the element
     * @return a new {@code CssStringBuilder} instance for the ID selector
     */
    public static CssStringBuilder idSelector(String id) {
        CssStringBuilder res = new CssStringBuilder(id);
        res.isId = true;
        return res;
    }

    /**
     * Creates a {@code CssStringBuilder} for a class selector.
     *
     * @param className the name of the class
     * @return a new {@code CssStringBuilder} instance for the class selector
     */
    public static CssStringBuilder classSelector(String className) {
        return new CssStringBuilder(className);
    }

    /**
     * Adds a CSS property to this class.
     * The property is added only if both the key and value are non-null and non-blank.
     *
     * @param key   the CSS property name
     * @param value the CSS property value
     * @return this {@code CssStringBuilder} instance for method chaining
     */
    public CssStringBuilder putProperties(String key, Object value) {
        if (key != null && value != null && !key.isBlank() && !value.toString().isBlank()) properties.put(key, value.toString());
        return this;
    }

    /**
     * Adds multiple CSS properties to this class from the provided map.
     * Each key-value pair in the map represents a CSS property name and its corresponding value.
     * The properties are added only if the map is non-null. Each property is added only if both the key and value are non-null and non-blank.
     *
     * @param values a map containing CSS property names and their corresponding values
     * @return this {@code CssStringBuilder} instance for method chaining
     */
    public CssStringBuilder putProperties(Map<String, Object> values) {
        if (values != null) values.forEach(this::putProperties);
        return this;
    }

    /**
     * Sets a pseudo-class for this CSS selector.
     * <p>
     * The pseudo-class is added to the current selector only if the provided value is non-null and non-blank.
     * If the input is null or blank, the existing pseudo-class value remains unchanged.
     * </p>
     *
     * @param pseudoClass the pseudo-class to be added to this CSS selector. If null or blank, the current pseudo-class remains unchanged.
     * @return this {@code CssStringBuilder} instance for method chaining.
     */
    public CssStringBuilder setPseudoClass(String pseudoClass) {
        this.pseudoClass = pseudoClass == null || pseudoClass.isBlank() ? this.pseudoClass : pseudoClass;
        return this;
    }

    /**
     * Creates a child CSS class under the current class.
     * The child class name must be non-null and non-blank.
     *
     * @param childName the name of the child CSS class
     * @param isDirectChild whether the child is a direct descendant
     * @return the newly created {@code CssStringBuilder} instance representing the child class
     * @throws IllegalArgumentException if the {@code childName} is null or blank
     */
    public CssStringBuilder putClass(String childName, boolean isDirectChild) {
        return putChild(classSelector(childName), isDirectChild);
    }

    /**
     * Creates a child CSS ID under the current class.
     * The ID name must be non-null and non-blank.
     *
     * @param id the ID of the child element
     * @param isDirectChild whether the child is a direct descendant
     * @return the newly created {@code CssStringBuilder} instance representing the child ID
     * @throws IllegalArgumentException if the {@code id} is null or blank
     */
    public CssStringBuilder putId(String id, boolean isDirectChild) {
        return putChild(idSelector(id), isDirectChild);
    }

    /**
     * Creates a child CSS tag under the current class.
     * The tag name must be non-null and non-blank.
     *
     * @param tagName the name of the tag
     * @param isDirectChild whether the child is a direct descendant
     * @return the newly created {@code CssStringBuilder} instance representing the child tag
     * @throws IllegalArgumentException if the {@code tagName} is null or blank
     */
    public CssStringBuilder putTag(String tagName, boolean isDirectChild) {
        return putChild(tagSelector(tagName), isDirectChild);
    }

    private CssStringBuilder putChild(CssStringBuilder child, boolean isDirectChild) {
        child.isDirectChild = isDirectChild;
        children.add(child);
        child.parent = this;
        return child;
    }

    /**
     * Returns the parent CSS class of the current class.
     *
     * @return the parent {@code CssStringBuilder} instance
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
     * @param current  the current {@code CssStringBuilder} being processed
     * @param previous a collection of previous {@code CssStringBuilder} instances in the hierarchy
     * @param buffer   the {@code StringBuilder} used to accumulate the CSS structure
     */
    private void toStringHelper(CssStringBuilder current, Collection<CssStringBuilder> previous, StringBuilder buffer) {
        previous.forEach(el -> buffer
                .append(el.isDirectChild ? "> " : "")
                .append(el.isId ? "#" : el.isTag ? "" : ".")
                .append(el.elementName)
                .append(el.pseudoClass == null ? "" : ":" + el.pseudoClass)
                .append(" "));
        buffer
                .append(current.isDirectChild ? "> " : "")
                .append(current.isId ? "#" : current.isTag ? "" : ".")
                .append(current.elementName)
                .append(current.pseudoClass == null ? "" : ":" + current.pseudoClass)
                .append(" {\n");
        current.properties.entrySet().stream().flatMap(e -> Stream.of("\t", e.getKey(), ": ", e.getValue(), ";\n")).forEach(buffer::append);
        buffer.append("}\n");
        if (!current.children.isEmpty()) {
            previous.add(current);
            for (CssStringBuilder child : current.children) toStringHelper(child, previous, buffer);
            previous.remove(current);
        }
    }

    /**
     * Compares this {@code CssStringBuilder} to another based on the element name.
     *
     * @param o the other {@code CssStringBuilder} to be compared
     * @return a negative integer, zero, or a positive integer as this object's element name
     * is less than, equal to, or greater than the specified object's element name
     */
    @Override
    public int compareTo(CssStringBuilder o) {
        return this.elementName.compareTo(o.elementName);
    }

    /**
     * Checks if this {@code CssStringBuilder} is equal to another object.
     *
     * @param o the object to compare with
     * @return {@code true} if this object is equal to the specified object, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CssStringBuilder that = (CssStringBuilder) o;
        return Objects.equals(elementName, that.elementName);
    }

    /**
     * Returns the hash code for this {@code CssStringBuilder}.
     *
     * @return the hash code based on the element name
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(elementName);
    }
}

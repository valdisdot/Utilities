package com.valdisdot.util.vaadin.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;

import java.util.Collection;

/**
 * A custom {@link Div} component for creating block-level HTML elements with optional class names and child components.
 * <p>
 * The {@code BlockElement} class provides constructors to initialize a block element with a CSS class name and
 * optionally with a collection or an array of {@link Component} instances, or a text string.
 * </p>
 */
public class BlockElement extends Div {

    /**
     * Constructs a new {@code BlockElement} with the specified class name.
     *
     * @param className the CSS class name to be added to the element. If null or blank, no class name is added.
     */
    public BlockElement(String className) {
        super();
        if (className != null && !className.isBlank()) addClassName(className);
    }

    /**
     * Constructs a new {@code BlockElement} with the specified class name and a collection of child components.
     *
     * @param className  the CSS class name to be added to the element. If null or blank, no class name is added.
     * @param components the collection of child components to be added to the element. If null, no components are added.
     */
    public BlockElement(String className, Collection<Component> components) {
        this(className);
        if (components != null) {
            for (Component c : components) {
                add(c);
            }
        }
    }

    /**
     * Constructs a new {@code BlockElement} with the specified class name and an array of child components.
     *
     * @param className  the CSS class name to be added to the element. If null or blank, no class name is added.
     * @param components the array of child components to be added to the element. If null, no components are added.
     */
    public BlockElement(String className, Component... components) {
        this(className);
        if (components != null) {
            for (Component c : components) {
                add(c);
            }
        }
    }

    /**
     * Constructs a new {@code BlockElement} with the specified class name and a text node.
     * The text is wrapped in a {@link Text} component and added as a child to this block element.
     *
     * @param className the CSS class name to be added to the element. If null or blank, no class name is added.
     * @param text      the text content to be added as a {@link Text} component.
     */
    public BlockElement(String className, String text) {
        this(className, new Text(text));
    }
}

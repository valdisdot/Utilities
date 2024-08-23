package com.valdisdot.util.vaadin.helper;

import java.util.Optional;

/**
 * Interface for generating and managing property keys for components and classes.
 * <p>
 * This interface helps to generate keys based on the class name and provided suffixes,
 * reducing the need for future refactoring.
 * </p>
 **/
public interface PropertiesRegister {
    /**
     * Registers a component and returns its key.
     *
     * @param component the component class to be registered
     * @return the generated key for the component
     */
    String registerComponent(Class<?> component);

    /**
     * Retrieves the registered key for a given component.
     *
     * @param component the component class to look up
     * @return an {@link Optional} containing the component key if found, otherwise empty
     */
    Optional<String> getPropertyKeyFor(Class<?> component);

    /**
     * Retrieves the registered key for a given component with suffixes appended.
     *
     * @param component the component class to look up
     * @param withSuffixes  the suffixes to append to the key
     * @return an {@link Optional} containing the component key with suffixes if found, otherwise empty
     */
    Optional<String> getPropertyKeyFor(Class<?> component, String ... withSuffixes);
}

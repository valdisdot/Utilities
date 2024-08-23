package com.valdisdot.util.commons;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represents a generic tree container structure.
 *
 * @param <E> the type of elements stored in this tree container
 */
public interface TreeContainer<E> {

    /**
     * Checks if this container is the root of the tree.
     *
     * @return true if this container is the root, false otherwise
     */
    boolean isRoot();

    /**
     * Checks if this container is a germ root, which means it is a root without any branches.
     *
     * @return true if this container is a germ root, false otherwise
     */
    boolean isGermRoot();

    /**
     * Checks if this container is a node, which means it has branches.
     *
     * @return true if this container is a node, false otherwise
     */
    boolean isNode();

    /**
     * Checks if this container is a leaf, which means it has no branches.
     *
     * @return true if this container is a leaf, false otherwise
     */
    boolean isLeaf();

    /**
     * Retrieves the sub-containers of this tree container.
     *
     * @return a collection of sub-containers
     */
    Collection<? extends TreeContainer<E>> getSubContainers();

    /**
     * Retrieves the element stored in this tree container.
     *
     * @return the element stored in this container
     */
    E getElement();

    /**
     * Returns the first element in the next level of subcontainers that matches the given predicate.
     *
     * @param predicate the predicate to test the elements
     * @return an {@link Optional} containing the first matching element, or an empty {@link Optional} if no match is found
     */
    Optional<E> getNextFirstElement(Predicate<E> predicate);

    /**
     * Returns any element in the next level of subcontainers that matches the given predicate.
     *
     * @param predicate the predicate to test the elements
     * @return an {@link Optional} containing a matching element, or an empty {@link Optional} if no match is found
     */
    Optional<E> getNextElement(Predicate<E> predicate);

    /**
     * Returns a collection of elements in the next level of subcontainers that match the given predicate.
     *
     * @param predicate the predicate to test the elements
     * @return a collection of matching elements
     */
    Collection<E> getNextElements(Predicate<E> predicate);

    /**
     * Checks if the current element matches the given predicate.
     *
     * @param predicate the predicate to test the element
     * @return {@code true} if the element matches the predicate, {@code false} otherwise
     */
    boolean isElementMatches(Predicate<E> predicate);

    /**
     * Flattens the tree into a collection of its elements. This method performs
     * a depth-first traversal of the tree and returns a collection containing all
     * the elements of the tree in a flat structure.
     *
     * @return a collection containing all elements of the tree
     */
    default Collection<E> flatTree() {
        if (isLeaf()) return List.of(getElement());
        if (isNode()) {
            if (isRoot()) {
                return getSubContainers().stream()
                        .map(TreeContainer::flatTree)
                        .collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll);
            } else {
                return getSubContainers().stream()
                        .map(TreeContainer::flatTree)
                        .collect(() -> new LinkedList<>(List.of(getElement())), LinkedList::addAll, LinkedList::addAll);
            }
        } else return List.of();
    }

}



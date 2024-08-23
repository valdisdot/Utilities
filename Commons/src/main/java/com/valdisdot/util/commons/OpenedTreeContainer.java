package com.valdisdot.util.commons;
import java.util.*;
import java.util.function.Predicate;

/**
 * Implementation of the {@link TreeContainer} interface, representing a tree structure with elements.
 *
 * @param <E> the type of elements stored in this tree container
 */
public class OpenedTreeContainer<E> implements TreeContainer<E> {
    private final E value;
    private final OpenedTreeContainer<E> previous;
    private List<OpenedTreeContainer<E>> branches;

    /**
     * Constructs a new OpenedTreeContainer with the specified previous container and value.
     *
     * @param previous the previous container in the tree
     * @param value    the value of this container
     */
    private OpenedTreeContainer(OpenedTreeContainer<E> previous, E value) {
        this.previous = previous;
        this.value = value;
    }

    /**
     * Constructs a new OpenedTreeContainer as the root of the tree.
     */
    public OpenedTreeContainer() {
        this(null, null);
    }

    /**
     * Adds a sequence of values to the tree, creating branches and leaves as needed.
     *
     * @param target the target container where the values will be added
     * @param values the values to be added
     */
    @SafeVarargs
    private void put(OpenedTreeContainer<E> target, E... values) {
        // done
        if (values == null || values.length == 0) return;
        // skip null
        if (values[0] == null) put(target, Arrays.copyOfRange(values, 1, values.length));
        else {
            // target is a leaf, make branches
            if (target.branches == null) target.branches = new LinkedList<>();
            // make a leaf
            if (values.length == 1) {
                // if not defined earlier
                if (branches.stream().noneMatch(c -> c.value.equals(value)))
                    target.branches.add(new OpenedTreeContainer<>(target, values[0]));
            } else {
                // go through branches
                Optional<OpenedTreeContainer<E>> existingOne = target.branches.stream().filter(c -> c.value.equals(values[0])).findAny();
                OpenedTreeContainer<E> newTarget;
                if (existingOne.isPresent()) newTarget = existingOne.get();
                else {
                    newTarget = new OpenedTreeContainer<>(target, values[0]);
                    target.branches.add(newTarget);
                }
                put(newTarget, Arrays.copyOfRange(values, 1, values.length));
            }
        }
    }

    /**
     * Adds a sequence of values to this tree container, creating branches and leaves as needed.
     *
     * @param values the values to be added
     * @return this tree container
     */
    @SafeVarargs
    public final OpenedTreeContainer<E> put(E... values) {
        put(this, values);
        return this;
    }

    /**
     * Adds a collection of values to this tree container, creating branches and leaves as needed.
     *
     * @param values the values to be added
     * @return this tree container
     */
    public OpenedTreeContainer<E> put(Collection<E> values) {
        if (values != null) put(this, ((E[]) values.toArray()));
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRoot() {
        return previous == null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isGermRoot() {
        return previous == null && branches == null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isNode() {
        return previous != null && branches != null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isLeaf() {
        return previous != null && branches == null;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<? extends TreeContainer<E>> getSubContainers() {
        return branches;
    }

    /** {@inheritDoc} */
    @Override
    public E getElement() {
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<E> getNextFirstElement(Predicate<E> predicate) {
        return isNode() ? branches.stream().map(OpenedTreeContainer::getElement).filter(predicate).findFirst() : Optional.empty();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<E> getNextElement(Predicate<E> predicate) {
        return isNode() ? branches.stream().map(OpenedTreeContainer::getElement).filter(predicate).findAny() : Optional.empty();
    }

    /** {@inheritDoc} */
    @Override
    public Collection<E> getNextElements(Predicate<E> predicate) {
        return isNode() ? branches.stream().map(OpenedTreeContainer::getElement).filter(predicate).toList() : List.of();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isElementMatches(Predicate<E> predicate) {
        return value != null && predicate.test(value);
    }

    /**
     * Returns a string representation of this tree container.
     *
     * @return a string representation of this tree container
     */
    @Override
    public String toString() {
        return "Node {\nvalue: " + (value == null ? "ROOT" : value) +
                "\nprevious.value: " + (previous == null ? "ROOT" : previous.value) +
                "\nbranches.size: " + (branches == null ? 0 : branches.size()) +
                "\nbranches: " + branches + "\n}";
    }

    /**
     * Retrieves all paths from the root to each leaf in the tree using depth-first search (DFS).
     * The root element itself is excluded from the paths.
     *
     * @return a list of paths, where each path is represented as a list of nodes
     */
    public List<List<OpenedTreeContainer<E>>> getPaths() {
        List<List<OpenedTreeContainer<E>>> paths = new ArrayList<>();
        if (branches != null) {
            for (OpenedTreeContainer<E> branch : branches) {
                List<OpenedTreeContainer<E>> currentPath = new ArrayList<>();
                getPathsHelper(branch, currentPath, paths);
            }
        }
        return paths;
    }

    /**
     * Helper method for DFS to retrieve all paths from the current node to each leaf.
     *
     * @param node        the current node
     * @param currentPath the current path being constructed
     * @param paths       the list of all paths
     */
    private void getPathsHelper(OpenedTreeContainer<E> node, List<OpenedTreeContainer<E>> currentPath, List<List<OpenedTreeContainer<E>>> paths) {
        if (node == null) return;
        currentPath.add(node);
        if (node.isLeaf()) {
            paths.add(new ArrayList<>(currentPath));
        } else if (node.branches != null) {
            for (OpenedTreeContainer<E> branch : node.branches) {
                getPathsHelper(branch, currentPath, paths);
            }
        }
        currentPath.remove(currentPath.size() - 1);
    }
}

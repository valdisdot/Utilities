package com.valdisdot.util.commons;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * A default implementation of the {@link Pagination} interface that provides
 * paginated access to collections of elements. The pagination is based on a
 * supplier function that generates collections for each page.
 *
 * @param <E> the type of elements in the paginated collection
 */
public class DefaultPagination<E> implements Pagination<E> {

    private final BiFunction<Integer, Integer, Supplier<Collection<E>>> collectionsSupplier;
    private final int indexOffset;
    private final Integer elementsPerPageSize;
    private final int lastIndex;
    private int current = 1;

    /**
     * Constructs a new {@code DefaultPagination} with the specified parameters.
     *
     * @param elementsPerPageSize   the number of elements per page; must be greater than 0
     * @param totalElementsCount    the total number of elements across all pages; must be greater than 0
     * @param collectionsSupplier   a function that supplies a collection for a given page index and element count per page
     * @param indexOffset           the offset to be subtracted from the pagination index to convert it to the data structure's index
     * @throws IndexOutOfBoundsException if the elementsPerPageSize or totalElementsCount are less than or equal to 0
     * @throws NullPointerException if the collectionsSupplier is null
     */
    public DefaultPagination(int elementsPerPageSize, int totalElementsCount, BiFunction<Integer, Integer, Supplier<Collection<E>>> collectionsSupplier, int indexOffset) {
        if (elementsPerPageSize <= 0) throw new IndexOutOfBoundsException("Elements per page size cannot be " + elementsPerPageSize);
        if (totalElementsCount <= 0) throw new IndexOutOfBoundsException("Total elements count cannot be " + totalElementsCount);
        this.lastIndex = totalElementsCount / elementsPerPageSize + (totalElementsCount % elementsPerPageSize != 0 ? 1 : 0);
        this.collectionsSupplier = Objects.requireNonNull(collectionsSupplier, "Collections supplier bi-function cannot be null");
        this.elementsPerPageSize = elementsPerPageSize;
        this.indexOffset = indexOffset;
    }

    /**
     * Constructs a new {@code DefaultPagination} with the specified parameters, using a default index offset of 1.
     *
     * @param elementsPerPageSize   the number of elements per page; must be greater than 0
     * @param totalElementsCount    the total number of elements across all pages; must be greater than 0
     * @param collectionsSupplier   a function that supplies a collection for a given page index and element count per page
     * @throws IndexOutOfBoundsException if the elementsPerPageSize or totalElementsCount are less than or equal to 0
     * @throws NullPointerException if the collectionsSupplier is null
     */
    public DefaultPagination(int elementsPerPageSize, int totalElementsCount, BiFunction<Integer, Integer, Supplier<Collection<E>>> collectionsSupplier) {
        this(elementsPerPageSize, totalElementsCount, collectionsSupplier, 1);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NoPageException if the specified page index is out of bounds or if there is no supplier for the specified page
     */
    @Override
    public synchronized Supplier<Collection<E>> jumpToPageAt(int index) throws NoPageException {
        if (index > lastIndex || index < 1)
            throw new NoPageException(String.format("Page index %d is out of the bound of [1, %d]", index, lastIndex));
        Supplier<Collection<E>> applied = collectionsSupplier.apply(index - indexOffset, elementsPerPageSize);
        if (applied == null)
            throw new NoPageException("There is no supplier for page at index " + index, new NullPointerException("Page elements supplier is null"));
        current = index;
        return applied;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int currentPageIndex() {
        return current;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int lastPageIndex() {
        return lastIndex;
    }
}


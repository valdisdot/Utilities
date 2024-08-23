package com.valdisdot.util.commons;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Interface for paginated data structures that provide access to elements
 * in a paginated manner. Implementations must always have at least page 1,
 * even if it is empty. The page count must begin from 1, not 0.
 *
 * @param <E> the type of elements in the paginated collection
 */
public interface Pagination<E> {

    /**
     * Jumps to the specified page index and returns a {@link Supplier} for the
     * collection of elements on that page.
     *
     * @param index the index of the page to jump to, starting from 1
     * @return a {@link Supplier} providing the collection of elements on the specified page
     * @throws NoPageException if the specified page index does not exist
     */
    Supplier<Collection<E>> jumpToPageAt(int index) throws NoPageException;

    /**
     * Returns the index of the current page.
     *
     * @return the current page index, starting from 1
     */
    int currentPageIndex();

    /**
     * Returns the index of the last page.
     *
     * @return the last page index, which is always >= 1
     */
    int lastPageIndex();

    /**
     * Jumps to the previous page and returns a {@link Supplier} for its collection of elements.
     *
     * @return a {@link Supplier} providing the collection of elements on the previous page
     * @throws NoPageException if there is no previous page
     */
    default Supplier<Collection<E>> previous() throws NoPageException {
        return jumpToPageAt(currentPageIndex() - 1);
    }

    /**
     * Jumps to the next page and returns a {@link Supplier} for its collection of elements.
     *
     * @return a {@link Supplier} providing the collection of elements on the next page
     * @throws NoPageException if there is no next page
     */
    default Supplier<Collection<E>> next() throws NoPageException {
        return jumpToPageAt(currentPageIndex() + 1);
    }

    /**
     * Jumps to the previous page or to the first page if there is no previous page.
     *
     * @return a {@link Supplier} providing the collection of elements on the previous or first page
     */
    default Supplier<Collection<E>> previousOrStart() {
        try {
            return previous();
        } catch (NoPageException e) {
            return jumpToStart();
        }
    }

    /**
     * Jumps to the first page and returns a {@link Supplier} for its collection of elements.
     *
     * @return a {@link Supplier} providing the collection of elements on the first page
     */
    default Supplier<Collection<E>> jumpToStart() {
        try {
            return jumpToPageAt(1);
        } catch (NoPageException e) {
            throw new RuntimeException("Wrong implementation, calling 'jumpToStart' must never throw this exception", e);
        }
    }

    /**
     * Jumps to the next page or to the last page if there is no next page.
     *
     * @return a {@link Supplier} providing the collection of elements on the next or last page
     */
    default Supplier<Collection<E>> nextOrEnd() {
        try {
            return next();
        } catch (NoPageException e) {
            return jumpToEnd();
        }
    }

    /**
     * Jumps to the last page and returns a {@link Supplier} for its collection of elements.
     *
     * @return a {@link Supplier} providing the collection of elements on the last page
     */
    default Supplier<Collection<E>> jumpToEnd() {
        try {
            return jumpToPageAt(lastPageIndex());
        } catch (NoPageException e) {
            throw new RuntimeException("Wrong implementation, calling 'jumpToEnd' must never throw this exception", e);
        }
    }

    /**
     * Checks if the pagination contains no elements (i.e., it is blank).
     *
     * @return {@code true} if the pagination is blank; {@code false} otherwise
     */
    default boolean isBlank() {
        return lastPageIndex() == 1 && jumpToStart().get().isEmpty();
    }

    /**
     * Exception thrown when attempting to access a page that does not exist.
     */
    class NoPageException extends Exception {

        /**
         * Constructs a {@code NoPageException} with the specified detail message.
         *
         * @param message the detail message
         */
        public NoPageException(String message) {
            super(message);
        }

        /**
         * Constructs a {@code NoPageException} with the specified detail message and cause.
         *
         * @param message the detail message
         * @param cause   the cause of the exception
         */
        public NoPageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}


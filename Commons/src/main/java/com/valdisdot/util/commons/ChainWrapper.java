package com.valdisdot.util.commons;

import java.util.Objects;
import java.util.function.*;

/**
 * A utility class for working with stateful objects in a chainable manner.
 * The Wrapper class allows the state of an object to be changed through various functional interfaces.
 * This class is designed to work with mutable objects, as using it with immutable objects does not make sense.
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * String s = new Wrapper<>(400, StringBuilder::new)
 *                 .apply("Hello", StringBuilder::append)
 *                 .apply(" Mama!", StringBuilder::append)
 *                 .apply(System.out::println)
 *                 .apply(b -> System.out.println(b.capacity()))
 *                 .apply(StringBuilder::trimToSize)
 *                 .apply(b -> System.out.println(b.capacity()))
 *                 .get()
 *                 .toString();
 * }</pre>
 * </p>
 */

public class ChainWrapper<T> implements Supplier<T> {
    private T t;

    /**
     * Constructs a Wrapper with the given object.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * MyObject obj = new MyObject();
     * Wrapper<MyObject> wrapper = new Wrapper<>(obj);
     * }</pre>
     *
     * @param object the object to wrap
     * @throws NullPointerException if the object is null
     */
    public ChainWrapper(T object) {
        this.t = Objects.requireNonNull(object, "Object is null");
    }

    /**
     * Constructs a Wrapper with an object supplied by the given supplier.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * Wrapper<MyObject> wrapper = new Wrapper<>(MyObject::new);
     * }</pre>
     *
     * @param objectSupplier the supplier of the object to wrap
     * @throws NullPointerException if the object supplier or the supplied object is null
     */
    public ChainWrapper(Supplier<T> objectSupplier) {
        this.t = Objects.requireNonNull(Objects.requireNonNull(objectSupplier, "Object supplier is null").get(), "Result of object supplier is null");
    }

    /**
     * Constructs a Wrapper with an object derived from another object using a function.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * Wrapper<MyObject> wrapper = new Wrapper<>("default", MyObject::new);
     * }</pre>
     *
     * @param <F> the type of the input to the function
     * @param deriveFrom the object to derive the wrapped object from
     * @param withFunction the function to derive the wrapped object
     * @throws NullPointerException if the deriving function or the derived object is null
     */
    public <F> ChainWrapper(F deriveFrom, Function<F, T> withFunction) {
        this.t = Objects.requireNonNull(withFunction, "Deriving function is null").apply(deriveFrom);
    }

    /**
     * Constructs a Wrapper with an object derived from two objects using a bi-function.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * Wrapper<MyObject> wrapper = new Wrapper<>("default", true, MyObject::new);
     * }</pre>
     *
     * @param <A> the type of the first input to the bi-function
     * @param <B> the type of the second input to the bi-function
     * @param deriveFromA the first object to derive the wrapped object from
     * @param deriveFromB the second object to derive the wrapped object from
     * @param withFunction the bi-function to derive the wrapped object
     * @throws NullPointerException if the deriving bi-function or the derived object is null
     */
    public <A, B> ChainWrapper(A deriveFromA, B deriveFromB, BiFunction<A, B, T> withFunction) {
        this.t = Objects.requireNonNull(withFunction, "Deriving binary function is null").apply(deriveFromA, deriveFromB);
    }

    /**
     * Applies a consumer to the wrapped object.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * wrapper.apply(MyObject::doSomething);
     * }</pre>
     *
     * @param objectConsumer the consumer to apply to the wrapped object
     * @return the current Wrapper instance for chaining
     * @throws NullPointerException if the object consumer is null
     */
    public ChainWrapper<T> apply(Consumer<T> objectConsumer) {
        Objects.requireNonNull(objectConsumer, "Object consumer is null").accept(t);
        return this;
    }

    /**
     * Applies a bi-consumer to the wrapped object with an additional parameter.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * wrapper.apply(MyObject::testAndDoSomething, "call Mama");
     * }</pre>
     *
     * @param <O> the type of the additional parameter
     * @param objectBiConsumer the bi-consumer to apply to the wrapped object
     * @param other the additional parameter for the bi-consumer
     * @return the current Wrapper instance for chaining
     * @throws NullPointerException if the object bi-consumer is null
     */
    public <O> ChainWrapper<T> apply(BiConsumer<T, O> objectBiConsumer, O other) {
        Objects.requireNonNull(objectBiConsumer, "Object binary consumer is null").accept(t, other);
        return this;
    }

    /**
     * Shares the current wrapped object without resetting its reference.
     *
     * @return the current wrapped object
     */
    public T share() {
        return t;
    }

    /**
     * Retrieves the current wrapped object and resets the reference to null.
     *
     * @return the current wrapped object
     */
    @Override
    public T get() {
        try {
            return t;
        } finally {
            t = null;
        }
    }
}


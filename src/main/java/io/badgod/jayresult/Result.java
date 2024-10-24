package io.badgod.jayresult;

import java.util.function.*;

/**
 * <p>
 * Result&lt;E, T&gt; is the type used for returning and propagating errors. It is an interface with the implementations
 * Ok(T), representing success and containing a value, and Err(E), representing error and containing an error value.
 * </p>
 * <p>
 * A Result is basically a specific variant of an "Either" monad, containing either an ok-value or an error-value.
 * </p>
 * <p>
 * Choose to return a Result whenever errors are expected and recoverable, most prominently used for I/O.
 *
 * @param <E> the error value's type
 * @param <T> to ok value's type
 */
public interface Result<E, T> {

    /**
     * Invokes supplier, if operation succeeds, returns the result in an Ok. If operation throws a (runtime)
     * exception, wraps the exception in an Err
     *
     * @param operation that might fail with an exception
     * @param <T> the expected (normal) result type of operation
     * @return the result of the operation, either Ok(T) or Err(E), where E is the thrown Exception of operation.
     */
    static <T> Result<Throwable, T> of(Supplier<T> operation) {
        try {
            return new Ok<>(operation.get());
        } catch (Throwable err) {
            return new Err<>(err);
        }
    }

    /**
     * Returns <code>true</code> when this Result is an Ok, otherwise <code>false</code>
     * @return  <code>true</code> when Ok, <code>false</code> otherwise
     */
    boolean isOk();

    /**
     * Returns <code>true</code> when this Result is an Err, otherwise <code>false</code>
     * @return  <code>true</code> when Err, <code>false</code> otherwise
     */
    boolean isErr();

    /**
     * Applies the mapping function to the Ok-value, i.e. transforms T -&gt; U when it is an Ok. Does nothing
     * if this is an Err(E)
     *
     * @param mappingFn the value transforming function, applied to T
     * @param <U> the new value type
     * @return the new value wrapped in an Ok (i.e. Ok(U)), or the unchanged Err(E)
     */
    <U> Result<E, U> map(Function<T, U> mappingFn);


    /**
     * Applies the mapping function to the Err-value, i.e. transforms the value of Err(E) to Err(F). Does nothing
     * if this is an Ok(T).
     *
     * @param mappingFn the value transforming function, applied to E
     * @param <F> the new err-value type
     * @return the new value wrapped in an Err (i.e. Err(F)), or the unchanged Ok(T)
     */
    <F> Result<F, T> mapErr(Function<E, F> mappingFn);

    /**
     * Applies the mapping function to the Ok-value, i.e. transforms T -&gt; Result&lt;E, U&gt;. Use this for chaining
     * operations that produce Results.
     *
     * @param mappingFn the operation that takes T and returns a Result&lt;E, U&gt;
     * @param <U> the new wrapped Ok-value type
     * @return either Err(E) or Ok(U)
     */
    <U> Result<E, U> andThen(Function<T, Result<E, U>> mappingFn);

    /**
     * Provides Ok-value T or throws an exception if this is an Err
     * @return T or throws {@link IllegalUnwrap} exception containing the Err type
     */
    T unwrap();


    /**
     * Provides Err-value E or throws an exception if this in an Ok
     * @return E or throws {@link IllegalUnwrap} exception containing the toString representation of Ok value
     */
    E unwrapErr();

    /**
     * Provides the Ok-value if this Result is an Ok, otherwise provides given <code>or</code> if this is an Err
     * @param or the T value to return if this is an Err
     * @return either Ok-value or provided <code>or</code>
     */
    T unwrapOr(T or);

    /**
     * provides the Ok-value if this Result is an Ok or applies the provided function on the Err-value to return a new
     * value of type T
     * @param elseFn function to produce a new T value from Err-value
     * @return value of type T
     */
    T unwrapOrElse(Function<E, T> elseFn);


    /**
     * If this Result is an Ok, returns the provided <code>andResult</code>, otherwise returns self
     * @param andResult the provided Result
     * @param <U> Ok-value type of the returned Result
     * @return either this (Err(E)) or the provided <code>andResult</code>
     */
    <U> Result<E, U> and(Result<E, U> andResult);

    /**
     * If this Result in an Err, returns the provided <code>orResult</code>, otherwise returns self
     * @param orResult the provided Result
     * @param <F> Err-value type of the returned Result
     * @return either this (Ok(T) or the provided <code>orResult</code>
     */
    <F> Result<F, T> or(Result<F, T> orResult);

    /**
     * If this Result is an Ok, simply returns it, otherwise if this Result is an Err, applies the provided mapping
     * function to the Err-value to return a new Result.
     * @param mappingFn the provided mapping function
     * @param <F> the Err-value type of the returned result
     * @return either this (Ok(T)) or the Result from applying the provided mapping function to the Err-value
     */
    <F> Result<F, T> orElse(Function<E, Result<F, T>> mappingFn);

    /**
     * Applies given consumer on this Result and then returns this Result. Consumer should not modify this Result.
     * Mainly intended for debugging/inspection of this Result
     * @param consumer to be applied on this Result
     * @return this
     */
    default Result<E, T> inspect(Consumer<Result<E, T>> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * Exception that is thrown {@link Result#unwrap()} is invoked on an Err, or {@link Result#unwrapErr()} is
     * invoked on an Ok
     */
    class IllegalUnwrap extends RuntimeException {
        IllegalUnwrap(String msg) {
            super(msg);
        }

        IllegalUnwrap(Throwable throwable) {
            super(throwable);
        }
    }

}
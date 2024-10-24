package io.badgod.jayresult;

import java.util.Objects;
import java.util.function.Function;

public class Err<E, T> implements Result<E, T> {

    private final E err;

    public Err(E err) {
        this.err = err;
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public boolean isErr() {
        return true;
    }

    @Override
    public <U> Result<E, U> map(Function<T, U> mappingFn) {
        return new Err<>(err);
    }

    @Override
    public <F> Result<F, T> mapErr(Function<E, F> mappingFn) {
        return new Err<>(mappingFn.apply(err));
    }

    @Override
    public <U> Result<E, U> andThen(Function<T, Result<E, U>> mappingFn) {
        return new Err<>(err);
    }

    @Override
    public T unwrap() {
        if (err instanceof Throwable) {
            throw new IllegalUnwrap((Throwable) err);
        } else {
            throw new IllegalUnwrap(err.toString());
        }
    }

    @Override
    public E unwrapErr() {
        return err;
    }

    @Override
    public T unwrapOr(T or) {
        return or;
    }

    @Override
    public T unwrapOrElse(Function<E, T> elseFn) {
        return elseFn.apply(err);
    }

    @Override
    public <U> Result<E, U> and(Result<E, U> andResult) {
        return new Err<>(err);
    }

    @Override
    public <F> Result<F, T> or(Result<F, T> orResult) {
        return orResult;
    }

    @Override
    public <F> Result<F, T> orElse(Function<E, Result<F, T>> mappingFn) {
        return mappingFn.apply(err);
    }

    @Override
    public String toString() {
        return "Err(" + err + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Err<?, ?> err1 = (Err<?, ?>) o;
        return Objects.equals(err, err1.err);
    }

    @Override
    public int hashCode() {
        return err.hashCode();
    }
}

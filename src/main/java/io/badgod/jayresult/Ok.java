package io.badgod.jayresult;

import java.util.Objects;
import java.util.function.Function;

public class Ok<E, T> implements Result<E, T> {

    private final T ok;

    public Ok(T ok) {
        Objects.requireNonNull(ok);
        this.ok = ok;
    }

    @Override
    public boolean isOk() {
        return true;
    }

    @Override
    public boolean isErr() {
        return false;
    }

    @Override
    public <U> Result<E, U> map(Function<T, U> mappingFn) {
        return new Ok<>(mappingFn.apply(ok));
    }

    @Override
    public <F> Result<F, T> mapErr(Function<E, F> mappingFn) {
        return new Ok<>(ok);
    }

    @Override
    public <U> Result<E, U> andThen(Function<T, Result<E, U>> mappingFn) {
        return mappingFn.apply(ok);
    }

    @Override
    public T unwrap() {
        return ok;
    }

    @Override
    public E unwrapErr() {
        throw new IllegalUnwrap(ok.toString());
    }

    @Override
    public T unwrapOr(T or) {
        return ok;
    }

    @Override
    public T unwrapOrElse(Function<E, T> elseFn) {
        return ok;
    }

    @Override
    public <U> Result<E, U> and(Result<E, U> andResult) {
        return andResult;
    }

    @Override
    public <F> Result<F, T> or(Result<F, T> orResult) {
        return new Ok<>(ok);
    }

    @Override
    public <F> Result<F, T> orElse(Function<E, Result<F, T>> mappingFn) {
        return new Ok<>(ok);
    }

    @Override
    public String toString() {
        return "Ok(" + ok + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ok<?, ?> ok1 = (Ok<?, ?>) o;
        return Objects.equals(ok, ok1.ok);
    }

    @Override
    public int hashCode() {
        return ok.hashCode();
    }
}
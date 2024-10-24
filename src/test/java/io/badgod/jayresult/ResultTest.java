package io.badgod.jayresult;


import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

public class ResultTest {

    @Test
    public void of() {
        assertThat(Result.of(() -> 5), is(new Ok<>(5)));

        RuntimeException exception = new RuntimeException("error");
        assertThat(Result.of(() -> {
            throw exception;
        }), is(new Err<>(exception)));
    }

    @Test
    public void isOk() {
        assertThat(new Ok<>(1).isOk(), is(true));
        assertThat(new Ok<>(1).isErr(), is(false));

        assertThat(new Err<>(1).isOk(), is(false));
        assertThat(new Err<>(1).isErr(), is(true));
    }

    @Test
    public void map() {
        assertThat(new Ok<>("foo").map(String::length), is(new Ok<>(3)));
        assertThat(new Err<String, String>("foo").map(String::length), is(new Err<>("foo")));
    }

    @Test
    public void mapErr() {
        assertThat(new Ok<String, String>("foo").mapErr(String::length), is(new Ok<>("foo")));
        assertThat(new Err<>("foo").mapErr(String::length), is(new Err<>(3)));
    }

    @Test
    public void andThen() {
        assertThat(new Ok<>("foo").andThen(s -> new Ok<>(s + "bar")), is(new Ok<>("foobar")));
        assertThat(new Ok<>("foo").andThen(s -> new Err<>(s + "bar")), is(new Err<>("foobar")));

        assertThat(new Err<>("foo").andThen(s -> new Ok<>(s + "bar")), is(new Err<>("foo")));
        assertThat(new Err<>("foo").andThen(s -> new Err<>(s + "bar")), is(new Err<>("foo")));
    }

    @Test
    public void unwrap() {
        assertThat(new Ok<>(2).unwrap(), is(2));

        try {
            new Err<>(2).unwrap();
            fail("should throw");
        } catch (Throwable e) {
            assertThat(e, is(instanceOf(Result.IllegalUnwrap.class)));
            assertThat(e.getMessage(), is("2"));
        }
    }

    @Test
    public void unwrapAnErrContainingThrowablePreservesCause() {
        IllegalArgumentException cause = new IllegalArgumentException("any");
        try {
            new Err<>(cause).unwrap();
            fail("should throw");
        } catch (Throwable e) {
            assertThat(e, is(instanceOf(Result.IllegalUnwrap.class)));
            assertThat(e.getMessage(), is("java.lang.IllegalArgumentException: any"));
            assertThat(e.getCause(), is(cause));
        }
    }

    @Test
    public void unwrapErr() {
        assertThat(new Err<>(2).unwrapErr(), is(2));

        try {
            new Ok<>(2).unwrapErr();
            fail("should throw");
        } catch (Throwable e) {
            assertThat(e, is(instanceOf(Result.IllegalUnwrap.class)));
            assertThat(e.getMessage(), is("2"));
        }
    }

    @Test
    public void unwrapOr() {
        assertThat(new Ok<>(2).unwrapOr(5), is(2));
        assertThat(new Err<>(2).unwrapOr(5), is(5));
    }

    @Test
    public void unwrapOrElse() {
        assertThat(new Ok<Integer, Integer>(2).unwrapOrElse(err -> err + 5), is(2));
        assertThat(new Err<>(2).unwrapOrElse(err -> err + 5), is(7));
    }

    @Test
    public void and() {
        assertThat(new Ok<>(2).and(new Ok<>(7)), is(new Ok<>(7)));
        assertThat(new Ok<>(2).and(new Err<>(7)), is(new Err<>(7)));

        assertThat(new Err<>(2).and(new Ok<>(7)), is(new Err<>(2)));
        assertThat(new Err<>(2).and(new Err<>(7)), is(new Err<>(2)));
    }

    @Test
    public void or() {
        assertThat(new Ok<>(2).or(new Ok<>(7)), is(new Ok<>(2)));
        assertThat(new Ok<>(2).or(new Err<>(7)), is(new Ok<>(2)));

        assertThat(new Err<>(2).or(new Ok<>(7)), is(new Ok<>(7)));
        assertThat(new Err<>(2).or(new Err<>(7)), is(new Err<>(7)));
    }

    @Test
    public void orElse() {
        assertThat(new Ok<Integer, Integer>(2).orElse(err -> new Ok<>(err * 2)), is(new Ok<>(2)));
        assertThat(new Err<Integer, Integer>(3).orElse(err -> new Ok<>(err * 2)), is(new Ok<>(6)));
    }

    @Test
    public void inspect() {
        String[] test = new String[]{"ok: ", "err: "};
        Result<String, Integer> inspectedOk = new Ok<String, Integer>(2).inspect(result -> test[0] = test[0] + result);
        Result<Integer, Integer> inspectedErr = new Err<Integer, Integer>(2).inspect(result -> test[1] = test[1] + result);

        assertThat(inspectedOk, is(new Ok<>(2)));
        assertThat(inspectedErr, is(new Err<>(2)));
        assertThat(test[0], is("ok: Ok(2)"));
        assertThat(test[1], is("err: Err(2)"));
    }

    @Test
    public void toString_() {
        assertThat(new Ok<>(2).toString(), is("Ok(2)"));
        assertThat(new Ok<>("hello, world!").toString(), is("Ok(hello, world!)"));

        assertThat(new Err<>(2).toString(), is("Err(2)"));
        assertThat(new Err<>("hello, world!").toString(), is("Err(hello, world!)"));
        assertThat(new Err<>(new RuntimeException("err")).toString(), is("Err(java.lang.RuntimeException: err)"));
    }

    @Test
    public void hashCode_() {
        Object val = "any";
        assertThat(new Ok<>(val).hashCode(), is(val.hashCode()));
        assertThat(new Err<>(val).hashCode(), is(val.hashCode()));
    }

    @Test
    public void equals() {
        Ok<Object, Integer> ok1 = new Ok<>(1);
        Ok<Object, Integer> ok2 = new Ok<>(2);

        Err<Integer, Object> err1 = new Err<>(1);
        Err<Integer, Object> err2 = new Err<>(2);

        assertThat(ok1, is(equalTo(ok1)));
        assertThat(ok1, is(equalTo(new Ok<>(1))));
        assertThat(ok1, is(not(equalTo(ok2))));
        assertThat(ok1, is(not(equalTo(err1))));

        assertThat(err1, is(equalTo(err1)));
        assertThat(err1, is(equalTo(new Err<>(1))));
        assertThat(err1, is(not(equalTo(err2))));
        assertThat(err1, is(not(equalTo(ok1))));

        assertThat(ok1, is(not(equalTo(null))));
        assertThat(err1, is(not(equalTo(null))));

        assertThat(ok1, is(not(equalTo("Ok(1)"))));
        assertThat(err1, is(not(equalTo("Err(1)"))));
    }

}

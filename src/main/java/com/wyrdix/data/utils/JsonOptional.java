package com.wyrdix.data.utils;

import com.google.gson.JsonElement;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class JsonOptional<T extends JsonElement> {


    private static final JsonOptional<?> EMPTY = new JsonOptional<>(null);

    private final T value;

    private JsonOptional(T value) {
        this.value = value;
    }

    public static <T extends JsonElement> JsonOptional<T> empty() {
        @SuppressWarnings("unchecked")
        JsonOptional<T> t = (JsonOptional<T>) EMPTY;
        return t;
    }

    public static <T extends JsonElement> JsonOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    public static <T extends JsonElement> JsonOptional<T> of(T element) {
        return new JsonOptional<>(element);
    }

    public T orElseGet(Supplier<? extends T> other) {
        return value != null ? value : other.get();
    }

    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public boolean isPresent() {
        return value != null && !value.isJsonNull();
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null)
            consumer.accept(value);
    }

    public JsonOptional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent())
            return this;
        else
            return predicate.test(value) ? this : empty();
    }

    public <U extends JsonElement> JsonOptional<U> mapToJson(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return ofNullable(mapper.apply(value));
        }
    }

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return Optional.empty();
        else {
            return Optional.ofNullable(mapper.apply(value));
        }
    }

    public <U extends JsonElement> JsonOptional<U> flatMap(Function<? super T, JsonOptional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return Objects.requireNonNull(mapper.apply(value));
        }
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }


    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

}

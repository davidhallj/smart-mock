package com.davidhallj.smartmock.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;
import java.util.function.Supplier;

@AllArgsConstructor
public class ExceptionMapping<T extends RuntimeException> {

    @Getter
    private Class<T> cls;
    private Supplier<T> getInstance;
    private Function<RuntimeException, T> runtimeExceptionResolver;

    public T instantiate() {
        return getInstance.get();
    }

    public T wrap(RuntimeException runtimeException) {
        return runtimeExceptionResolver.apply(runtimeException);
    }

}

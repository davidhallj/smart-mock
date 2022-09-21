package com.davidhallj.smartmock.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;
import java.util.function.Supplier;

@AllArgsConstructor
public class ExceptionMapping<T extends RuntimeException> {

    @Getter
    private Class<T> cls;
    private Supplier<T> getInstance;
    private Function<RuntimeException, T> wrapInstance;

    public T instantiate() {
        return getInstance.get();
    }

    public T wrap(RuntimeException runtimeException) {
        return wrapInstance.apply(runtimeException);
    }

}

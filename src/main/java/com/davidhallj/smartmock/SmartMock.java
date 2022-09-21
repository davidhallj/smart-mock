package com.davidhallj.smartmock;

import com.davidhallj.smartmock.config.CacheNamingStrategy;
import com.davidhallj.smartmock.config.CacheWriteStrategy;
import com.davidhallj.smartmock.config.ExecutionStrategy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface SmartMock {

    String url();

    String cacheRootDir() default "cache";

    // TODO wrap all of these settings up into a single 'RunMode' setting
    // Possible values:
    // DEV_MODE: ExecutionStrategy.ALWAYS_USE_REMOTE, CacheWriteStrategy.OFF - allows for fast prototyping against live endpoints during development
    // PERSIST_MODE / CACHE_MODE (work on naming)
    // READ_ONLY mode

    ExecutionStrategy executionStrategy() default ExecutionStrategy.LOCAL_WHEN_AVAILABLE;

    CacheNamingStrategy cacheNamingStrategy() default CacheNamingStrategy.METHOD_SCOPED;

    CacheWriteStrategy cacheWriteStrategy() default CacheWriteStrategy.ON;

}

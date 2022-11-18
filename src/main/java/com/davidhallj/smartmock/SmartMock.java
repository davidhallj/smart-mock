package com.davidhallj.smartmock;

import com.davidhallj.smartmock.config.CacheNamingStrategy;
import com.davidhallj.smartmock.config.CacheWriteStrategy;
import com.davidhallj.smartmock.config.ExecutionStrategy;
import com.davidhallj.smartmock.config.RunConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface SmartMock {

    String url();

    String cacheRootDir() default "cache";

    @Deprecated
    ExecutionStrategy executionStrategy() default ExecutionStrategy.LOCAL_WHEN_AVAILABLE;

    @Deprecated
    CacheNamingStrategy cacheNamingStrategy() default CacheNamingStrategy.METHOD_SCOPED;

    @Deprecated
    CacheWriteStrategy cacheWriteStrategy() default CacheWriteStrategy.ON;

    RunConfig runConfig() default RunConfig.SMART_CACHE_MODE;

    Advanced advanced() default @Advanced;

}

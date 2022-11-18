package com.davidhallj.smartmock.config;

import com.davidhallj.smartmock.core.ExceptionResolver;
import com.davidhallj.smartmock.core.WebExceptionResolver;
import com.davidhallj.smartmock.jaxrs.JaxrsFactory;
import com.davidhallj.smartmock.jaxrs.JaxrsFactoryImpl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Defaults {

    public static final ExceptionResolver EXCEPTION_RESOLVER = new WebExceptionResolver();
    public static final String TEST_RESOURCES_DIR  = "src/test/resources"; // assumes maven
    public static final String CACHE_DIR  = "cache";
    public static final CacheNamingStrategy CACHE_NAMING_STRATEGY = CacheNamingStrategy.METHOD_SCOPED;
    //public static final ExecutionStrategy EXECUTION_STRATEGY = ExecutionStrategy.LOCAL_WHEN_AVAILABLE;
    //public static final CacheWriteStrategy WRITE_STRATEGY = CacheWriteStrategy.ON;
    public static final RunConfig RUN_CONFIG = RunConfig.SMART_CACHE_MODE;

    public static final JaxrsFactory JAXRS_FACTORY = new JaxrsFactoryImpl();

}

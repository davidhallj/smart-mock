package com.davidhallj.smartmock.annotations;

import com.davidhallj.smartmock.config.Defaults;
import com.davidhallj.smartmock.config.advanced.CacheNamingStrategy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface Advanced {

    String resourcesDirectoryPath() default Defaults.MAVEN_TEST_RESOURCES;

    String cacheDirectoryName() default Defaults.CACHE_DIR;

    CacheNamingStrategy cacheNamingStrategy() default CacheNamingStrategy.METHOD_SCOPED;

}

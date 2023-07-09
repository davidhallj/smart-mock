package com.davidhallj.smartmock;

import com.davidhallj.smartmock.config.CacheNamingStrategy;
import com.davidhallj.smartmock.config.Defaults;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface Advanced {

    CacheNamingStrategy cacheNamingStategy() default CacheNamingStrategy.METHOD_SCOPED;
    
    String cacheDirectoryName() default Defaults.CACHE_DIR;
    
    String resourcesDirectorPath() default Defaults.TEST_RESOURCES_DIR;
    
}

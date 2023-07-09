package com.davidhallj.smartmock;

import com.davidhallj.smartmock.config.RunConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface SmartMock {

    String url();

    RunConfig runConfig() default RunConfig.SMART_CACHE_MODE;

    Advanced advanced() default @Advanced;

}

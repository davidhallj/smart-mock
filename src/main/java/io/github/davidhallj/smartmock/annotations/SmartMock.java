package io.github.davidhallj.smartmock.annotations;

import io.github.davidhallj.smartmock.config.RunStrategy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface SmartMock {

    String url();

    RunStrategy runConfig() default RunStrategy.SMART_CACHE_MODE;

    Advanced advanced() default @Advanced;

}

package io.github.davidhallj.smartmock.config;

import io.github.davidhallj.smartmock.config.advanced.CacheNamingStrategy;
import io.github.davidhallj.smartmock.core.SmartMockStaticContext;
import io.github.davidhallj.smartmock.exceptionmapping.ExceptionResolver;
import io.github.davidhallj.smartmock.jaxrs.JaxrsFactory;
import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SmartMockRunConfiguration {

    /**
     * Top level SmartMock params
     */
    private final RunStrategy runStrategy;
    /**
     * Advanced params
     *
     * Question: should these default here or in the annotation?
     */
    private final String testResourceDir;
    private final String cacheDir;
    private final CacheNamingStrategy cacheNamingStrategy;

    /**
     * Experimental params
     * TODO actually add these options ot the @Advanced annotation
     */
    @Builder.Default
    private final JaxrsFactory jaxrsFactory = SmartMockStaticContext.JAXRS_FACTORY;
    @Builder.Default
    private final Gson gson = SmartMockStaticContext.GSON;

    @Builder.Default
    private final boolean cacheExceptions = true;
    @Builder.Default
    private final ExceptionResolver exceptionResolver = Defaults.WEB_EXCEPTION_RESOLVER;

}

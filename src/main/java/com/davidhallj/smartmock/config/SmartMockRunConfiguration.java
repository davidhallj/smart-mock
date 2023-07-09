package com.davidhallj.smartmock.config;

import com.davidhallj.smartmock.config.advanced.CacheNamingStrategy;
import com.davidhallj.smartmock.core.SmartMockStaticContext;
import com.davidhallj.smartmock.jaxrs.JaxrsFactory;
import com.google.gson.Gson;
import lombok.Builder;
import lombok.Getter;


/**
 * TODO allow for extending the builder pattern with 'testMethodName', or 'TestInfo' objects
 */
@Getter
@Builder
public class SmartMockConfiguration {

    //private final SmartMockRuntimeContext runContext;
    //private final SmartMockTestContext testContext;

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


    //// If you instantiate this way, you can only use flow 2) with the factory where you provide more context
    //public static SmartMockConfiguration create(SmartMock smartMock) {
    //
    //    return SmartMockConfiguration.builder()
    //            .runStrategy(smartMock.runConfig())
    //            .testResourceDir(smartMock.advanced().resourcesDirectoryPath())
    //            .cacheDir(smartMock.advanced().cacheDirectoryName())
    //            .cacheNamingStrategy(smartMock.advanced().cacheNamingStrategy())
    //            .build();
    //
    //}

    //
    //public static SmartMockConfiguration create(SmartMock smartMock, Field annotatedField, String testMethodName) {
    //
    //    final SmartMockRuntimeContext smartMockRuntimeContext = SmartMockRuntimeContext.builder()
    //            .url(smartMock.url())
    //            .mockType(annotatedField.getType())
    //            .build();
    //
    //    final SmartMockTestContext smartMockTestContext = SmartMockTestContext.builder()
    //            .testMethodName(testMethodName)
    //            .build();
    //
    //    return SmartMockConfiguration.builder()
    //            .runContext(smartMockRuntimeContext)
    //            .testContext(smartMockTestContext)
    //            .runStrategy(smartMock.runConfig())
    //            .testResourceDir(smartMock.advanced().resourcesDirectoryPath())
    //            .cacheDir(smartMock.advanced().cacheDirectoryName())
    //            .cacheNamingStrategy(smartMock.advanced().cacheNamingStrategy())
    //            .build();
    //}


}

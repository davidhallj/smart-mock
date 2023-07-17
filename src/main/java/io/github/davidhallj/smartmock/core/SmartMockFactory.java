package com.davidhallj.smartmock.core;

import com.davidhallj.smartmock.config.SmartMockConfiguration;
import com.davidhallj.smartmock.config.SmartMockProxyContext;
import com.davidhallj.smartmock.config.SmartMockRunConfiguration;
import com.davidhallj.smartmock.config.SmartMockTestContext;
import com.davidhallj.smartmock.exception.SmartMockException;
import com.davidhallj.smartmock.jaxrs.JaxrsFactory;
import com.davidhallj.smartmock.proxy.SmartCachingInvocationHandler;
import com.davidhallj.smartmock.proxy.SmartMockProxyImpl;
import lombok.Builder;

import java.lang.reflect.InvocationHandler;

public class SmartMockFactory {

    /**
     * Provide the full config object. Use its public createSmartMock method to create the mock
     * using the same static configuration every time
     */
    @Builder
    public static class StaticSmartMockFactory extends SmartMockFactory {

        private final SmartMockConfiguration smartMockConfiguration;

        /**
         * This flow requires all data to be set on the SmartMockConfiguration object
         * The SmartMockAnnotationsProcessor does this flow automatically
         */
        public Object createSmartMock() {

            return SmartMockFactory.createSmartMock(
                    smartMockConfiguration.getRunConfig(),
                    smartMockConfiguration.getProxyContext(),
                    smartMockConfiguration.getTestContext()
            );

        }

    }

    /**
     * Provide a base run configuration. Use the public createSmartMock method to create mocks using the
     * same base run config, but different proxy and test contexts. This is useful if you plan on using
     * the SmartMockFactory pattern directly without using the @SmartMock annotation
     */
    @Builder
    public static class BaseSmartMockFactory extends SmartMockFactory {

        private final SmartMockRunConfiguration config;

        public Object createSmartMock(SmartMockProxyContext smartMockProxyContext) {

            return createSmartMock(
                    smartMockProxyContext,
                    SmartMockTestContext.builder().build()
            );

        }

        public Object createSmartMock(SmartMockProxyContext smartMockProxyContext, SmartMockTestContext smartMockTestContext) {
            return SmartMockFactory.createSmartMock(
                    config,
                    smartMockProxyContext,
                    smartMockTestContext
            );
        }

        @Deprecated
        public <T> T createSmartMock(String uri, Class<T> clazz, String testMethodName) {

            final SmartMockProxyContext smartMockProxyContext = SmartMockProxyContext.builder()
                    .url(uri)
                    .mockType(clazz)
                    .build();

            final SmartMockTestContext smartMockTestContext = SmartMockTestContext.builder()
                    .testMethodName(testMethodName)
                    .build();

            return (T) createSmartMock(smartMockProxyContext, smartMockTestContext);

            //InvocationHandler cachingInvocationHandler = new CachingInvocationHandler(
            //        createRestResource(uri, clazz, config.getJaxrsFactory()),
            //        clazz,
            //        config,
            //        testMethodName
            //);
            //
            //SmartMockProxyImpl<T> smartMockProxy = new SmartMockProxyImpl<>(clazz, cachingInvocationHandler);
            //
            //return smartMockProxy.proxy();
        }

    }


    public static Object createSmartMock(SmartMockConfiguration smartMockConfiguration) {

        final String uri = smartMockConfiguration.getProxyContext().getUrl();
        final Class<?> clazz = smartMockConfiguration.getProxyContext().getMockType();

        if (uri == null || clazz == null) {
            throw new SmartMockException("SmartMockFactory requires a RunContext data to be supplied inside the config object or provided at runtime");
        }

        final Object restResource = createRestResource(uri, clazz, smartMockConfiguration.getRunConfig().getJaxrsFactory());

        InvocationHandler cachingInvocationHandler = new SmartCachingInvocationHandler(
                restResource,
                smartMockConfiguration
        );

        SmartMockProxyImpl smartMockProxy = new SmartMockProxyImpl<>(clazz, cachingInvocationHandler);

        return smartMockProxy.proxy();

    }

    public static Object createSmartMock(SmartMockRunConfiguration smartMockRunConfiguration, SmartMockProxyContext smartMockProxyContext, SmartMockTestContext smartMockTestContext) {
        return createSmartMock(SmartMockConfiguration.builder()
                .runConfig(smartMockRunConfiguration)
                .proxyContext(smartMockProxyContext)
                .testContext(smartMockTestContext)
                .build());

        //final String uri = smartMockProxyContext.getUrl();
        //final Class<?> clazz = smartMockProxyContext.getMockType();
        //
        //if (uri == null || clazz == null) {
        //    throw new SmartMockException("SmartMockFactory requires a RunContext data to be supplied inside the config object or provided at runtime");
        //}
        //
        //final Object restResource = createRestResource(uri, clazz, smartMockRunConfiguration.getJaxrsFactory());
        //
        //InvocationHandler cachingInvocationHandler = new CachingInvocationHandler(
        //        restResource,
        //        clazz,
        //        smartMockRunConfiguration,
        //        smartMockTestContext.getTestMethodName().isPresent() ? smartMockTestContext.getTestMethodName().get() : null
        //);
        //
        //SmartMockProxyImpl smartMockProxy = new SmartMockProxyImpl<>(clazz, cachingInvocationHandler);
        //
        //return smartMockProxy.proxy();

    }

    private static <T> T createRestResource(String uri, Class<T> clazz, JaxrsFactory jaxrsFactory) {
        return (T) jaxrsFactory.createJaxrsProxy(uri, clazz);
    }

    //private <T> T createRestResource(String uri, Class<T> clazz) {
    //    return (T) config.getJaxrsFactory().createJaxrsProxy(uri, clazz);
    //}

}

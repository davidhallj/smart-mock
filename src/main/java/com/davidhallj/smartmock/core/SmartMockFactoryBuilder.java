package com.davidhallj.smartmock.core;

import com.davidhallj.smartmock.config.FullConfigContext;
import com.davidhallj.smartmock.config.SmartMockConfiguration;
import com.davidhallj.smartmock.config.SmartMockRuntimeContext;
import com.davidhallj.smartmock.config.SmartMockTestContext;
import com.davidhallj.smartmock.exception.SmartMockException;
import com.davidhallj.smartmock.jaxrs.JaxrsFactory;
import com.davidhallj.smartmock.proxy.CachingInvocationHandler;
import com.davidhallj.smartmock.proxy.SmartMockProxyImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;


/**
 * TODO need to find a way to make the 2 flows more explicit / different from each other
 * Flow 1) All data is set on the SmartMockConfiguration object
 * Flow 2) Consumer provides the 'RunContext' and 'TestContext' objects
 *
 * TODO make testMethodName optional all the way up at this level
 *
 */
@Slf4j
@AllArgsConstructor
public class SmartMockFactory {

    public static FullConfigSmartMockFactory init(FullConfigContext fullConfigContext) {
        return new FullConfigSmartMockFactory(fullConfigContext);
    }

    public static FullConfigSmartMockFactory init(SmartMockConfiguration smartMockConfiguration, SmartMockRuntimeContext smartMockRuntimeContext, SmartMockTestContext smartMockTestContext) {
        return new FullConfigSmartMockFactory(
                FullConfigContext.builder()
                        .config(smartMockConfiguration)
                        .runContext(smartMockRuntimeContext)
                        .testContext(smartMockTestContext)
                        .build()
        );
    }

    public static HalfConfigSmartMockFactory init(SmartMockConfiguration smartMockConfiguration) {
        return HalfConfigSmartMockFactory.builder()
                .config(smartMockConfiguration)
                .build();
    }

    @Builder
    public static class FullConfigSmartMockFactory extends SmartMockFactory {

        private final FullConfigContext fullConfigContext;

        /**
         * This flow requires all data to be set on the SmartMockConfiguration object
         * The SmartMockAnnotationsProcessor does this flow automatically
         */
        public Object createSmartMock() {

            return createSmartMock(
                    fullConfigContext.getConfig(),
                    fullConfigContext.getRunContext(),
                    // NPE here.. fix
                    fullConfigContext.getTestContext()
            );

        }

    }

    @Builder
    public static class HalfConfigSmartMockFactory extends SmartMockFactory {

        private final SmartMockConfiguration config;

        //public Object createSmartMock(SmartMockRuntimeContext smartMockRuntimeContext) {
        //
        //    return createSmartMock(
        //            smartMockRuntimeContext,
        //            config.getTestContext() == null ? SmartMockTestContext.builder().build() : config.getTestContext()
        //    );
        //
        //}

        public Object createSmartMock(SmartMockRuntimeContext smartMockRuntimeContext, SmartMockTestContext smartMockTestContext) {
            return createSmartMock(
                    config,
                    smartMockRuntimeContext,
                    smartMockTestContext
            );
        }

        public <T> T createSmartMock(String uri, Class<T> clazz, String testMethodName) {

            if (uri == null || clazz == null) {
                throw new SmartMockException("SmartMockFactory requires a RunContext data to be supplied inside the config object or provided at runtime");
            }

            InvocationHandler cachingInvocationHandler = new CachingInvocationHandler(
                    createRestResource(uri, clazz, config.getJaxrsFactory()),
                    clazz,
                    config,
                    testMethodName
            );

            SmartMockProxyImpl<T> smartMockProxy = new SmartMockProxyImpl<>(clazz, cachingInvocationHandler);

            return smartMockProxy.proxy();
        }


    }



    public static Object createSmartMock(SmartMockConfiguration smartMockConfiguration, SmartMockRuntimeContext smartMockRuntimeContext, SmartMockTestContext smartMockTestContext) {

        final String uri = smartMockRuntimeContext.getUrl();
        final Class<?> clazz = smartMockRuntimeContext.getMockType();

        if (uri == null || clazz == null) {
            throw new SmartMockException("SmartMockFactory requires a RunContext data to be supplied inside the config object or provided at runtime");
        }

        final Object restResource = createRestResource(uri, clazz, smartMockConfiguration.getJaxrsFactory());

        InvocationHandler cachingInvocationHandler = new CachingInvocationHandler(
                restResource,
                clazz,
                smartMockConfiguration,
                smartMockTestContext.getTestMethodName().isPresent() ? smartMockTestContext.getTestMethodName().get() : null
        );

        SmartMockProxyImpl smartMockProxy = new SmartMockProxyImpl<>(clazz, cachingInvocationHandler);

        return smartMockProxy.proxy();

    }

    private static <T> T createRestResource(String uri, Class<T> clazz, JaxrsFactory jaxrsFactory) {
        return (T) jaxrsFactory.createJaxrsProxy(uri, clazz);
    }

    //
    //private <T> T createRestResource(String uri, Class<T> clazz) {
    //    return (T) config.getJaxrsFactory().createJaxrsProxy(uri, clazz);
    //}
}

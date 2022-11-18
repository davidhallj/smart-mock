package com.davidhallj.smartmock;

import com.davidhallj.smartmock.config.SmartMockConfiguration;
import com.davidhallj.smartmock.jaxrs.JaxrsFactory;
import com.davidhallj.smartmock.proxy.CachingInvocationHandler2;
import com.davidhallj.smartmock.proxy.SmartMockProxyImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;

@Slf4j
@Getter
@AllArgsConstructor
public class SmartMockFactory2 {

    private final JaxrsFactory jaxrsFactory;
    private final SmartMockConfiguration smartMockConfiguration;

    public <T> T createSmartMock(Class<T> clazz, String testMethodName) {

        final String url = smartMockConfiguration.getUrl();

        return createSmartMock(url, clazz, testMethodName);
    }

    public <T> T createSmartMock(String uri, Class<T> clazz, String testMethodName) {

        InvocationHandler cachingInvocationHandler = new CachingInvocationHandler2<>(
                createRestResource(uri, clazz),
                clazz,
                smartMockConfiguration,
                testMethodName
        );

        SmartMockProxyImpl<T> smartMockProxy = new SmartMockProxyImpl<>(clazz, cachingInvocationHandler);

        return smartMockProxy.proxy();
    }

    private <T> T createRestResource(String uri, Class<T> clazz) {
        return (T) jaxrsFactory.createJaxrsProxy(uri, clazz);
    }
}

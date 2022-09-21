package com.davidhallj.smartmock;

import com.davidhallj.smartmock.config.CacheNamingStrategy;
import com.davidhallj.smartmock.config.CacheWriteStrategy;
import com.davidhallj.smartmock.config.ExceptionResolver;
import com.davidhallj.smartmock.config.ExecutionStrategy;
import com.davidhallj.smartmock.config.WebExceptionResolver;
import com.davidhallj.smartmock.jaxrs.JaxrsFactory;
import com.davidhallj.smartmock.jaxrs.JaxrsFactoryImpl;
import com.davidhallj.smartmock.proxy.CachingInvocationHandler;
import com.davidhallj.smartmock.proxy.SmartMockProxyImpl;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.InvocationHandler;

@Builder
@Getter
public class SmartMockFactory  {

    public static final ExceptionResolver DEFAULT_EXCEPTION_RESOLVER = new WebExceptionResolver();
    public static final String DEFAULT_TEST_RESOURCES_DIR  = "src/test/resources"; // assumes maven
    public static final String DEFAULT_CACHE_DIR  = "cache";
    public static final CacheNamingStrategy DEFAULT_CACHE_NAMING_STRATEGY = CacheNamingStrategy.METHOD_SCOPED;
    public static final ExecutionStrategy DEFAULT_EXECUTION_STRATEGY = ExecutionStrategy.LOCAL_WHEN_AVAILABLE;
    public static final CacheWriteStrategy DEFAULT_WRITE_STRATEGY = CacheWriteStrategy.ON;

    public static final JaxrsFactory DEFAULT_JAXRS_FACTORY = new JaxrsFactoryImpl();


    // TODO could more logically split up these high level class level configs with the runtime
    // level configs that are brought via the 'createSmartMock' method call
    // This would allow you to specify a shared 'class level config'

    @Builder.Default
    private ExceptionResolver exceptionResolver = DEFAULT_EXCEPTION_RESOLVER;
    @Builder.Default
    private String testResourcesDir = DEFAULT_TEST_RESOURCES_DIR;
    @Builder.Default
    private String cacheRootDir = DEFAULT_CACHE_DIR;
    @Builder.Default
    private CacheNamingStrategy cacheNamingStrategy = DEFAULT_CACHE_NAMING_STRATEGY;
    @Builder.Default
    private ExecutionStrategy executionStrategy = DEFAULT_EXECUTION_STRATEGY;
    @Builder.Default
    private CacheWriteStrategy cacheWriteStrategy = DEFAULT_WRITE_STRATEGY;
    @Builder.Default
    private JaxrsFactory jaxrsFactory = DEFAULT_JAXRS_FACTORY;

    public <T> T createSmartMock(String uri, Class<T> clazz) {

        InvocationHandler cachingInvocationHandler = new CachingInvocationHandler<>(
                createRestResource(uri, clazz),
                this.exceptionResolver, this.testResourcesDir, this.cacheRootDir, this.executionStrategy, this.cacheWriteStrategy
        );

        SmartMockProxyImpl<T> smartMockProxy = new SmartMockProxyImpl<>(clazz, cachingInvocationHandler);

        return smartMockProxy.proxy();

    }

    public <T> T createSmartMock(String uri, Class<T> clazz, String methodName) {

        final String resolvedCacheDirectory = resolveCacheDirectory(this.cacheNamingStrategy, clazz, this.cacheRootDir, methodName);

        InvocationHandler cachingInvocationHandler = new CachingInvocationHandler<>(
                createRestResource(uri, clazz),
                this.exceptionResolver, this.testResourcesDir, resolvedCacheDirectory, this.executionStrategy, this.cacheWriteStrategy
        );

        SmartMockProxyImpl<T> smartMockProxy = new SmartMockProxyImpl<>(clazz, cachingInvocationHandler);

        return smartMockProxy.proxy();
    }

    public static String resolveCacheDirectory(CacheNamingStrategy cacheNamingStrategy, Class<?> mockType, String baseCacheDir, String methodName) {

        switch (cacheNamingStrategy) {
            case METHOD_SCOPED:

                if (methodName == null) {
                    throw new RuntimeException("When using CacheNamingStrategy of METHOD_SCOPED, the test must be run using the SmartMockJunitRunner or extend SmartMockTestBase");
                }

                return String.format("%s/%s", baseCacheDir, methodName);
            case MOCK_SCOPED:
                return String.format("%s/%s", baseCacheDir, mockType.getSimpleName());
            case STATIC_SCOPED:
                return baseCacheDir;
            default:
                return baseCacheDir;
        }

    }


    private <T> T createRestResource(String uri, Class<T> clazz) {
        return (T) jaxrsFactory.createJaxrsProxy(uri, clazz);
    }
}

package com.davidhallj.smartmock.proxy;

import com.davidhallj.smartmock.config.CacheWriteStrategy;
import com.davidhallj.smartmock.config.Defaults;
import com.davidhallj.smartmock.config.SmartMockConfiguration;
import com.davidhallj.smartmock.util.CacheHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class CachingInvocationHandler2<T> implements InvocationHandler {

    // TODO make this a singleton and re-use throughout?
    private static Gson gson = new GsonBuilder()
            //.setPrettyPrinting()
            .create();

    private final T realServiceImpl;
    private final Class<T> clazz;
    private final SmartMockConfiguration smartMockConfiguration;
    private final String testMethodName;
    private final CacheHelper cacheHelper;


    private int hits = 0;

    public CachingInvocationHandler2(T realServiceImpl, Class<T> clazz, SmartMockConfiguration smartMockConfiguration, String testMethodName) {
        this.realServiceImpl = realServiceImpl;
        this.clazz = clazz;
        this.smartMockConfiguration = smartMockConfiguration;
        this.testMethodName = testMethodName;
        cacheHelper = new CacheHelper(smartMockConfiguration.getCacheNamingStrategy(), clazz, smartMockConfiguration.getResourcesDirName(), smartMockConfiguration.getCacheDirName(), testMethodName);
    }

    @Override
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
    {
        hits++;
        Object result = null;

        String methodName = m.getName();

        // This solves an issue where the debugger calls toString() on the Proxy (and so we end up caching the response)
        // Could also compare against the list of values of the actual method names on the interface so that we're
        // only intercepting the real methods
        if (m.getName().contains("toString")) {
            return null;
        }

        List<String> argNames = Optional.ofNullable(args).map(Arrays::stream).map(CachingInvocationHandler2::argToString).stream().toList();

        final String cacheFileName = String.format("%s%s.json", methodName, argNames.toString());

        switch (smartMockConfiguration.getRunConfig()) {
            case SMART_CACHE_MODE -> {
                log.info("SMART_CACHE_MODE");

                if (cacheHelper.cacheFileExists(cacheFileName)) {

                    result = cacheHelper.readCacheFile(cacheFileName);

                    if (result == null) {
                        return null;
                    }
                    return gson.fromJson(result.toString(), m.getReturnType());
                } else {

                    m = realServiceImpl.getClass().getMethod(methodName, m.getParameterTypes());

                    // If this is called using the real service, result is implicitly of the correct type
                    try {
                        result = m.invoke(realServiceImpl, args);
                    } catch (InvocationTargetException e) {
                        if (smartMockConfiguration.getRunConfig().getCacheWriteStrategy() == CacheWriteStrategy.ON) {
                            // TODO inject Exception Resolver logic
                            cacheHelper.writeCacheFile(cacheFileName, Defaults.EXCEPTION_RESOLVER.buildExceptionChain(e.getTargetException()));
                        }
                        throw e.getTargetException();
                    }

                    if (smartMockConfiguration.getRunConfig().getCacheWriteStrategy() == CacheWriteStrategy.ON) {
                        cacheHelper.writeCacheFile(cacheFileName, result == null ? "" : gson.toJson(result));
                    }

                    return result;
                }

            }
            case READ_ONLY_MODE -> {
                log.info("READ_ONLY_MODE");
                result = cacheHelper.readCacheFile(cacheFileName);

                if (result == null) {
                    throw new RuntimeException("Running in READ_ONLY mode but the file is not found");
                }

                return gson.fromJson(result.toString(), m.getReturnType());
            }
            case DEV_MODE -> {
                log.info("DEV_MODE");

                m = realServiceImpl.getClass().getMethod(methodName, m.getParameterTypes());
                return m.invoke(realServiceImpl, args);

            }
            default -> {
                // TODO handle this better
                log.info("DEFAULT");
                return null;
            }
        }


    }

    private static String argToString(Object obj) {

        if (obj.getClass().getTypeName().contains("String")) {
            return obj.toString();
        } else {
            // in the case of objects, we don't want to create new files each and every time , so just use the class name
            // so it can be reused. This could cause some issues down the road if you want to test difference inside
            // these object parameters, so this will need to be revisited
            return String.format("[%s]", obj.getClass().getName());
        }

    }


}

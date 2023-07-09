package com.davidhallj.smartmock.proxy;

import com.davidhallj.smartmock.config.Defaults;
import com.davidhallj.smartmock.config.SmartMockRunConfiguration;
import com.davidhallj.smartmock.exception.SmartMockException;
import com.davidhallj.smartmock.util.CacheHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.davidhallj.smartmock.core.SmartMockStaticContext.GSON;

@Slf4j
@AllArgsConstructor
public class CachingInvocationHandler implements InvocationHandler {

    private final Object realServiceImpl;
    private final Class<?> clazz;
    private final SmartMockRunConfiguration smartMockRunConfiguration;
    private final String testMethodName;
    private final CacheHelper cacheHelper;
    private int hits = 0;

    public CachingInvocationHandler(Object realServiceImpl, Class<?> clazz, SmartMockRunConfiguration smartMockRunConfiguration, String testMethodName) {
        this.realServiceImpl = realServiceImpl;
        this.clazz = clazz;
        this.smartMockRunConfiguration = smartMockRunConfiguration;
        this.testMethodName = testMethodName;
        cacheHelper = new CacheHelper(smartMockRunConfiguration.getCacheNamingStrategy(), clazz, smartMockRunConfiguration.getTestResourceDir(), smartMockRunConfiguration.getCacheDir(), testMethodName);
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

        List<String> argNames = Optional.ofNullable(args).map(Arrays::stream).map(CachingInvocationHandler::argToString).stream().toList();

        final String cacheFileName = String.format("%s%s.json", methodName, argNames.toString());

        switch (smartMockRunConfiguration.getRunStrategy()) {
            case SMART_CACHE_MODE -> {
                log.info("SMART_CACHE_MODE");

                if (cacheHelper.cacheFileExists(cacheFileName)) {

                    result = cacheHelper.readCacheFile(cacheFileName);

                    if (result == null) {
                        return null;
                    }
                    return GSON.fromJson(result.toString(), m.getReturnType());
                } else {

                    m = realServiceImpl.getClass().getMethod(methodName, m.getParameterTypes());

                    // If this is called using the real service, result is implicitly of the correct type
                    try {
                        result = m.invoke(realServiceImpl, args);
                    } catch (InvocationTargetException e) {
                        // TODO inject Exception Resolver logic
                        cacheHelper.writeCacheFile(cacheFileName, Defaults.EXCEPTION_RESOLVER.buildExceptionChain(e.getTargetException()));
                        throw e.getTargetException();
                    }

                    cacheHelper.writeCacheFile(cacheFileName, result == null ? "" : GSON.toJson(result));

                    return result;
                }

            }
            case READ_ONLY_MODE -> {
                log.info("READ_ONLY_MODE");
                result = cacheHelper.readCacheFile(cacheFileName);

                if (result == null) {
                    throw new SmartMockException(String.format("Running in READ_ONLY mode but the matching cache file [%s] is not found", cacheFileName));
                }

                return GSON.fromJson(result.toString(), m.getReturnType());
            }
            case DEV_MODE -> {
                log.info("DEV_MODE");

                m = realServiceImpl.getClass().getMethod(methodName, m.getParameterTypes());
                return m.invoke(realServiceImpl, args);

            }
            default -> {
                throw new SmartMockException("Invalid RunConfig");
            }
        }


    }

    private static String argToString(Object obj) {

        // TODO why not just check if the Clas is of type string itself?
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

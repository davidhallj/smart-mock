package com.davidhallj.smartmock.proxy;

import com.davidhallj.smartmock.config.CacheWriteStrategy;
import com.davidhallj.smartmock.config.ExceptionResolver;
import com.davidhallj.smartmock.config.ExecutionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.cxf.helpers.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class CachingInvocationHandler<T> implements InvocationHandler {

    // TODO make this a singleton and re-use throughout?
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private T realServiceImpl;

    // Extracted from config
    private ExceptionResolver exceptionResolver;
    private String testResourcesDir;
    private String rootCacheDir;
    private ExecutionStrategy executionStrategy;
    private CacheWriteStrategy cacheWriteStrategy;

    private Supplier<String> subDirectorySupplier = () -> "myTest";

    private int hits = 0;

    public CachingInvocationHandler(T realServiceImpl, ExceptionResolver exceptionResolver, String testResourcesDir, String rootCacheDir, ExecutionStrategy executionStrategy, CacheWriteStrategy cacheWriteStrategy) {
        this.realServiceImpl = realServiceImpl;
        this.exceptionResolver = exceptionResolver;
        this.testResourcesDir = testResourcesDir;
        this.rootCacheDir = rootCacheDir;
        this.executionStrategy = executionStrategy;
        this.cacheWriteStrategy = cacheWriteStrategy;
    }

    @Override
    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable
    {
        hits++;
        Object result = null;

        String methodName = m.getName();

        // This solves an issue where the debugger calls toString() on the Proxy (and so we end up caching the response)
        // Could also compare against the list of values of the actual method names on the interface
        if (m.getName().contains("toString")) {
            return null;
        }



        //List<String> argNames = args == null ? Collections.emptyList() : Arrays.stream(args).map(CachingInvocationHandler::argToString).toList();
        List<String> argNames = Optional.ofNullable(args).map(Arrays::stream).map(CachingInvocationHandler::argToString).stream().toList();

        String cacheFileName = String.format("%s%s.json", methodName, argNames.toString());

        if (cacheFileExists(cacheFileName) && executionStrategy == ExecutionStrategy.LOCAL_WHEN_AVAILABLE) {
            result = readCacheFile(cacheFileName);

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
                writeCacheFile(cacheFileName, exceptionResolver.buildExceptionChain(e.getTargetException()));
                throw e.getTargetException();
            }

            writeCacheFile(cacheFileName, result == null ? "" : gson.toJson(result));

            return result;
        }

    }


    private void writeCacheFile(String fileName, String content) throws IOException {
        // Check if the cache dir exists at all.
        if (cacheWriteStrategy == CacheWriteStrategy.ON) {
            File cacheDir = new File(getCacheRoot());
            if (!cacheDir.exists()) {
                System.out.println("Cache does not exist. Creating cache directory");
                if (!cacheDir.mkdirs()) {
                    // TODO Better error handling?
                    throw new RuntimeException("Error creating cache directory");
                }
            }

            System.out.println("Write cache file: " + getCacheFile(fileName));
            PrintWriter writer = new PrintWriter(getCacheFile(fileName), "UTF-8");
            writer.println(content);
            writer.close();
        }
    }

    private String readCacheFile(String fileName) {
        System.out.println("Read cache file: " + getCacheFile(fileName));

        String fileContents = FileUtils.getStringFromFile(getCacheFile(fileName)).trim();

        // Try resolving the fileContents to an error
        exceptionResolver.handleException(fileContents);

        return fileContents;

    }

    private String getTestResourcesRoot() {
        File resourcesDirectory = new File(testResourcesDir);
        return resourcesDirectory.getAbsolutePath();
    }

    private String getCacheRoot() {
        return String.format("%s/%s", getTestResourcesRoot(), rootCacheDir);
    }

    private File getCacheFile(String fileName) {
        return new File(String.format("%s/%s", getCacheRoot(), fileName));
    }

    private boolean cacheFileExists(String fileName) {
        File f = getCacheFile(fileName);
        return f.exists() && !f.isDirectory();
    }

    // Main question is how to handle changing state... think a REST interface for CRUD...
    // First get might return null. After posting, the get will now return the posted object.
    // Then an update, then a delete... etc. In this scenario, we need a unique caching strategy
    // to capture these state transitions

    // Could go sequentially... timestamps? Simple numbers?



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

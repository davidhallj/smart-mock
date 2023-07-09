package com.davidhallj.smartmock.util;

import com.davidhallj.smartmock.config.advanced.CacheNamingStrategy;
import com.davidhallj.smartmock.exception.SmartMockException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;

@Slf4j
public class CacheHelper2 implements ICacheHelper {

    private final Path cacheRoot;

    public CacheHelper2(CacheNamingStrategy cacheNamingStrategy, Class<?> mockType, String baseResourcesDirectory, String cacheDirectoryName, String methodName) {

        File resourcesDirectory = new File(baseDirectory);
        final String cacheBaseDirectory = String.format("%s/%s", resourcesDirectory.getAbsolutePath(), cacheDirectoryName);
        final String cacheDirectoryLocation = resolveCacheDirectory(cacheNamingStrategy, mockType, cacheBaseDirectory, methodName);


        this.cacheRoot = resolveCachePath(cacheNamingStrategy, mockType, cacheBaseDirectory, methodName);
    }

    @Override
    public void writeCacheFile(String fileName, String content) {

    }

    @Override
    public String readCacheFile(String fileName) {
        return null;
    }

    @Override
    public boolean cacheFileExists(String fileName) {
        return false;
    }

    private String resolveCacheDirectory(CacheNamingStrategy cacheNamingStrategy, Class<?> mockType, String cacheDirectory, String methodName) {

        switch (cacheNamingStrategy) {
            case METHOD_SCOPED:

                if (methodName == null) {
                    throw new SmartMockException("When using CacheNamingStrategy of METHOD_SCOPED, the test must be run using the SmartMockJunitRunner or extend SmartMockTestBase");
                }

                return String.format("%s/%s", cacheDirectory, methodName);
            case MOCK_SCOPED:
                return String.format("%s/%s", cacheDirectory, mockType.getSimpleName());
            case STATIC_SCOPED:
                return cacheDirectory;
            case TEST_SUITE_SCOPED:
                throw new UnsupportedOperationException("CacheNamingStrategy of TEST_SUITE_SCOPED is not yet implemented");
            default:
                return cacheDirectory;
        }

    }

    private Path resolveCachePath(CacheNamingStrategy cacheNamingStrategy, Class<?> mockType, String baseResourcesDirectory, String methodName) {

        switch (cacheNamingStrategy) {
            case METHOD_SCOPED:

                if (methodName == null) {
                    throw new SmartMockException("When using CacheNamingStrategy of METHOD_SCOPED, the test must be run using the SmartMockJunitRunner or extend SmartMockTestBase");
                }

                return Path.of(baseResourcesDirectory, methodName);
            case MOCK_SCOPED:
                return Path.of(baseResourcesDirectory, mockType.getSimpleName());
            case STATIC_SCOPED:
                return Path.of(baseResourcesDirectory);
            case TEST_SUITE_SCOPED:
                throw new UnsupportedOperationException("CacheNamingStrategy of TEST_SUITE_SCOPED is not yet implemented");
            default:
                return Path.of(baseResourcesDirectory);
        }

    }

}

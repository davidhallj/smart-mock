package io.github.davidhallj.smartmock.util;

import io.github.davidhallj.smartmock.config.SmartMockConfiguration;
import io.github.davidhallj.smartmock.config.advanced.CacheNamingStrategy;
import io.github.davidhallj.smartmock.exception.SmartMockException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

@Slf4j
public class SmartCache {

    private final Path cacheRoot;

    public SmartCache(SmartMockConfiguration config) {
        this.cacheRoot = resolveCachePath(config, config.getProxyContext().getMockType(), config.getTestContext().getTestMethodName());
    }

    public void writeCacheFile(String fileName, String content) throws IOException {

        createCacheDirIfEmpty();

        final Path cacheFilePath = cacheRoot.resolve(fileName);

        log.info("Write cache file: {}", cacheFilePath);

        Files.writeString(
                cacheFilePath,
                content,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    public String readCacheFile(String fileName) throws IOException {
        Path cacheFilePath = cacheRoot.resolve(fileName);

        log.info("Read cache file: {}", cacheFilePath);

        try {

            String fileContents = Files.readString(cacheFilePath);

            // TODO implement error handling
            // Should error handling be in the ache layer or up in the SmartCacheInvocationHandler?
            // Try resolving the fileContents to an error
            //exceptionResolver.handleException(fileContents);

            return fileContents;

        } catch (Exception e) {
            return null;
        }

    }

    public boolean cacheFileExists(String fileName) {
        final Path cacheFilePath = cacheRoot.resolve(fileName);
        return Files.exists(cacheFilePath) && !Files.isDirectory(cacheFilePath);
    }

    private void createCacheDirIfEmpty() {
        if (!Files.exists(cacheRoot)) {
            log.info("Cache does not exist. Creating cache directory");
            if (!cacheRoot.toFile().mkdirs()) {
                throw new SmartMockException("Error creating cache directory");
            }
        }
    }


    private static Path resolveCachePath(SmartMockConfiguration smartMockConfiguration, Class<?> mockType, Optional<String> testMethodName) {

        final CacheNamingStrategy cacheNamingStrategy = smartMockConfiguration.getRunConfig().getCacheNamingStrategy();

        final String baseResourcesDirectory = smartMockConfiguration.getRunConfig().getTestResourceDir();

        switch (cacheNamingStrategy) {
            case METHOD_SCOPED:

                if (testMethodName.isEmpty()) {
                    throw new SmartMockException("When using CacheNamingStrategy of METHOD_SCOPED, the test must be run using the SmartMockJunitRunner or extend SmartMockTestBase");
                }

                return Path.of(baseResourcesDirectory, smartMockConfiguration.getRunConfig().getCacheDir(), testMethodName.get());
            case MOCK_SCOPED:
                return Path.of(baseResourcesDirectory, smartMockConfiguration.getRunConfig().getCacheDir(), mockType.getSimpleName());
            case STATIC_SCOPED:
                return Path.of(baseResourcesDirectory, smartMockConfiguration.getRunConfig().getCacheDir());
            case TEST_SUITE_SCOPED:
                throw new UnsupportedOperationException("CacheNamingStrategy of TEST_SUITE_SCOPED is not yet implemented");
            default:
                return Path.of(baseResourcesDirectory, smartMockConfiguration.getRunConfig().getCacheDir());
        }

    }

}

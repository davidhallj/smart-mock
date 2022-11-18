package com.davidhallj.smartmock.util;

import com.davidhallj.smartmock.config.CacheNamingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class CacheHelper {

    //private final String cacheRoot; // File?
    private final String cacheDirectoryLocation;
    private final File cacheDirectory;

    public CacheHelper(CacheNamingStrategy cacheNamingStrategy, Class<?> mockType, String baseDirectory, String cacheDirectoryName, String methodName) {
        File resourcesDirectory = new File(baseDirectory);
        final String cacheBaseDirectory = String.format("%s/%s", resourcesDirectory.getAbsolutePath(), cacheDirectoryName);
        this.cacheDirectoryLocation = resolveCacheDirectory(cacheNamingStrategy, mockType, cacheBaseDirectory, methodName);
        this.cacheDirectory = new File(cacheDirectoryLocation);
    }

    public void writeCacheFile(String fileName, String content) throws IOException {
        // Check if the cache dir exists at all.
        //File cacheDir = new File(cacheDirectoryLocation);
        createCacheDirIfEmpty();

        log.info("Write cache file: {}", getCacheFile(fileName));
        PrintWriter writer = new PrintWriter(getCacheFile(fileName), "UTF-8");
        writer.println(content);
        writer.close();
    }

    public String readCacheFile(String fileName) {

        File file = getCacheFile(fileName);

        log.info("Read cache file: {}", file);

        //String fileContents = FileUtils.getStringFromFile(getCacheFile(fileName)).trim();

        try {
            byte[] bytesArray = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray);
            fis.close();

            String fileContents = new String(bytesArray);

            // TODO implement
            // Try resolving the fileContents to an error
            //exceptionResolver.handleException(fileContents);

            return fileContents;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public boolean cacheFileExists(String fileName) {
        File f = getCacheFile(fileName);
        return f.exists() && !f.isDirectory();
    }


    private File getCacheFile(String fileName) {
        return new File(String.format("%s/%s", cacheDirectoryLocation, fileName));
    }

    private void createCacheDirIfEmpty() {
        if (!cacheDirectory.exists()) {
            log.info("Cache does not exist. Creating cache directory");
            if (!cacheDirectory.mkdirs()) {
                // TODO Better error handling?
                throw new RuntimeException("Error creating cache directory");
            }
        }
    }

    private String resolveCacheDirectory(CacheNamingStrategy cacheNamingStrategy, Class<?> mockType, String cacheDirectory, String methodName) {

        switch (cacheNamingStrategy) {
            case METHOD_SCOPED:

                if (methodName == null) {
                    throw new RuntimeException("When using CacheNamingStrategy of METHOD_SCOPED, the test must be run using the SmartMockJunitRunner or extend SmartMockTestBase");
                }

                return String.format("%s/%s", cacheDirectory, methodName);
            case MOCK_SCOPED:
                return String.format("%s/%s", cacheDirectory, mockType.getSimpleName());
            case STATIC_SCOPED:
                return cacheDirectory;
            default:
                return cacheDirectory;
        }

    }


}

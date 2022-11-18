package com.davidhallj.smartmock.config;

import com.davidhallj.smartmock.SmartMock;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SmartMockConfiguration {

    /**
     * Top level SmartMock params
     */
    private final String url;
    private final RunConfig runConfig;
    /**
     * Advanced params
     */
    private final CacheNamingStrategy cacheNamingStrategy;
    private final String cacheDirName;
    private final String resourcesDirName;


    public static SmartMockConfiguration create(SmartMock smartMock) {

        // Validations?

        return SmartMockConfiguration.builder()
                .url(smartMock.url())
                .runConfig(smartMock.runConfig())
                .cacheNamingStrategy(smartMock.advanced().cacheNamingStategy())
                .cacheDirName(smartMock.advanced().cacheDirectoryName())
                .resourcesDirName(smartMock.advanced().resourcesDirectorPath())
                .build();
    }


}

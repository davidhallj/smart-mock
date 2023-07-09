package com.davidhallj.smartmock.config;

import com.davidhallj.smartmock.annotations.SmartMock;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Field;

@Getter
@Builder
public class FullConfigContext {

    private final SmartMockRuntimeContext runContext;
    private final SmartMockTestContext testContext;
    private final SmartMockConfiguration config;

    public static FullConfigContext create(SmartMock smartMock, Field annotatedField, String testMethodName) {

        // Validations

        final SmartMockRuntimeContext smartMockRuntimeContext = SmartMockRuntimeContext.builder()
                .url(smartMock.url())
                .mockType(annotatedField.getType())
                .build();

        final SmartMockTestContext smartMockTestContext = SmartMockTestContext.builder()
                .testMethodName(testMethodName)
                .build();

        final SmartMockConfiguration smartMockConfiguration = SmartMockConfiguration.builder()
                .runStrategy(smartMock.runConfig())
                .testResourceDir(smartMock.advanced().resourcesDirectoryPath())
                .cacheDir(smartMock.advanced().cacheDirectoryName())
                .cacheNamingStrategy(smartMock.advanced().cacheNamingStrategy())
                .build();

        return FullConfigContext.builder()
                .runContext(smartMockRuntimeContext)
                .testContext(smartMockTestContext)
                .config(smartMockConfiguration)
                .build();



    }


}

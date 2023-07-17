package io.github.davidhallj.smartmock.config;

import io.github.davidhallj.smartmock.annotations.SmartMock;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Field;

@Getter
@Builder
public class SmartMockConfiguration {

    private final SmartMockRunConfiguration runConfig;
    private final SmartMockProxyContext proxyContext;
    private final SmartMockTestContext testContext;

    public static SmartMockConfiguration create(SmartMock smartMock, Field annotatedField, String testMethodName) {

        // TODO Validations

        final SmartMockProxyContext smartMockProxyContext = SmartMockProxyContext.builder()
                .url(smartMock.url())
                .mockType(annotatedField.getType())
                .build();

        final SmartMockTestContext smartMockTestContext = SmartMockTestContext.builder()
                .testMethodName(testMethodName)
                .build();

        final SmartMockRunConfiguration smartMockRunConfiguration = SmartMockRunConfiguration.builder()
                .runStrategy(smartMock.runConfig())
                .testResourceDir(smartMock.advanced().resourcesDirectoryPath())
                .cacheDir(smartMock.advanced().cacheDirectoryName())
                .cacheNamingStrategy(smartMock.advanced().cacheNamingStrategy())
                .build();

        return SmartMockConfiguration.builder()
                .runConfig(smartMockRunConfiguration)
                .proxyContext(smartMockProxyContext)
                .testContext(smartMockTestContext)
                .build();



    }


}

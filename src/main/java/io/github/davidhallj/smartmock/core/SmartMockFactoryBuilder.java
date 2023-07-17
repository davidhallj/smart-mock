package io.github.davidhallj.smartmock.core;

import io.github.davidhallj.smartmock.config.SmartMockConfiguration;
import io.github.davidhallj.smartmock.config.SmartMockProxyContext;
import io.github.davidhallj.smartmock.config.SmartMockRunConfiguration;
import io.github.davidhallj.smartmock.config.SmartMockTestContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SmartMockFactoryBuilder {

    public static SmartMockFactory.StaticSmartMockFactory init(SmartMockConfiguration smartMockConfiguration) {
        return new SmartMockFactory.StaticSmartMockFactory(smartMockConfiguration);
    }

    public static SmartMockFactory.StaticSmartMockFactory init(SmartMockRunConfiguration smartMockRunConfiguration, SmartMockProxyContext smartMockProxyContext, SmartMockTestContext smartMockTestContext) {
        return new SmartMockFactory.StaticSmartMockFactory(
                SmartMockConfiguration.builder()
                        .runConfig(smartMockRunConfiguration)
                        .proxyContext(smartMockProxyContext)
                        .testContext(smartMockTestContext)
                        .build()
        );
    }

    public static SmartMockFactory.BaseSmartMockFactory init(SmartMockRunConfiguration smartMockRunConfiguration) {
        return SmartMockFactory.BaseSmartMockFactory.builder()
                .config(smartMockRunConfiguration)
                .build();
    }

}

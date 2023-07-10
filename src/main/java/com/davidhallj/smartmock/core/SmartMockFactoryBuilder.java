package com.davidhallj.smartmock.core;

import com.davidhallj.smartmock.config.SmartMockConfiguration;
import com.davidhallj.smartmock.config.SmartMockProxyContext;
import com.davidhallj.smartmock.config.SmartMockRunConfiguration;
import com.davidhallj.smartmock.config.SmartMockTestContext;
import com.davidhallj.smartmock.core.SmartMockFactory.StaticSmartMockFactory;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class SmartMockFactoryBuilder {

    public static StaticSmartMockFactory init(SmartMockConfiguration smartMockConfiguration) {
        return new StaticSmartMockFactory(smartMockConfiguration);
    }

    public static StaticSmartMockFactory init(SmartMockRunConfiguration smartMockRunConfiguration, SmartMockProxyContext smartMockProxyContext, SmartMockTestContext smartMockTestContext) {
        return new StaticSmartMockFactory(
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

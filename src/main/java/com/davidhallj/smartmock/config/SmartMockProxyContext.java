package com.davidhallj.smartmock.config;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SmartMockProxyContext {

    private final String url;
    private final Class<?> mockType;

    @Builder
    public SmartMockProxyContext(String url, Class<?> mockType) {
        this.url = url;
        this.mockType = mockType;
    }

}

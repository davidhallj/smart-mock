package com.davidhallj.smartmock.config;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SmartMockRuntimeContext {

    private final String url;
    // TODO might need to make this more strongly typed using generics
    private final Class<?> mockType;

    @Builder
    public SmartMockRuntimeContext(String url, Class<?> mockType) {
        this.url = url;
        this.mockType = mockType;
    }
}

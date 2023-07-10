package com.davidhallj.smartmock.config;

import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
public class SmartMockTestContext {

    @Builder.Default
    private Optional<String> testMethodName = Optional.empty();

    @Builder
    public SmartMockTestContext(String testMethodName) {
        this.testMethodName = Optional.ofNullable(testMethodName);
    }
    private SmartMockTestContext() {
        this.testMethodName = Optional.empty();
    }

}


package com.davidhallj.smartmock.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SmartMockTestContextTest {

    @Test
    void builderDefaults() {

        SmartMockTestContext smartMockTestContext = SmartMockTestContext.builder().build();

        assertThat(smartMockTestContext.getTestMethodName()).isEmpty();


    }

}
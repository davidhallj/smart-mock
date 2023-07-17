package io.github.davidhallj.smartmock.config;

import io.github.davidhallj.smartmock.config.SmartMockTestContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SmartMockTestContextTest {

    @Test
    void builderDefaults() {

        SmartMockTestContext smartMockTestContext = SmartMockTestContext.builder().build();

        assertThat(smartMockTestContext.getTestMethodName()).isEmpty();


    }

}
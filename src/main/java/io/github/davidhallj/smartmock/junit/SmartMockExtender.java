package io.github.davidhallj.smartmock.junit;

import io.github.davidhallj.smartmock.core.SmartMockAnnotations;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
@NoArgsConstructor
// TODO rename?
public class SmartMockExtender implements BeforeEachCallback, BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        log.info("beforeAll");
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        log.info("beforeEach");
        SmartMockAnnotations.init(extensionContext.getTestInstance().get(), extensionContext.getTestMethod().get().getName());
    }

}

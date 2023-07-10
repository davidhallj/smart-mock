package com.davidhallj.smartmock;

import com.davidhallj.smartmock.annotations.SmartMock;
import com.davidhallj.smartmock.config.Defaults;
import com.davidhallj.smartmock.config.RunStrategy;
import com.davidhallj.smartmock.core.SmartMockStaticContext;
import com.davidhallj.smartmock.jaxrs.Greeting;
import com.davidhallj.smartmock.jaxrs.HelloResource;
import com.davidhallj.smartmock.jaxrs.HelloResourceImpl;
import com.davidhallj.smartmock.junit.SmartMockExtender;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.davidhallj.smartmock.jaxrs.JaxrsTestUtils.buildServerAddress;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ExtendWith(SmartMockExtender.class)
public class DevModeAnnotationTest {


    private static final Path CACHE_ROOT = Path.of(Defaults.MAVEN_TEST_RESOURCES, Defaults.CACHE_DIR);

    @SmartMock(url = "http://0.0.0.0:8181/services/hello", runConfig = RunStrategy.DEV_MODE)
    private HelloResource helloResourceSmartCacheMode;

    @BeforeAll
    public static void classSetup() {
        final HelloResourceImpl impl = new HelloResourceImpl();
        final Server server = SmartMockStaticContext.JAXRS_FACTORY.createJaxrsServer(buildServerAddress("hello"), HelloResource.class, impl);

        // Baseline -> no files in cache
        assertThat(Files.exists(CACHE_ROOT)).isFalse();

    }

    @AfterAll
    public static void classTeardown() {
        assertThat(Files.exists(CACHE_ROOT)).isFalse();
    }

    @Test
    void run() {
        final Greeting greeting1 = helloResourceSmartCacheMode.greet();
        final Greeting greeting2 = helloResourceSmartCacheMode.greet();
        final Greeting greeting3 = helloResourceSmartCacheMode.greet();

        assertThat(greeting1.getId()).isEqualTo(1);
        assertThat(greeting2.getId()).isEqualTo(2);
        assertThat(greeting3.getId()).isEqualTo(3);

    }

    @Test
    @Disabled
    void willThrowServerErrorException() {
        assertThrows(InternalServerErrorException.class, () -> {
            helloResourceSmartCacheMode.willThrowServerErrorException();
        });
    }

    @Test
    @Disabled
    void willThrowBadRequestException() {
        assertThrows(BadRequestException.class, () -> {
            helloResourceSmartCacheMode.willThrowBadRequestException();
        });
    }

}

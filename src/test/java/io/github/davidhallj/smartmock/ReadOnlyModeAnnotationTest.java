package io.github.davidhallj.smartmock;

import io.github.davidhallj.smartmock.annotations.SmartMock;
import io.github.davidhallj.smartmock.config.Defaults;
import io.github.davidhallj.smartmock.config.RunStrategy;
import io.github.davidhallj.smartmock.core.SmartMockStaticContext;
import io.github.davidhallj.smartmock.exception.SmartMockException;
import io.github.davidhallj.smartmock.jaxrs.Greeting;
import io.github.davidhallj.smartmock.jaxrs.HelloResource;
import io.github.davidhallj.smartmock.jaxrs.HelloResourceImpl;
import io.github.davidhallj.smartmock.jaxrs.JaxrsTestUtils;
import io.github.davidhallj.smartmock.junit.SmartMockExtender;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.davidhallj.smartmock.jaxrs.JaxrsTestUtils.buildServerAddress;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@ExtendWith(SmartMockExtender.class)
public class ReadOnlyModeAnnotationTest {

    private static final Path CACHE_ROOT = Path.of(Defaults.MAVEN_TEST_RESOURCES, Defaults.CACHE_DIR);

    @SmartMock(url = "http://0.0.0.0:8181/services/hello", runConfig = RunStrategy.READ_ONLY_MODE)
    private HelloResource helloResourceReadOnlyMode;

    @SmartMock(url = "http://0.0.0.0:8181/services/hello")
    private HelloResource helloResourceSmartCacheMode;

    @BeforeAll
    public static void classSetup() {
        final HelloResourceImpl impl = new HelloResourceImpl();
        final Server server = SmartMockStaticContext.JAXRS_FACTORY.createJaxrsServer(JaxrsTestUtils.buildServerAddress("hello"), HelloResource.class, impl);

        // Baseline -> no files in cache
        assertThat(Files.exists(CACHE_ROOT)).isFalse();


    }

    @AfterAll
    public static void classTeardown() {
        //SmartMockTestUtil.deleteDirectory(CACHE_ROOT);
        //assertThat(Files.exists(CACHE_ROOT)).isFalse();
    }

    @Test
    void run() {

        assertThrows(SmartMockException.class, () -> {
            helloResourceReadOnlyMode.greet();
        });

        // Setup.. run using smartCache mode to ensure the cache is populated
        final Greeting baselineGreeting = helloResourceSmartCacheMode.greet();


        final Greeting greeting1 = helloResourceReadOnlyMode.greet();
        final Greeting greeting2 = helloResourceReadOnlyMode.greet();
        final Greeting greeting3 = helloResourceReadOnlyMode.greet();

        assertThat(greeting1.getId()).isEqualTo(baselineGreeting.getId());
        assertThat(greeting2.getId()).isEqualTo(baselineGreeting.getId());
        assertThat(greeting3.getId()).isEqualTo(baselineGreeting.getId());

    }

    @Test
    void willThrowServerErrorException() {

        assertThrows(SmartMockException.class, () -> {
            helloResourceReadOnlyMode.willThrowServerErrorException();
        });

        assertThrows(InternalServerErrorException.class, () -> {
            helloResourceSmartCacheMode.willThrowServerErrorException();
        });

        assertThrows(InternalServerErrorException.class, () -> {
            helloResourceReadOnlyMode.willThrowServerErrorException();
        });
    }

    @Test
    void willThrowBadRequestException() {

        assertThrows(SmartMockException.class, () -> {
            helloResourceReadOnlyMode.willThrowBadRequestException();
        });

        assertThrows(BadRequestException.class, () -> {
            helloResourceSmartCacheMode.willThrowBadRequestException();
        });

        assertThrows(BadRequestException.class, () -> {
            helloResourceReadOnlyMode.willThrowBadRequestException();
        });
    }

}

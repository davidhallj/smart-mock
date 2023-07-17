package io.github.davidhallj.smartmock;

import io.github.davidhallj.smartmock.config.Defaults;
import io.github.davidhallj.smartmock.config.RunStrategy;
import io.github.davidhallj.smartmock.config.SmartMockRunConfiguration;
import io.github.davidhallj.smartmock.config.SmartMockProxyContext;
import io.github.davidhallj.smartmock.config.SmartMockTestContext;
import io.github.davidhallj.smartmock.config.advanced.CacheNamingStrategy;
import io.github.davidhallj.smartmock.core.SmartMockFactory;
import io.github.davidhallj.smartmock.core.SmartMockFactoryBuilder;
import io.github.davidhallj.smartmock.core.SmartMockStaticContext;
import io.github.davidhallj.smartmock.exception.SmartMockException;
import io.github.davidhallj.smartmock.jaxrs.Greeting;
import io.github.davidhallj.smartmock.jaxrs.HelloResource;
import io.github.davidhallj.smartmock.jaxrs.HelloResourceImpl;
import io.github.davidhallj.smartmock.util.SmartMockTestUtil;
import io.github.davidhallj.smartmock.jaxrs.JaxrsTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.github.davidhallj.smartmock.jaxrs.JaxrsTestUtils.buildServerAddress;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
//@ExtendWith(SmartMockExtender.class)
public class SmartMockTest {

    private static final String TEST_CACHE_DIR = "smartmock-test-cache";
    private static final String READONLY_CACHE_DIR = "readonly-test-cache";

    @BeforeAll
    public static void classSetup() {
        final HelloResourceImpl impl = new HelloResourceImpl();
        final Server server = SmartMockStaticContext.JAXRS_FACTORY.createJaxrsServer(JaxrsTestUtils.buildServerAddress("hello"), HelloResource.class, impl);
    }

    @AfterAll
    public static void classTeardown() {
        SmartMockTestUtil.deleteDirectory(Paths.get(Defaults.MAVEN_TEST_RESOURCES, TEST_CACHE_DIR).toFile());
    }

    @Test
    void smartCacheMode_happyPath(TestInfo testInfo) {

        SmartMockRunConfiguration config = SmartMockRunConfiguration.builder()
                .runStrategy(RunStrategy.SMART_CACHE_MODE)
                .testResourceDir(Defaults.MAVEN_TEST_RESOURCES)
                .cacheDir(TEST_CACHE_DIR)
                .cacheNamingStrategy(Defaults.CACHE_NAMING_STRATEGY)
                .build();

        SmartMockFactory.BaseSmartMockFactory smartMockFactory = SmartMockFactoryBuilder.init(config);

        // Baseline -> no files in cache
        final String testMethodName = testInfo.getTestMethod().get().getName();
        final Path expectedCacheDirectory = Paths.get(Defaults.MAVEN_TEST_RESOURCES, TEST_CACHE_DIR, testMethodName);
        assertThat(Files.exists(expectedCacheDirectory)).isFalse();

        //final HelloResource helloResource = smartMockFactory.createSmartMock(HelloResource.url, HelloResource.class, testMethodName);

        final HelloResource helloResource = (HelloResource) smartMockFactory.createSmartMock(
                SmartMockProxyContext.builder()
                        .url(HelloResource.url)
                        .mockType(HelloResource.class)
                        .build(),
                SmartMockTestContext.builder()
                        .testMethodName(testMethodName)
                        .build()
        );


        final Greeting greeting1 = helloResource.greet();

        assertThat(greeting1).isNotNull();
        assertThat(greeting1.getGreeting()).isEqualTo("Hello world!");

        assertThat(greeting1.getId()).isNotNull();

        // Cache directory was created
        assertThat(Files.exists(expectedCacheDirectory)).isTrue();

        final Greeting greeting2 = helloResource.greet();

        assertThat(greeting2).isNotNull();
        assertThat(greeting2.getGreeting()).isEqualTo("Hello world!");
        // This check ensures that the live service was not called, and that the cached object was returned
        assertThat(greeting2.getId()).isEqualTo(greeting1.getId());

        // Ensure that it is not caching the objects in any way.. new objects are created from same data cache
        assertThat(greeting1).isNotEqualTo(greeting2);

        helloResource.greet();
        helloResource.greet();

        // Cache directory still exists
        assertThat(Files.exists(expectedCacheDirectory)).isTrue();

        SmartMockTestUtil.deleteDirectory(expectedCacheDirectory.toFile());
        //SmartMockTestUtil.deleteDirectory(Paths.get(Defaults.TEST_RESOURCES_DIR, TEST_CACHE_DIR).toFile());
        assertThat(Files.exists(expectedCacheDirectory)).isFalse();

    }


    @Test
    void devMode_happyPath(TestInfo testInfo) {

        SmartMockRunConfiguration config = SmartMockRunConfiguration.builder()
                .runStrategy(RunStrategy.DEV_MODE)
                .testResourceDir(Defaults.MAVEN_TEST_RESOURCES)
                .cacheDir(Defaults.CACHE_DIR)
                .cacheNamingStrategy(Defaults.CACHE_NAMING_STRATEGY)
                .build();

        SmartMockFactory.BaseSmartMockFactory smartMockFactory = SmartMockFactoryBuilder.init(config);

        // Baseline -> no files in cache
        final String expectedCacheDirectoryName = testInfo.getTestMethod().get().getName();
        final Path expectedCacheDirectory = Paths.get(Defaults.MAVEN_TEST_RESOURCES, Defaults.CACHE_DIR, expectedCacheDirectoryName);
        assertThat(Files.exists(expectedCacheDirectory)).isFalse();

        final HelloResource helloResource = smartMockFactory.createSmartMock(HelloResource.url, HelloResource.class, expectedCacheDirectoryName);

        final Greeting greeting1 = helloResource.greet();

        assertThat(greeting1).isNotNull();
        assertThat(greeting1.getGreeting()).isEqualTo("Hello world!");
        assertThat(greeting1.getId()).isEqualTo(1);

        // Cache directory was not created
        assertThat(Files.exists(expectedCacheDirectory)).isFalse();

        final Greeting greeting2 = helloResource.greet();

        assertThat(greeting2).isNotNull();
        assertThat(greeting2.getGreeting()).isEqualTo("Hello world!");
        // This check verifies that the live service was called, because the ID increments
        assertThat(greeting2.getId()).isEqualTo(2);

        // Ensure that it is not caching the objects in any way.. new objects are created from same data cache
        assertThat(greeting1).isNotEqualTo(greeting2);

        helloResource.greet();
        helloResource.greet();

        // Cache directory still exists
        assertThat(Files.exists(expectedCacheDirectory)).isFalse();

        SmartMockTestUtil.deleteDirectory(expectedCacheDirectory.toFile());
        //SmartMockTestUtil.deleteDirectory(Paths.get(Defaults.TEST_RESOURCES_DIR, TEST_CACHE_DIR).toFile());
        assertThat(Files.exists(expectedCacheDirectory)).isFalse();

    }


    @Test
    void readOnlyMode_happyPath(TestInfo testInfo) {

        SmartMockRunConfiguration config = SmartMockRunConfiguration.builder()
                .runStrategy(RunStrategy.SMART_CACHE_MODE)
                .testResourceDir(Defaults.MAVEN_TEST_RESOURCES)
                .cacheDir(READONLY_CACHE_DIR)
                .cacheNamingStrategy(CacheNamingStrategy.STATIC_SCOPED)
                .build();

        SmartMockFactory.BaseSmartMockFactory smartMockFactory = SmartMockFactoryBuilder.init(config);

        // Baseline -> no files in cache
        final String testMethodName = testInfo.getTestMethod().get().getName();
        final Path expectedCacheDirectory = Paths.get(Defaults.MAVEN_TEST_RESOURCES, READONLY_CACHE_DIR);
        assertThat(Files.exists(expectedCacheDirectory)).isTrue();

        final HelloResource readOnlyHelloResource = smartMockFactory.createSmartMock(HelloResource.url, HelloResource.class, testMethodName);

        final Greeting greeting1 = readOnlyHelloResource.greet();

        assertThat(greeting1).isNotNull();
        assertThat(greeting1.getGreeting()).isEqualTo("Hello world!");
        assertThat(greeting1.getId()).isEqualTo(1);

        final Greeting greeting2 = readOnlyHelloResource.greet();

        assertThat(greeting2).isNotNull();
        assertThat(greeting2.getGreeting()).isEqualTo("Hello world!");
        // This check verifies that the live service was called, because the ID increments
        assertThat(greeting2.getId()).isEqualTo(1);

        // Ensure that it is not caching the objects in any way.. new objects are created from same data cache
        assertThat(greeting1).isNotEqualTo(greeting2);

        readOnlyHelloResource.greet();
        readOnlyHelloResource.greet();

        // Cache directory still exists
        assertThat(Files.exists(expectedCacheDirectory)).isTrue();

    }

    @Test
    void readOnlyMode_noCache(TestInfo testInfo) {

        SmartMockRunConfiguration config = SmartMockRunConfiguration.builder()
                .runStrategy(RunStrategy.READ_ONLY_MODE)
                .testResourceDir(Defaults.MAVEN_TEST_RESOURCES)
                .cacheDir("bad-cache-dir")
                .cacheNamingStrategy(Defaults.CACHE_NAMING_STRATEGY)
                .build();

        SmartMockFactory.BaseSmartMockFactory smartMockFactory = SmartMockFactoryBuilder.init(config);

        // Baseline -> no files in cache
        final String expectedCacheDirectoryName = testInfo.getTestMethod().get().getName();
        final Path expectedCacheDirectory = Paths.get(Defaults.MAVEN_TEST_RESOURCES, Defaults.CACHE_DIR, expectedCacheDirectoryName);
        assertThat(Files.exists(expectedCacheDirectory)).isFalse();

        final HelloResource readOnlyHelloResource = smartMockFactory.createSmartMock(HelloResource.url, HelloResource.class, expectedCacheDirectoryName);

        assertThrows(SmartMockException.class, () -> readOnlyHelloResource.greet());

        // Cache directory still exists
        assertThat(Files.exists(expectedCacheDirectory)).isFalse();

        SmartMockTestUtil.deleteDirectory(expectedCacheDirectory.toFile());
        //SmartMockTestUtil.deleteDirectory(Paths.get(Defaults.TEST_RESOURCES_DIR, TEST_CACHE_DIR).toFile());
        assertThat(Files.exists(expectedCacheDirectory)).isFalse();

    }

    /**
     * Run this to setup the file system for the read only tests
     * @param testInfo
     */
    @Test
    @Disabled
    public void createCache(TestInfo testInfo) {

        SmartMockRunConfiguration config = SmartMockRunConfiguration.builder()
                .runStrategy(RunStrategy.SMART_CACHE_MODE)
                .testResourceDir(Defaults.MAVEN_TEST_RESOURCES)
                .cacheDir(READONLY_CACHE_DIR)
                .cacheNamingStrategy(CacheNamingStrategy.STATIC_SCOPED)
                .build();

        SmartMockFactory.BaseSmartMockFactory smartMockFactory = SmartMockFactoryBuilder.init(config);

        // Baseline -> no files in cache
        final String testMethodName = testInfo.getTestMethod().get().getName();
        final Path expectedCacheDirectory = Paths.get(Defaults.MAVEN_TEST_RESOURCES, READONLY_CACHE_DIR);

        final HelloResource helloResource = smartMockFactory.createSmartMock(HelloResource.url, HelloResource.class, testMethodName);

        final Greeting greeting = helloResource.greet();

        assertThat(Files.exists(expectedCacheDirectory)).isTrue();

    }

}

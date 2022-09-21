package com.davidhallj.smartmock;

import com.davidhallj.smartmock.config.CacheWriteStrategy;
import com.davidhallj.smartmock.config.ExecutionStrategy;
import com.davidhallj.smartmock.jaxrs.JaxrsFactory;
import com.davidhallj.smartmock.jaxrs.JaxrsFactoryImpl;
import com.davidhallj.smartmock.junit.SmartMockExtender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.cxf.endpoint.Server;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SmartMockExtender.class)
public class SmartMockTest {

    private static JaxrsFactory jaxrsFactory;

    @SmartMock(url = "http://0.0.0.0:8181/services/hello", executionStrategy = ExecutionStrategy.ALWAYS_USE_REMOTE, cacheWriteStrategy = CacheWriteStrategy.OFF)
    private HelloResource helloResource;

    @BeforeAll
    public static void classSetup() {
        jaxrsFactory = new JaxrsFactoryImpl();
        final HelloResourceImpl impl = new HelloResourceImpl();
        final Server server = jaxrsFactory.createJaxrsServer(buildServerAddress("hello"), HelloResource.class, impl);
    }

    @Test
    public void createServer() {

        Greeting greeting = helloResource.greet();

        assertNotNull(greeting);

        assertEquals("Hello world!", greeting.getGreeting());

    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public interface HelloResource {

        @GET
        @Path("greeting")
        @Consumes(MediaType.APPLICATION_JSON)
        @Produces(MediaType.APPLICATION_JSON)
        Greeting greet();

    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static final class Greeting {

        private final String id;
        private final String greeting;

    }

    public static final class HelloResourceImpl implements HelloResource {

        @Override
        public Greeting greet() {
            return Greeting.builder()
                    .id("1")
                    .greeting("Hello world!")
                    .build();
        }
    }

    // TODO pull into an actual util
    public static String buildServerAddress(String address) {
        return buildServerAddress(address, getPort());
    }

    public static String buildServerAddress(String address, int port) {
        return String.format("http://0.0.0.0:%d/services/%s", port, address);
    }

    public static int getPort() {
        return 8181;
    }

}

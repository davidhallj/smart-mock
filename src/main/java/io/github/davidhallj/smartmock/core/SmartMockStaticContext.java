package io.github.davidhallj.smartmock.core;

import io.github.davidhallj.smartmock.jaxrs.JaxrsFactory;
import io.github.davidhallj.smartmock.jaxrs.JaxrsFactoryImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmartMockStaticContext {

    private SmartMockStaticContext() {
        log.info("SmartMockRunContext constructor");
    }

    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static final JaxrsFactory JAXRS_FACTORY = new JaxrsFactoryImpl();

}

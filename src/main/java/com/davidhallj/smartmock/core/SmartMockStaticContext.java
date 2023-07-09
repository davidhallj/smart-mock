package com.davidhallj.smartmock.core;

import com.davidhallj.smartmock.jaxrs.JaxrsFactory;
import com.davidhallj.smartmock.jaxrs.JaxrsFactoryImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmartMockRunContext {

    private SmartMockRunContext() {
        log.info("SmartMockRunContext constructor");
    }

    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static final JaxrsFactory JAXRS_FACTORY = new JaxrsFactoryImpl();

}

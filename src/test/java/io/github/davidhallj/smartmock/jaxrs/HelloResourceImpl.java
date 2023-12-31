package io.github.davidhallj.smartmock.jaxrs;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ServerErrorException;

@Slf4j
public final class HelloResourceImpl implements HelloResource {

    public HelloResourceImpl() {
        log.info("HelloResourceImpl constructor");
    }

    @Getter
    private int counter = 0;

    @Override
    public Greeting greet() {
        return Greeting.builder()
                .id(++counter)
                .greeting("Hello world!")
                .build();
    }

    @Override
    public void willThrowServerErrorException() {
        throw new ServerErrorException(500);
    }

    @Override
    public void willThrowBadRequestException() {
        throw new BadRequestException("Bad request");
    }
}
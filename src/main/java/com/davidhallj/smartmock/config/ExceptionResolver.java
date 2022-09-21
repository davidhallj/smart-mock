package com.davidhallj.smartmock.config;

public interface ExceptionResolver {

    void handleException(String fileContents);

    String buildExceptionChain(Throwable e);

}

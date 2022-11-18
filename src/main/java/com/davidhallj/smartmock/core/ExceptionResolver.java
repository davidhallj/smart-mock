package com.davidhallj.smartmock.core;

public interface ExceptionResolver {

    void handleException(String fileContents);

    String buildExceptionChain(Throwable e);

}

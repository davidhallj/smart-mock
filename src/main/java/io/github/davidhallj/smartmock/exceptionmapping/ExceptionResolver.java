package io.github.davidhallj.smartmock.exceptionmapping;

public interface ExceptionResolver {

    void handleException(String fileContents);

    String buildExceptionChain(Throwable e);

}

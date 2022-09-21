package com.davidhallj.smartmock.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// This is not the
public class SmartMockProxyImpl<T> implements InvocationHandler {

    private final InvocationHandler invocationHandler;
    private final Class<T> clazz;

    public SmartMockProxyImpl(Class<T> clazz, InvocationHandler invocationHandler) {
        this.clazz = clazz;
        this.invocationHandler = invocationHandler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invocationHandler.invoke(proxy, method, args);
    }

    public T proxy() {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { clazz }, invocationHandler);
    }

}

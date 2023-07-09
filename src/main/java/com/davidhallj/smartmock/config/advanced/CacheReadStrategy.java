package com.davidhallj.smartmock.config;

public enum CacheReadStrategy {
    /**
     * Will never attempt to read from cache
     */
    OFF, // ALWAYS_USE_REMOVE
    /**
     * If cache data is there, it will attempt to read it. If no cache data is found, execution will be
     * allowed to continue and
     */
    SMART, // SMART ?
    /**
     * If no cache data is found, will stop test execution and throw Exception
     */
    ALWAYS
}

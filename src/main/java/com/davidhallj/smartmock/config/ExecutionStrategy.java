package com.davidhallj.smartmock.config;

public enum ExecutionStrategy {
    /**
     * By default this will always write to the cache (and overwrite anything that exists currently)
     * To turn this off, change the CacheWriteStrategy to false
     */
    ALWAYS_USE_REMOTE,
    /**
     * Will use local cache data if it is already there, otherwise it will fallback to making the
     * remote call and attempt to write the data to the cache
     */
    LOCAL_WHEN_AVAILABLE
}

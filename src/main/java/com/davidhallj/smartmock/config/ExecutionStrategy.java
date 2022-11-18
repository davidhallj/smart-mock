package com.davidhallj.smartmock.config;

// CacheReadStrategy?
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
    LOCAL_WHEN_AVAILABLE,
    /**
     * Will only ever read from local cache. This is the absolute safest option to ensure a live
     * service call is never dispatched
     */
    LOCAL_ONLY
}

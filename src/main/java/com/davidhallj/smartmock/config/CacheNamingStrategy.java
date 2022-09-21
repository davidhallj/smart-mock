package com.davidhallj.smartmock.config;

public enum CacheNamingStrategy {
    /**
     * Each test method will create its own sub directory in the main cache,
     * named after itself.
     * Note: requires a hook into the junit framework in order to recieve the current
     * executing test method name. Must run the test class with the SmartMockJunitRunner,
     * or extend from the SmartMockTestBase
     */
    METHOD_SCOPED,
    /**
     * Each SmartMock will write to its own sub directory in the main cache, named after
     * itself
     */
    MOCK_SCOPED,
    /**
     * Each test method will write to the same directory, specified by 'cacheRootDir' config value
     */
    STATIC_SCOPED


}

package com.davidhallj.smartmock.config.advanced;

public enum CacheNamingStrategy {
    /**
     * Each test method will create its own sub directory in the main cache,
     * named after itself.
     *
     * TODO review this note
     * Note: requires a hook into the junit framework in order to receive the current
     * executing test method name. Must run the test class with the SmartMockJunitRunner,
     * or extend from the SmartMockTestBase
     *
     * Each unit test will be isolated to only using its own cache directory
     */
    METHOD_SCOPED,
    /**
     * Each SmartMock instance will write to its own sub directory in the main cache, named after
     * itself
     *
     * Potential to share cached data between different unit tests
     */
    MOCK_SCOPED,
    /**
     * Each test method will write to the same directory, specified by 'cacheRootDir' config value
     */
    STATIC_SCOPED,
    /**
     * TODO need to implement this
     */
    TEST_SUITE_SCOPED


}

package com.davidhallj.smartmock.config;

// TODO
public enum CachingStrategy {
    /**
     *  Will cache each request, even if it has the same parameters.
     *
     *  Order absolutely matters.
     */
    PLAYBACK_MODE,
    /**
     * First request for a given set of parameters gets cached, and that cached file will
     * always be returned
     */
    STATELESS
    //etc
}

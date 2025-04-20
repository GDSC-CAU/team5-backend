package org.gdsccau.team5.safebridge.common.cache;

import lombok.Getter;

@Getter
public enum CacheType {

    TERM_FIND_COUNT("findCount"),
    TERM_FIND_TIME("findTime"),
    HOT_TERM("term");

    private final String cacheName;
    private final Integer expiredAfterWrite;
    private final Integer maximumSize;

    CacheType(final String cacheName) {
        this.cacheName = cacheName;
        this.expiredAfterWrite = 5;
        this.maximumSize = 1000;
    }
}

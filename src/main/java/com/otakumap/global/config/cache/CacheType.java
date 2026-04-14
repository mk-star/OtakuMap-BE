//package com.otakumap.global.config.cache;
//
//import lombok.Getter;
//
//@Getter
//public enum CacheType {
//    POPULAR_VIEWS("views");
//
//    private final String name;
//    private final int expireAfterWrite;
//    private final int maximumSize;
//
//    CacheType(String name) {
//        this.name = name;
//        this.expireAfterWrite = ConstConfig.DEFAULT_TTL_SEC;
//        this.maximumSize = ConstConfig.DEFAULT_MAX_SIZE;
//    }
//
//    static class ConstConfig {
//        static final int DEFAULT_TTL_SEC = 3000;
//        static final int DEFAULT_MAX_SIZE = 10000;
//    }
//}
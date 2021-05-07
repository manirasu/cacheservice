package com.cache.utils;


public class CacheUtil {
    private final static String PROPERTY_FILE = "config.properties";
    public static long getTimeStamp(){
        return System.currentTimeMillis();
    }

    public static long getTimeStamp(long timeToLive) {
        long value =getTimeStamp() + timeToLive;
        System.out.println("bbb: " +value );
        return value;
    }
}
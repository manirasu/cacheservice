package com.cache;

import com.cache.scheduler.GlobalTTL;
import com.cache.utils.CacheUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class JUnit5CacheTest {

    CacheService genericCache = CacheService.getInstance();
    @Test
    public void testCreateCacheConfigurationAndCheckPropertyValue() {
       System.out.println("testCreateCacheConfigurationAndCheckPropertyValue");
        genericCache.setCacheTimeout(100);
        genericCache.setMaxCacheSize(100);
        assertEquals(100,((CacheService) genericCache).getCacheTimeout(),"Pass");
        assertEquals(100,((CacheService) genericCache).getMaxCacheSize(),"Pass");
        System.out.println("**************************************************************");
    }


    @Test
    public void testPutWithTTL() {
        System.out.println("==============testPutWithTTL================");
        try {
            genericCache.clearCache();
            String key = "key1";
            String value = "test";
            long timeToLive = 10000;
            genericCache.put(key, value, timeToLive);
            assertEquals(1,((CacheService) genericCache).getMapSize(),"Pass");
            assertEquals(value, genericCache.get(key));
            Thread.sleep(timeToLive+10000);
            assertEquals(0,((CacheService) genericCache).getMapSize(),"Pass");
            assertNotEquals(genericCache.get(key), value);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("**************************************************************");
    }

    @Test
    public void testPutWithDefaultTTL() {
        System.out.println("============testPutWithDefaultTTL================");
        try {
            genericCache.clearCache();
            String key = "key1";
            String value = "test";
            genericCache.put(key, value);
            assertEquals(value, genericCache.get(key));
            assertEquals(1,((CacheService) genericCache).getMapSize(),"Pass");
            Thread.sleep(10000);
            assertEquals(1,((CacheService) genericCache).getMapSize(),"Pass");
            assertEquals(genericCache.get(key), value);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("**************************************************************");
    }

    /**
     * Test of get method, of class Cache.
     */
    @Test
    public void testGet() {
        System.out.println("===============testGet==========================");
        String key = "key2";
        String value = "test2";
        long timeToLive = 10000;
        genericCache.put(key, value, timeToLive);
        genericCache.put("dob", "24sep", timeToLive);
        assertEquals(value, genericCache.get(key));
        assertNotEquals(genericCache.get("dob"), value);
        assertEquals("24sep", genericCache.get("dob"));
        System.out.println("**************************************************************");
    }

    /**
     * Test of delete method, of class Cache.
     */
    @Test
    public void testDelete() {
        System.out.println("===============testDelete==========================");
        String key = "M";
        String value = "Mani";
        long timeToLive = 10000;

        genericCache.put(key, value, timeToLive);
        assertEquals(value, genericCache.get(key));
        genericCache.delete(key);
        assertNotEquals(genericCache.get(key), value);
        System.out.println("**************************************************************");
    }

     /**
     * Test Eviction based on the TTL
     */
    @Test
    public void testEvictionBasedOnTTL() {
        System.out.println("===============testEvictionBasedOnTTL==========================");
        genericCache.clearCache();

        try {
            genericCache.put("M", "Mani", 30000);
            genericCache.put("R", "Maran", 50000);
            genericCache.put("R","test",1000);
            Thread.sleep(1500);
            assertEquals(2,((CacheService) genericCache).getMapSize(),"Pass");
            assertEquals("Mani", genericCache.get("M"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("**************************************************************");
    }

    /**
     * Test max cache size
     */
    @Test
    public void testMaxCacheSize() {
        System.out.println("===============testMaxCacheSize==========================");
        genericCache.setMaxCacheSize(10);
        try {
            for (int i = 1; i <=12;i++) {
                String key = "name" + String.valueOf(i);
                long ttl = 5000000;
                System.out.println("Put at " + CacheUtil.getTimeStamp() + ": " + key + " for eviction at - " + CacheUtil.getTimeStamp(ttl));
                genericCache.put(key, key, ttl);

            }
            assertEquals(10,((CacheService) genericCache).getMapSize(),"Pass");

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("**************************************************************");
    }

    @Test
    public void testEvictionBasedOnGlobalTimeOut() {
        System.out.println("===============testEvictionBasedOnGlobalTimeOut==========================");
        genericCache.setCacheTimeout(20000);
        genericCache.clearCache();

        try {
            for (int i = 1; i <= 5; i++) {
                String key = "name" + String.valueOf(i);
                long ttl = 5000000;
                System.out.println("Put at " + CacheUtil.getTimeStamp() + ": " + key + " for eviction at - " + CacheUtil.getTimeStamp(ttl));
                genericCache.put(key, key, ttl);
            }
            Thread.sleep(25000);
            assertEquals(0, ((CacheService) genericCache).getMapSize(), "Pass");
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 1; i <= 5; i++) {
            String key = "name" + String.valueOf(i);
            long ttl = 5000000;
            System.out.println("Put at " + CacheUtil.getTimeStamp() + ": " + key + " for eviction at - " + CacheUtil.getTimeStamp(ttl));
            genericCache.put(key, key, ttl);
        }
        assertEquals(5, ((CacheService) genericCache).getMapSize(), "Pass");
        System.out.println("**************************************************************");

    }
}

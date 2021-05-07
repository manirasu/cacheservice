package com.cache;

public interface GenericCache<K, V> {

    void start();

    void stop();

    public V get(K key);

    boolean  put(K key, V value);

    boolean put(K key, V value, long ttl);

    void delete(K key);

    void clearCache();

    boolean containsKey(K key);

    public long getExpireTime(K key);

}

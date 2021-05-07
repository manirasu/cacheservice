package com.cache;

import com.cache.errors.ErrorMessage;
import com.cache.utils.Constants;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class DataStore<K, V> {
    static Logger log = Logger.getLogger(DataStore.class.getName());
    private final ConcurrentHashMap<K, ValueNode> cacheMap;

    class ValueNode{
        public V Value;
        public long TimeStamp;
        public ValueNode(V value, long timeStamp){
            Value = value;
            TimeStamp = timeStamp;
        }
    }

    public DataStore(){
        this.cacheMap = new ConcurrentHashMap<>();
    }

    public boolean put(K key, V value, long timeStamp, int maxCacheSize){
        if(cacheMap.size() < maxCacheSize ) {
            cacheMap.put(key, new ValueNode(value, timeStamp));
            return true;
        } else {
            log.info ("cache map reached maximum heap size:: " +cacheMap.size());
            new ErrorMessage(Constants.MAXCACHESIZE);
            return false;
        }
    }

    public void remove(K key){
        log.info("To remove key :" +key + " from cache map");
        cacheMap.remove(key);
    }

    public V get(K key) {
        return cacheMap.containsKey(key) ? cacheMap.get(key).Value : null;
    }

    public long getExpireTime(K key){
        return cacheMap.containsKey(key)? cacheMap.get(key).TimeStamp: Long.MAX_VALUE;
    }

    public boolean containsKey(K key) {
        return cacheMap.containsKey(key) ? true : false;
    }

    public void clear() {
        cacheMap.clear();
    }

    public ConcurrentHashMap<K, ValueNode> getCacheMap() {
        return cacheMap;
    }
}

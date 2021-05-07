package com.cache;

import com.cache.scheduler.GlobalTTL;
import com.cache.utils.CacheUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Properties;

public class CacheService <K,V>  implements GenericCache<K,V>  {
    static Logger log = Logger.getLogger(CacheService.class.getName());
    private static volatile CacheService cacheService  = null;
    private final static String PROPERTY_FILE = "cache.properties";
    private final long  DEFAULT_VALUE = ((long)1)*(365*24*60*60*1000);

    private long cacheTimeout;

    private int maxCacheSize;

    private DataStore<K, V> dataStore;
    private boolean isEviction = false;
    private long evictionPeriod = 1;
    private Properties properties = null;

    class DataNode {

        public long timeStamp;
        public K key;

        public DataNode(long timeStamp, K key) {
            this.timeStamp = timeStamp;
            this.key = key;
        }
    }
    private CacheService() {
        loadProperties();
        cacheTimeout = cacheTimeout();
        maxCacheSize = cacheSize();
        this.dataStore = new DataStore();
        new GlobalTTL(this);
        start();
    }

    public long getCacheTimeout() {
        return  cacheTimeout;
    }

    public int getMaxCacheSize() {
        return  maxCacheSize;
    }

    public void setCacheTimeout(long cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
    }

    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    public static CacheService getInstance()
    {
        if (cacheService == null)
        {
            // To make thread safe
            synchronized (CacheService.class)
            {
                // check again as multiple threads
                // can reach above step
                if (cacheService==null)
                    cacheService = new CacheService();
            }
        }
        return cacheService;
    }

    @Override
    public V get(K key) {
        return dataStore.get(key);
    }

    @Override
    public boolean put(K key,V value) {
        return put(key,value,0);
    }

    /**
     *
     * @param key
     * @param value
     * @param timeToLive
     * @return  boolean
     * insert the record in to the cache and PriorityQueue based on the maxCache size.if timetoLive is is default value means, it will be available one year
     * and based on global ttl.
     */
    @Override
    public boolean put(K key, V value,long timeToLive) {
        timeToLive = timeToLive != 0 ? timeToLive : DEFAULT_VALUE;
        long timeStamp = CacheUtil.getTimeStamp(timeToLive);
        synchronized (timeStampKeyList) {
            boolean inserted =  dataStore.put(key, value, timeStamp,maxCacheSize);
            if(inserted) {
                timeStampKeyList.add(new DataNode(timeStamp, key));
                return true;
            }
            return false;
        }
    }

    /**
     * To monitor the cache based on the TTL
     */
    @Override
    public synchronized void start() {
        if (!isEviction) {
            this.isEviction = true;
            startEvictionService();
        }
    }

    /**
     * Based on the TTL timestamp.
     */
    private PriorityQueue<DataNode> timeStampKeyList = new PriorityQueue<>(new Comparator<DataNode>() {
        @Override
        public int compare(DataNode o1, DataNode o2) {
            return (int) (o1.timeStamp - o2.timeStamp);
        }
    });

    @Override
    public void delete (K key) {
        dataStore.remove(key);
    }

    public int getMapSize() {
        return dataStore.getCacheMap().size();
    }

    /**
     * Clear the cache based on the Global TTL
     */

    public void clearCache() {
       log.info("Invoking clear cache method from Global TTL scheduler");
        dataStore.clear();
        stop();
        timeStampKeyList.clear();
        log.info("Cleared data store Map and time stamp key list ");
    }

    @Override
    public synchronized void stop() {
        this.isEviction = false;
    }


    @Override
    public boolean containsKey(K key) {
        return this.dataStore.containsKey(key);
    }

    @Override
    public long getExpireTime(K key) {
        return dataStore.getExpireTime(key);
    }


    private void startEvictionService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isEviction) {
                    synchronized (timeStampKeyList) {
                        evictData();
                    }
                    try {
                        Thread.sleep(evictionPeriod);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }).start();
    }

    /**
     * To removed to cache value based on the TTL.
     */

    private void evictData() {
        while (timeStampKeyList.isEmpty() == false
                && timeStampKeyList.peek().timeStamp <= CacheUtil.getTimeStamp()) {
            DataNode currNode = timeStampKeyList.poll();
            dataStore.remove(currNode.key);
        }
    }

    private void loadProperties() {
        InputStream iStream = null;
        properties = new Properties();
        try {

            iStream = this.getClass().getClassLoader()
                    .getResourceAsStream(PROPERTY_FILE);
            if(iStream == null){
                throw new IOException("File not found");
            }
            properties.load(iStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(iStream != null){
                    iStream.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private long cacheTimeout() {
        long timout =0;
        try {
            String cachetimeoutVal = properties.getProperty("cache.globalTTL");
            if (cachetimeoutVal != null && !cachetimeoutVal.isEmpty()) {
                timout = Long.parseLong(cachetimeoutVal);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("cache.globalTTL :" +timout);
        return timout;
    }

    private int cacheSize() {
        int size =0;
        try {
            String cacheSize = properties.getProperty("cache.maxcachesize");
            if (cacheSize != null && !cacheSize.isEmpty()) {
                size = Integer.parseInt(cacheSize);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("cache.maxcachesize :" +size);
        return size;
    }
}


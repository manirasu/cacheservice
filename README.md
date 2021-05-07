# Cache
Java implementation of cache service
This project contain thread safe implementation of in-memory generic cahce.
# Eviction Stratagey
1. TTL Cache
  TimeToLive cache works by evicting those data whose time to live has expired after regular interval of time. 
2. Global Cache
  Gloabl cache clear the cache based on the global TTL value.

# Usage
Usage of the code can be seen in CacheService class which is creating a Singleton wrapper 

    public class CacheService <K,V>  implements GenericCache<K,V>  {
        private static Cache mInstance = new Cache();

        private CacheService() {
        loadProperties();
        cacheTimeout = cacheTimeout();
        maxCacheSize = cacheSize();
        this.dataStore = new DataStore();
        new GlobalTTL(this);
        start();
    }

            ......
    }

   

Build :
=======
mvn package

output:
=======
It will generate <cacheservice.jar> jar file in System.getProperty("user.dir")/target directory.


Example :
=========
   How to invoke the CacheService
   
   public class TestProgram {
    public static void main(String s[]) {
        CacheService genericCache = CacheService.getInstance();

        genericCache.put("mani","123",1000);
        genericCache.put("JK","567",200);
        genericCache.put("1","text",2000);


        System.out.println(genericCache.get("JK"));
        System.out.println(genericCache.get("1"));

    }
}

Note :can able to override cache.properties.files
sample:
cache.maxcachesize=5
cache.globalTTL=5000000

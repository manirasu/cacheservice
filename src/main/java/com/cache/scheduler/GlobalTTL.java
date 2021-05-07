package com.cache.scheduler;

import com.cache.CacheService;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GlobalTTL  {
    static Logger log = Logger.getLogger(GlobalTTL.class.getName());
    private static CacheService cache;

    public GlobalTTL(CacheService cache) {
        this.cache = cache;
        ScheduledExecutorService executorService;
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(GlobalTTL::run ,0,cache.getCacheTimeout(), TimeUnit.MILLISECONDS);
    }
    private static void run() {
        log.info("Invoking Global TTL run method");
        cache.clearCache();
    }
}

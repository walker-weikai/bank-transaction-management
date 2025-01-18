package com.hometask.wk.management.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * @author: weikai
 */
@EnableCaching
@Configuration
public class CacheConfig {

    /**
     * 缓存类型
     */
    public static final String CaffeineCacheSingle = "CaffeineCacheSingle";//本地缓存 单值
    public static final String CaffeineCacheBatch = "CaffeineCacheBatch";//本地缓存 多值

    /**
     * 缓存过期时间
     * Expire以此为前缀 整数类型对应单位分钟-转字符串
     */
//    public static final String ExpireMinutes1 = 1 + "";
//    public static final String ExpireMinutes5 = 5 + "";
//    public static final String ExpireMinutes15 = 15 + "";
    public static final String ExpireMinutes30 = 30 + "";
//    public static final String ExpireHours1 = 60 + "";
    public static final String ExpireHours2 = 2 * 60 + "";
//    public static final String ExpireHours8 = 8 * 60 + "";
//    public static final String ExpireHours12 = 12 * 60 + "";
//    public static final String ExpireDays1 = 24 * 60 + "";
//    public static final String ExpireDays2 = 2 * 24 * 60 + "";


    @Primary
    @Bean(name = CaffeineCacheSingle)
    public CacheManager caffeineCacheSingleManager() throws Exception {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = new ArrayList<>();
        Field[] declaredFields = CacheConfig.class.getDeclaredFields();
        for (Field field : declaredFields) {
            String name = field.getName();
            if (field.getType() == String.class && StringUtils.startsWith(name, "Expire")) {
                caches.add(new CaffeineCache((String) field.get(null), Caffeine.newBuilder().expireAfterWrite(NumberUtils.toInt(Objects.toString(field.get(null))), TimeUnit.MINUTES).build()));
            }
        }
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    @Bean(name = CaffeineCacheBatch)
    public CacheManager caffeineCacheBatchManager() throws Exception {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = new ArrayList<>();
        Field[] declaredFields = CacheConfig.class.getDeclaredFields();
        for (Field field : declaredFields) {
            String name = field.getName();
            if (field.getType() == String.class && StringUtils.startsWith(name, "Expire")) {
                caches.add(new CaffeineCache((String) field.get(null), Caffeine.newBuilder().expireAfterWrite(NumberUtils.toInt(Objects.toString(field.get(null))), TimeUnit.MINUTES).build()));
            }
        }
        cacheManager.setCaches(caches);
        return cacheManager;
    }

}

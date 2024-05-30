package com.subsystem.module.cache;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.subsystem.Constants;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CaffeineCacheModule {
    CacheManager cacheManager;

    /**
     * 根据缓存类型获取对应的缓存
     * 这里根据业务需要 获取LoadingCache，因为这个缓存实现了 批量存入操作
     */
    public LoadingCache getLoadingCache(String cacheType) {
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache(cacheType);
        return (LoadingCache) cache.getNativeCache();
    }
}

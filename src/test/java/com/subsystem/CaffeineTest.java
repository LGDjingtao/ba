package com.subsystem;

import com.github.benmanes.caffeine.cache.*;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CaffeineTest {
    //@Test
    public void cacheTest() {
        // 初始化缓存，设置了1分钟的写过期，100的缓存最大个数
        Cache<Integer, Integer> cache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
        int key1 = 1;
        // 使用getIfPresent方法从缓存中获取值。如果缓存中不存指定的值，则方法将返回 null：
        System.out.println(cache.getIfPresent(key1));

        // 也可以使用 get 方法获取值，该方法将一个参数为 key 的 Function 作为参数传入。如果缓存中不存在该 key
        // 则该函数将用于提供默认值，该值在计算后插入缓存中：
        System.out.println(cache.get(key1, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return 2;
            }
        }));

        // 校验key1对应的value是否插入缓存中
        System.out.println(cache.getIfPresent(key1));

        // 手动put数据填充缓存中
        int value1 = 2;
        cache.put(key1, value1);

        // 使用getIfPresent方法从缓存中获取值。如果缓存中不存指定的值，则方法将返回 null：
        System.out.println(cache.getIfPresent(1));

        // 移除数据，让数据失效
        cache.invalidate(1);
        System.out.println(cache.getIfPresent(1));
    }

    /**
     * 模拟从数据库中读取key
     *
     * @param key
     * @return
     */
    private int getInDB(int key) {
        return key + 1;
    }

    //@Test
    public void loadingCacheTest() {
        // 初始化缓存，设置了1分钟的写过期，100的缓存最大个数
        LoadingCache<Integer, Integer> cache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) {
                        return getInDB(key);
                    }
                });

        int key1 = 1;
        // get数据，取不到则从数据库中读取相关数据，该值也会插入缓存中：
        Integer value1 = cache.get(key1);

        System.out.println(value1);
        // 支持直接get一组值，支持批量查找
        Map<Integer, Integer> dataMap
                = cache.getAll(Arrays.asList(1, 2, 3));
        System.out.println(dataMap);
    }

    //@Test
    public void asyncCacheTest() throws ExecutionException, InterruptedException {
        // 使用executor设置线程池
        AsyncCache<Integer, Integer> asyncCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(100).executor(Executors.newSingleThreadExecutor()).buildAsync();
        Integer key = 1;
        // get返回的是CompletableFuture
//        CompletableFuture<Integer> future = asyncCache.get(key, new Function<Integer, Integer>() {
//            @Override
//            public Integer apply(Integer key) {
//                // 执行所在的线程不在是main，而是ForkJoinPool线程池提供的线程
//                System.out.println("当前所在线程：" + Thread.currentThread().getName());
//                int value = getInDB(key);
//                return value;
//            }
//        });

        CompletableFuture<Integer> future2 = asyncCache.get(key, (tao) -> {
            System.out.println("当前所在线程：" + Thread.currentThread().getName());
            int value = getInDB(tao);
            return value;
        });

        int value = future2.get();
        int value2 = future2.get();
        System.out.println("当前所在线程：" + Thread.currentThread().getName());
        System.out.println(value);
        System.out.println(value2);

    }

    public static void main(String[] args) {
//        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
//        map.put("2", "1");
//        //不存在就不计算 存在就用后面的再次计算
//        String s = map.computeIfPresent("1", (v,k) -> k+"2");
//        System.out.println(s);
//        String s1 = map.get("2");
//        System.out.println(s1);
//        //存在就不计算 ， 不存在就用后面的
//        map.computeIfAbsent("32",v->"22");
//        String s2 = map.get("32");
//        System.out.println(s2);

//        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
//        map.put("1", "1");
//        //不存在就不计算 存在就用后面的再次计算
//        String s = map.computeIfPresent("1", (v,k) -> null);
//        System.out.println(s);
//        String s1 = map.get("2");
//        System.out.println(s1);
//        //存在就不计算 ， 不存在就用后面的
//        map.computeIfAbsent("32",v->"22");
//        String s2 = map.get("32");
//        System.out.println(s2);
        String strip = "";
        String trim = strip.trim();
        if (null == trim || "".equals(trim)){
            System.out.println(trim);
        }
    }


}

package com.subsystem;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.subsystem.module.cache.CaffeineCacheModule;
import com.subsystem.module.cache.TestCa;
import com.subsystem.module.cache.User;
import com.subsystem.module.redis.StringRedisModule;
import com.subsystem.module.staticdata.SubSystemStaticDataDefaultModule;
import junit.framework.TestCase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Unit test for simple App.
 */
@SpringBootTest
public class SubSystemApplicationTest extends TestCase {
    @Autowired
    CacheManager cacheManager;
    @Autowired
    private StringRedisModule stringRedisModule;
    @Autowired
    SubSystemStaticDataDefaultModule subSystemStaticDataDefaultModule;
    @Autowired
    CaffeineCacheModule caffeineCacheModule;
    @Resource
    TestCa testCa;

    @Test
    public void test() {

    }

    /**
     * 缓存1的读写以及过期策列 测试
     */
    @Test
    public void cache1Test() throws InterruptedException {
        testCa.saveUser(new User(1, 20, "taotao"));
        User user1 = testCa.getUser(1);
        System.out.println(user1);
        Thread.sleep(1000);
        User user2 = testCa.getUser(1);
        System.out.println(user2);
        Thread.sleep(1000);
        User user3 = testCa.getUser(1);
        System.out.println(user3);
        Thread.sleep(1000);
        User user4 = testCa.getUser(1);
        System.out.println(user4);
        Thread.sleep(1000);
        User user5 = testCa.getUser(1);
        System.out.println(user5);
        Thread.sleep(1000);
        User user6 = testCa.getUser(1);
        System.out.println(user6);
        Thread.sleep(1000);
        User user7 = testCa.getUser(1);
        System.out.println(user7);
        Thread.sleep(1000);
        User user8 = testCa.getUser(1);
        System.out.println(user8);
    }


    /**
     * 缓存2的读写以及过期策列 测试
     */
    @Test
    public void cache2Test() throws InterruptedException {
        testCa.saveUser2(new User(1, 20, "taotao"));
        User user1 = testCa.getUser2(2);
        System.out.println(user1);
        Thread.sleep(1000);
        System.out.println(testCa.getUser2(2));
        Thread.sleep(1000);
        System.out.println(testCa.getUser2(2));
        Thread.sleep(1000);
        System.out.println(testCa.getUser2(2));
        Thread.sleep(1000);
        System.out.println(testCa.getUser2(2));
        Thread.sleep(1000);
        System.out.println(testCa.getUser2(2));
        Thread.sleep(1000);
        System.out.println(testCa.getUser2(2));
        Thread.sleep(1000);
        System.out.println(testCa.getUser2(2));
    }


    /**
     * StringRedisTemplate 常用 get set 测试
     */
    @Test
    public void redisGetSetTest() {
        stringRedisModule.set("com.taotao", JSON.toJSONString(new User(1, 20, "taotao")));
        String s = stringRedisModule.get("com.taotao");
        System.out.println(s);
    }

    /**
     * StringRedisTemplate 管道流 测试
     */
    @Test
    public void redisPipelinedTest() {
        List<String> modelKeys = subSystemStaticDataDefaultModule.getAllModelKeys();
        List<Object> baseData = stringRedisModule.batchGetByPipelined(modelKeys);

        List<Object> filterData = baseData.stream().map(v -> null == v ? Constants.EMPTY_JSON_OBJ : v).collect(Collectors.toList());

        Map<String, Object> result = Stream.iterate(0, n -> n + 1)
                .limit(modelKeys.size())
                .collect(Collectors.toMap(modelKeys::get, filterData::get));

        LoadingCache cache = caffeineCacheModule.getLoadingCache(Constants.SYNCHRONIZE_REDIS);
        cache.putAll(result);
        System.out.println("asdas");
    }
}

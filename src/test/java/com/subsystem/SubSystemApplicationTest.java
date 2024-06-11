package com.subsystem;

import com.alibaba.fastjson.JSON;
import com.subsystem.core.common.Constants;
import com.subsystem.core.event.LinkageEvent;
import com.subsystem.core.module.SubSystemDefaultContext;
import com.subsystem.core.module.cache.CaffeineCacheModule;
import com.subsystem.core.module.redis.StringRedisModule;
import com.subsystem.core.module.staticdata.SubSystemStaticDataDefaultModule;
import com.subsystem.core.repository.RepositoryModule;
import com.subsystem.core.repository.mapping.AlarmInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Unit test for simple App.
 */
@SpringBootTest
public class SubSystemApplicationTest {
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
    private final static ConcurrentHashMap<String, ConcurrentHashMap<String, ScheduledExecutorService>> map = new ConcurrentHashMap();

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

    @Test
    public void cache22Test() throws InterruptedException {
        //device_report_A006B100PFDS014
        Object synchronizeRedisCacheValue = caffeineCacheModule.getSynchronizeRedisCacheValue("device_report_A006B100PFDS014");
        System.out.println(synchronizeRedisCacheValue);
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

        //LoadingCache cache = caffeineCacheModule.getSynRedisCache();
        //cache.putAll(result);
        System.out.println("asdas");
    }

    @Test
    public void redisPipelinedTest222() {
        testCa.saveGangzi(new User(2, 19, "gangzi"));
        User laona = testCa.getLaona(1);
    }

    @Test
    void test_cacheValid() {
        SubSystemDefaultContext subSystemDefaultContext = new SubSystemDefaultContext();
        subSystemDefaultContext.setKey("asdasd");
        //caffeineCacheModule.setLinkagCacheValue3(subSystemDefaultContext);
        System.out.println();

    }

    @Resource
    RepositoryModule repositoryModule;

    @Test
    void test_alarmFiledValid() {
        List<AlarmInfo> alarmFiledInfo = repositoryModule.findAlarmFiledInfo();
        System.out.println("tset");
    }

    @Test
    void test_scheduledExecutorServiceValid() throws Exception {
        ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = getCallable(scheduled);
        scheduled.schedule(runnable, 10, TimeUnit.SECONDS);
        while (true) {
            Thread.sleep(1000);
            System.out.println(scheduled.isShutdown());
        }
    }

    private Runnable getCallable(ScheduledExecutorService scheduled) {
        return () -> {
            System.out.println("Runnable");
            scheduled.shutdown();
        };
    }

    @Test
    void test_endThisEventValid() throws Exception {
        int i = 0;
        int j = 0;

        while (true) {
            ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
            Runnable runnable = getCallable(scheduled);
            scheduled.schedule(runnable, 10, TimeUnit.SECONDS);
            ConcurrentHashMap<String, ScheduledExecutorService> triggerDeviceCodeMap = this.map.get("triggerDeviceCode"+j);
            if (null == triggerDeviceCodeMap) {
                triggerDeviceCodeMap = new ConcurrentHashMap<>();
                this.map.put("linkageDeviceCode"+i, triggerDeviceCodeMap);
            }
            triggerDeviceCodeMap.put("triggerDeviceCode"+j, scheduled);
            endThisEvent( "linkageDeviceCode"+i,  "triggerDeviceCode"+j);
            Thread.sleep(1000);
            System.out.println(scheduled.isShutdown());
        }
    }

    /**
     * 结束这个事件
     */
    private void endThisEvent(String linkageDeviceCode, String triggerDeviceCode) {

        if (this.map.containsKey(linkageDeviceCode)) {
            ConcurrentHashMap<String, ScheduledExecutorService> triggerDeviceCodeMap = this.map.get(linkageDeviceCode);
            if (triggerDeviceCodeMap.containsKey(triggerDeviceCode)) {
                ScheduledExecutorService scheduled = triggerDeviceCodeMap.get(triggerDeviceCode);
                List<Runnable> runnables = scheduled.shutdownNow();
                triggerDeviceCodeMap.remove(triggerDeviceCode);
            }
        }
    }


}

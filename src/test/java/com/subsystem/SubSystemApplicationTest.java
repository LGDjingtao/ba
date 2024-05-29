package com.subsystem;

import com.subsystem.module.cache.TestCa;
import com.subsystem.module.cache.User;
import junit.framework.TestCase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.annotation.Resource;

/**
 * Unit test for simple App.
 */
@SpringBootTest(classes = SubSystemApplication.class)
public class SubSystemApplicationTest extends TestCase {
    @Resource
    CacheManager cacheManager;
    @Autowired
    TestCa testCa;

    @Test
    public void test1(){
        //testCa.saveUser();
        testCa.saveUser(new User(1,20,"taotao"));
        Cache name1 = cacheManager.getCache("name1");
        User user = name1.get(1,User.class);
        name1.put(1,new User(1,20,"taotao"));
        user = name1.get(1,User.class);
        System.out.println(user);
    }

    public User loadDataFromDatabase(Long id) {
        // 模拟从数据库获取数据
        return new User(4, 4,"2222");
    }

}

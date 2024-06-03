package com.subsystem;


import com.subsystem.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class TestCa {

    @Cacheable( cacheNames = Constants.LOCAL,key = "#id", sync = true)
    public User getUser(int id) {
        //TODO 查找数据库
        return new User(222, 222, "tao222");
    }

    @Cacheable( cacheNames = Constants.SYN_REDIS,key = "#id", sync = true)
    public User getUser2(int id) {
        //TODO 查找数据库
        return new User(222, 222, "tao222");
    }

    /**
     * 更新缓存，每次都会执行方法体
     *
     * @param user
     */
    @CachePut( cacheNames = Constants.LOCAL, key = "#user.id")
    public User saveUser(User user) {
        return new User(1, 1, "tao1");
    }

    @CachePut( cacheNames = Constants.LOCAL, key = "#user.id")
    public User saveGangzi(User user) {
        //testCa.saveLaona(new User(1,18,"laona"));
        return user;
    }

    @CachePut( cacheNames = Constants.LOCAL, key = "#user.id")
    public User saveLaona(User user) {
        return user;
    }

    @Cacheable( cacheNames = Constants.LOCAL,key = "#id")
    public User getLaona(int id) {
        //TODO 查找数据库
        return new User(3, 90, "没找到老衲");
    }


    /**
     * 更新缓存，每次都会执行方法体
     *
     * @param user
     */
    @CachePut( cacheNames = Constants.SYN_REDIS, key = "#result.id")
    public User saveUser2(User user) {
        return new User(2, 2, "tao2");
    }


    /**
     * 删除
     * @param user
     */
    @CacheEvict(value = "name1",key = "#user.id")
    public void delUser(User user){
      //  log.info();
    }

}

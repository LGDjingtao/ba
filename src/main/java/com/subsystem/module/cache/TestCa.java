package com.subsystem.module.cache;


import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class TestCa {
    @Cacheable(value = "name1", key = "#id", sync = true)
    public User getUser(int id){
        //TODO 查找数据库
        return new User(2,3,"3");
    }

    /**
     * 更新缓存，每次都会执行方法体
     * @param user
     */
    @CachePut(value = "name1", key = "#user.id")
    public void saveUser(User user){
        //todo 保存数据库
    }

//
//    /**
//     * 删除
//     * @param user
//     */
//    @CacheEvict(value = "name1",key = "#user.id")
//    public void delUser(User user){
//        //todo 保存数据库
//    }

}

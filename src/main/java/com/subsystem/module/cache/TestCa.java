package com.subsystem.module.cache;


import com.subsystem.Constants;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
@Component
public class TestCa {
    @Cacheable( cacheNames = Constants.LOCAL,key = "#id", sync = true)
    public User getUser(int id) {
        //TODO 查找数据库
        return new User(222, 222, "tao222");
    }

    @Cacheable( cacheNames = Constants.SYNCHRONIZE_REDIS,key = "#id", sync = true)
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

    /**
     * 更新缓存，每次都会执行方法体
     *
     * @param user
     */
    @CachePut( cacheNames = Constants.SYNCHRONIZE_REDIS, key = "#result.id")
    public User saveUser2(User user) {
        return new User(2, 2, "tao2");
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

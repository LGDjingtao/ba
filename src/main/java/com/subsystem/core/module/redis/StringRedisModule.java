package com.subsystem.core.module.redis;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * reids 模块
 */
@Component
@AllArgsConstructor
@Slf4j
public class StringRedisModule {
    StringRedisTemplate redisTemplate;

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, JSON.toJSONString(value));
    }

    /**
     * 批量获取
     * redis管道流，提升一次大批量查询效率
     * 但是要注意的是这个管道流不是原子性的
     * 需要保持原子性就得使用MSET 和 MGET
     *
     * @param keys 需要查询key的集和
     * @return 所需要的value
     */
    public List<Object> batchGetByPipelined(List<String> keys) {
        List<Object> result = redisTemplate.executePipelined((RedisConnection v) -> {
            StringRedisConnection src = (StringRedisConnection) v;
            for (String k : keys) {
                src.get(k);
            }
            return null;
        });
        return result;
    }

}

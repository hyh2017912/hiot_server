package com.viewhigh.hiot.elec.service.serviceimp;

import com.viewhigh.hiot.elec.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Auther: zhaoh
 * @Date: 2019-04-12 15:31
 **/
@Service
public class RedisServiceImpl implements RedisService {


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //redis的消息队列直接使用redis数组实现

    @Autowired
    private ListOperations<String,Object> listRedis;

    /**
     * 默认过期时长，单位：秒
     */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24;

    /**
     * 不设置过期时长
     */
    public static final long NOT_EXPIRE = -1;

    //////////////////////////////////////////////VALUE///////////////////////////////////////////////////
    @Override
    public void remove(String... keys) {
        String[] var2 = keys;
        int var3 = keys.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String key = var2[var4];
            this.remove(key);
        }
    }

    @Override
    public void removePattern(String pattern) {
        Set<String> keys = this.redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            this.redisTemplate.delete(keys);
        }
    }

    @Override
    public void remove(String key) {
        if (this.exists(key)) {
            this.redisTemplate.delete(key);
        }
    }

    @Override
    public boolean exists(String key) {
        return this.redisTemplate.hasKey(key);
    }

    @Override
    public String get(String key) {
        ValueOperations<String, Object> operations = this.redisTemplate.opsForValue();
        Object o = operations.get(key);
        String s = (String) o;
//        return (String) operations.get(key);
        return s;
    }

    @Override
    public boolean set(String key, String value) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = this.redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception var5) {
            var5.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean set(String key, String value, Long expireTime, TimeUnit timeUnit) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = this.redisTemplate.opsForValue();
            operations.set(key, value);
            this.redisTemplate.expire(key, expireTime, timeUnit);
            result = true;
        } catch (Exception var7) {
            var7.printStackTrace();
        }
        return result;
    }




    //////////////////////////////////////////////LIST///////////////////////////////////////////////////
    @Override
    public Object rightPop(String key) {
        return listRedis.rightPop(key);
    }

    @Override
    public Long size(String key) {
        return  listRedis.size(key);
    }

    @Override
    public Long leftPush(String key, String value) {
        return listRedis.leftPush(key,value);
    }

    @Override
    public List<Object> range(String key, Long start, Long end) {
        return listRedis.range(key, start, end);
    }

    @Override
    public void trim(String key, Long start, Long end) {
        listRedis.trim(key, start, end);
    }



    @Override
    public Long leftPushAll(String key, String... values) {
        return listRedis.leftPushAll(key, values);
    }




    @Override
    public Long leftPushIfPresent(String key, String value) {
        return listRedis.leftPushIfPresent(key, value);
    }

    @Override
    public Long leftPush(String key, String pivot, String value) {
        return listRedis.leftPush(key, pivot, value);
    }

    @Override
    public Long rightPush(String key, String value) {
        return listRedis.rightPush(key, value);
    }

    @Override
    public Long rightPushAll(String key, String... values) {
        return listRedis.rightPushAll(key, values);
    }

    @Override
    public Long rightPushIfPresent(String key, String value) {
        return listRedis.rightPushIfPresent(key, value);
    }

    @Override
    public Long rightPush(String key, String pivot, String value) {
        return listRedis.rightPush(key, pivot, value);
    }

    @Override
    public void set(String key, Long index, String value) {
        listRedis.set(key, index, value);
    }


    @Override
    public Object rightPop(String key, Long timeout, TimeUnit unit) {
        return listRedis.rightPop(key, timeout, unit);
    }

    @Override
    public Object rightPopAndLeftPush(String sourceKey, String destinationKey) {
        return listRedis.rightPopAndLeftPush(sourceKey, destinationKey);
    }

    @Override
    public Object rightPopAndLeftPush(String sourceKey, String destinationKey, Long timeout, TimeUnit unit) {
        return listRedis.rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit);
    }


    @Override
    public Long remove(String key, Long count, Object value) {
        if (this.exists(key)) {
            return listRedis.remove(key, count, value);
        }
        return null;
    }

    @Override
    public Object index(String key, Long index) {
        return listRedis.index(key, index);
    }

    @Override
    public Object leftPop(String key) {
        if (this.exists(key)) {
            return listRedis.leftPop(key);
        }
        return null;
    }

    @Override
    public Object leftPop(String key, Long timeout, TimeUnit unit) {
        return listRedis.leftPop(key, timeout, unit);
    }

}

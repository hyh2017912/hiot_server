package com.viewhigh.hiot.elec.service;

import java.util.List;
import java.util.concurrent.TimeUnit;


public interface RedisService {
    //////////////////////////////////////////////VALUE///////////////////////////////////////////////////
    /**
     * @Title: remove
     * @Description: TODO(批量删除对应的value)
     * @param @param keys    参数
     * @return void    返回类型
     * @throws
     */
    public void remove(String... keys);

    /**
     * @Title: removePattern
     * @Description: TODO(批量删除key)
     * @param @param pattern    参数
     * @return void    返回类型
     * @throws
     */
    public void removePattern(String pattern);

    /**
     * @Title: remove
     * @Description: TODO(删除对应的value)
     * @param @param key    参数
     * @return void    返回类型
     * @throws
     */
    public void remove(String key);

    /**
     * @Title: exists
     * @Description: TODO(判断缓存中是否有对应的value)
     * @param @param key
     * @param @return    参数
     * @return boolean    返回类型
     * @throws
     */
    public boolean exists(String key);

    /**
     * @Title: get
     * @Description: TODO(读取缓存)
     * @param @param key
     * @param @return    参数
     * @return String    返回类型
     * @throws
     */
    public String get(String key);

    /**
     * @Title: set
     * @Description: TODO(写入缓存，永久)
     * @param @param key
     * @param @param value
     * @param @return    参数
     * @return boolean    返回类型
     * @throws
     */
    public boolean set(String key, String value);


    /**
     * @Title: set
     * @Description: TODO(写入缓存（有时间限制）)
     * @param @param key
     * @param @param value
     * @param @param expireTime
     * @param @param timeUnit
     * @param @return    参数
     * @return boolean    返回类型
     * @throws
     */
    public boolean set(String key, String value, Long expireTime, TimeUnit timeUnit);

    //////////////////////////////////////////////LIST///////////////////////////////////////////////////

    /**
     * @Title: range
     * @Description: TODO(获取指定key的范围内的value值的 list列表。 （0  -1）返回所有值列表 )
     * @param @param key
     * @param @param start
     * @param @param end
     * @param @return    参数
     * @return List<String>    返回类型
     * @throws
     */
    public List<Object> range(String key, Long start, Long end);

    /**
     * @Title: trim
     * @Description: TODO(保留key指定范围内的列表值。其它的都删除。)
     * @param @param key
     * @param @param start
     * @param @param end
     * @return void    返回类型
     * @throws
     */
    public void trim(String key, Long start, Long end);

    /**
     * @Title: size
     * @Description: TODO(获取key 列表的长度)
     * @param @param key
     * @param @return    参数
     * @return Long    返回类型
     * @throws
     */
    public Long size(String key);

    /**
     * @Title: leftPush
     * @Description: TODO(写入缓存，是左面进入 先进后出)
     * @param @param key
     * @param @param value
     * @param @return    参数
     * @return Long    返回类型
     * @throws
     */
    public Long leftPush(String key, String value);

    /**
     * @Title: leftPushAll
     * @Description: TODO(多个值写入缓存，是左面进入 先进后出)
     * @param @param key
     * @param @param values
     * @param @return    参数
     * @return Long    返回类型
     * @throws
     */
    public Long leftPushAll(String key, String... values);

    /**
     * @Title: leftPushIfPresent
     * @Description: TODO(如果列表存在，则在列表左边插入值value)
     * @param @param key
     * @param @param value
     * @param @return    参数
     * @return Long    返回类型
     * @throws
     */
    public Long leftPushIfPresent(String key, String value);

    /**
     * @Title: leftPush
     * @Description: TODO(在key的列表中指定的value左边（前面）插入一个新的value.如果 指定的value不存在则不插入任何值。)
     * @param @param key
     * @param @param pivot
     * @param @param value
     * @param @return    参数
     * @return Long    返回类型
     * @throws
     */
    public Long leftPush(String key, String pivot, String value);

    /**
     * @Title: rightPush
     * @Description: TODO(写入缓存，是右边面进入 先进先出)
     * @param @param key
     * @param @param value
     * @param @return    参数
     * @return Long    返回类型
     * @throws
     */
    public Long rightPush(String key, String value);

    /**
     * @Title: rightPushAll
     * @Description: TODO(多个值写入缓存，是右边面进入 先进先出)
     * @param @param key
     * @param @param values
     * @param @return    参数
     * @return Long    返回类型
     * @throws
     */
    public Long rightPushAll(String key, String... values);

    /**
     * @Title: rightPushIfPresent
     * @Description: TODO(如果列表存在，则在列表右边插入值value)
     * @param @param key
     * @param @param value
     * @param @return    参数
     * @return Long    返回类型
     * @throws
     */
    public Long rightPushIfPresent(String key, String value);

    /**
     * @Title: rightPush
     * @Description: TODO(在key的列表中指定的value右边（前面）插入一个新的value.如果 指定的value不存在则不插入任何值。)
     * @param @param key
     * @param @param pivot
     * @param @param value
     * @param @return    参数
     * @return Long    返回类型
     * @throws
     */
    public Long rightPush(String key, String pivot, String value);

    /**
     * @Title: set
     * @Description: TODO(设置key列表中指定位置的值为value index不能大于列表长度。大于抛出异常,为负数则从右边开始计算)
     * @param @param key
     * @param @param index
     * @param @param value    参数
     * @return void    返回类型
     * @throws
     */
    public void set(String key, Long index, String value);

    /**
     * @Title: remove
     * @Description: TODO(删除列表中第一个遇到的value值。count指定删除多少个,count为0则全部删除)
     * @param @param key
     * @param @param count (大于0从左边开始，等于0全部删除，小于0从右边开始)
     * @param @param value
     * @param @return    参数
     * @return Long    返回类型
     * @throws
     */
    public Long remove(String key, Long count, Object value);

    /**
     * @Title: index
     * @Description: TODO(通过索引获取列表中的元素)
     * @param @param key
     * @param @param index  (大于0从左边开始，小于0从右边开始)
     * @param @return    参数
     * @return String    返回类型
     * @throws
     */
    public Object index(String key, Long index);

    /**
     * @Title: leftPop
     * @Description: TODO(移除列表中的第一个值，并返回该值)
     * @param @param key
     * @param @return    参数
     * @return String    返回类型
     * @throws
     */
    public Object leftPop(String key);

    /**
     * @Title: leftPop
     * @Description: TODO(移除列表中的第一个值，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。)
     * @param @param key
     * @param @param timeout
     * @param @param unit
     * @param @return    参数
     * @return String    返回类型
     * @throws
     */
    public Object leftPop(String key, Long timeout, TimeUnit unit);

    /**
     * @Title: rightPop
     * @Description: TODO(移除列表中的最后一个值，并返回该值)
     * @param @param key
     * @param @return    参数
     * @return String    返回类型
     * @throws
     */
    public Object rightPop(String key);

    /**
     * @Title: rightPop
     * @Description: TODO(移除列表中的最后一个值，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。)
     * @param @param key
     * @param @param timeout
     * @param @param unit
     * @param @return    参数
     * @return String    返回类型
     * @throws
     */
    public Object rightPop(String key, Long timeout, TimeUnit unit);

    /**
     * @Title: rightPopAndLeftPush
     * @Description: TODO(从指定列表中从右边（尾部）移除第一个值，并将这个值从左边（头部）插入目标列表)
     * @param @param sourceKey
     * @param @param destinationKey
     * @param @return    参数
     * @return String    返回类型
     * @throws
     */
    public Object rightPopAndLeftPush(String sourceKey, String destinationKey);

    /**
     * @Title: rightPopAndLeftPush
     * @Description: TODO(从指定列表中从右边（尾部）移除第一个值，并将这个值从左边（头部）插入目标列表，如果移除的列表中没有值，则一直阻塞指定的单位时间)
     * @param @param sourceKey
     * @param @param destinationKey
     * @param @param timeout
     * @param @param unit
     * @param @return    参数
     * @return String    返回类型
     * @throws
     */
    public Object rightPopAndLeftPush(String sourceKey, String destinationKey, Long timeout, TimeUnit unit);


}

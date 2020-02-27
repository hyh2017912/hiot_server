package com.viewhigh.hiot.elec.support.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ThreadLocalAttr
 * Description 定义一个ThreadLocal
 * @Author huyang
 * Date 2019/5/28 18:09
 **/
public class ThreadLocalAttr {
    private static ThreadLocal<Map<String,Object>> tl = new ThreadLocal<Map<String,Object>>(){
        @Override
        protected synchronized Map<String, Object> initialValue() {
            HashMap<String, Object> map = new HashMap<>();
            map.put("readerIdleTime",1);
            map.put("writerIdleTime",1);
            map.put("allIdleTime",1);
            map.put("backMsg",null);
            return map;
        }
    };

    /**
     * 获取指定name的值，并将name在Local中值自增1
     * @param name
     * @return
     */
    public static Object getThreadAttrTimes(String name) {
        Integer integer = (Integer) tl.get().get(name);
        Integer integerCopy = integer;
        setThreadAttribute(name,++integerCopy);
        return integer;
    }



    /**
     * 获取指定name值
     * @param name
     * @return
     */
    public static Object getThreadAttribute(String name) {
        return tl.get().get(name);
    }

    /**
     * 设定指定name值
     * @param name
     * @param value
     * @return
     */
    public static Object setThreadAttribute(String name, Object value) {
        return tl.get().put(name, value);
    }

}

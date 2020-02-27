package com.viewhigh.hiot.elec.common;

/**
 * @ClassName DataConfig
 * Description 数据存储、缓存的一些配置
 * @Author huyang
 * Date 2019/5/24 14:39
 **/
public class RedisPrefix {
    // oes 设备数据socket采集通用前缀
    public static final String GLOBAL_PREFIX = "OES_SOCKET_";
    // 上传数据重复判定前缀
    public static final String DATA_REPEAT = "DATA_REPEAT_";
    // 客户端鉴权判定前缀
    public static final String DATA_CHECK = "DEVICE_CHECK_";
    // 设备是否存活
    public static final String DEVICE_ALIVE = "DEVICE_ALIVE_";
}

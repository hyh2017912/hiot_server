package com.viewhigh.hiot.elec.common;

/**
 * @EnumName ServerConfig
 * Description 定义服务端配置信息--数据配置类型为整形类
 * @Author huyang
 * Date 2019/5/16 14:09
 **/
public enum ServerConfig {
    SERVER_PORT(55884), //"服务端端口号"
    HEART_TIME_READ(3), //"服务端心跳时间间隔"
    HEART_TIME_WRITE(0), //"服务端心跳时间间隔"
    HEART_TIME_ALL(0), //"服务端心跳时间间隔"
    HEART_FREQUENCY(3), //"心跳间隔次数"
    SO_BACKLOG(32), //"设置tcp缓冲区"
    SO_SNDBUF(128), //"设置发送缓冲区"
    SO_RCVBUF(256), //"设置接收缓冲区"
    // LengthFieldBasedFrameDecoder编码解码的配置
    DECODE_MFL(1024), // maxFrameLength
    DECODE_LFO(0), // lengthFieldOffset
    DECODE_LFL(4), // lengthFieldLength
    DECODE_LAJ(0), // lengthAdjustment
    DECODE_IBT(4), // initialBytesToStrip

    // channelId 长短设置
    CHANNEL_ID_LEN(0), // 0 标识短对应asShortText 1 对应asLongText

    //设备离线判定时间
    DEVICE_OFFLINE(30), //秒
    //数据过期时间，判断数据重复用
    DATA_EXPIRE_TIME(3000); //秒

    public int getValue() {
        return value;
    }

    public void setValue(int config) {
        this.value = value;
    }

    int value;
    ServerConfig(int value){
        this.value = value;
    }
}

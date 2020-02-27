package com.viewhigh.hiot.elec.support.utils;

import com.viewhigh.hiot.elec.common.ServerConfig;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ClassName NettyUtils
 * Description 工具类
 * @Author huyang
 * Date 2019/5/28 17:43
 **/
public class NettyUtils {
    /**
     * 返回指定ChannelHandlerContext的唯一标识id，并去除-
     * @param ctx
     * @return
     */
    public static String getChannelId(ChannelHandlerContext ctx){
        if (ServerConfig.CHANNEL_ID_LEN.getValue() == 0){
            return ctx.channel().id().asShortText().replaceAll("-","");
        }else {
            return ctx.channel().id().asLongText().replaceAll("-","");
        }
    }

    public static String getChannelIdTest(String s){
        return s.replaceAll("-","");
    }

    public static void main(String[] s){
        System.out.println(getChannelIdTest("fc7774fffe60362d-0000126c-00000001-2796b8c022aefca9-70897d7e"));
    }

}

package com.viewhigh.hiot.elec.server;

import com.viewhigh.hiot.elec.common.ServerConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteOrder;

/**
 * @ClassName ServerInitializer
 * Description 初始化管道
 * @Author huyang
 * Date 2019/5/16 14:20
 **/
@Component()
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = LogManager.getLogger(ServerInitializer.class.getName());
    @Autowired
    private SocketServerHandler socketServerHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        logger.info("新连接接入成功.....");
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 心跳事件
        pipeline.addLast(new IdleStateHandler(ServerConfig.HEART_TIME_READ.getValue(),
                ServerConfig.HEART_TIME_WRITE.getValue(), ServerConfig.HEART_TIME_ALL.getValue()));
        // 这里的解码器参数配置，两端保持一直,显式配置大端传输
        pipeline.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN,
                ServerConfig.DECODE_MFL.getValue(), ServerConfig.DECODE_LFO.getValue(),
                ServerConfig.DECODE_LFL.getValue(), ServerConfig.DECODE_LAJ.getValue(),
                ServerConfig.DECODE_IBT.getValue(),true));
        pipeline.addLast(new LengthFieldPrepender(ServerConfig.DECODE_LFL.getValue()));
        //字符串解码
//        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
        //字符串编码
//        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
        //自己定义的处理器
        pipeline.addLast(socketServerHandler);
//        pipeline.addLast(new SocketServerHandler());
    }
}

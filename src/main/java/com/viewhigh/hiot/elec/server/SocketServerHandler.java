package com.viewhigh.hiot.elec.server;

import com.viewhigh.hiot.elec.common.ServerConfig;
import com.viewhigh.hiot.elec.service.serviceimp.ProtocolElecService;
import com.viewhigh.hiot.elec.service.serviceimp.DataAnalysisService;
import com.viewhigh.hiot.elec.support.utils.ByteET;
import com.viewhigh.hiot.elec.support.utils.NettyUtils;
import com.viewhigh.hiot.elec.support.utils.ThreadLocalAttr;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName SocketServerHandler
 * Description 初始化接收处理器
 * @Author huyang
 * Date 2019/5/16 14:25
 **/
@Component
@ChannelHandler.Sharable  // 必须确认handler是线程安全的
public class SocketServerHandler extends ChannelInboundHandlerAdapter{

    private static final Logger logger = LogManager.getLogger(SocketServerHandler.class.getName());

//    private byte[] backMsg = null;
    private static ExecutorService executorService;
    static{
        executorService = Executors.newCachedThreadPool();
    }
    @Autowired
    private DataAnalysisService dataAnalysisService;
    @Autowired
    private ProtocolElecService protocolElecService;
//    private DataAnalysisService dataAnalysisService = new DataAnalysisService();
//    private ProtocolElecService protocolElecService = new ProtocolElecService();



    /**
     * channel 通道 action 活跃的
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     * @param ctx
     */
    public void channelActive(ChannelHandlerContext ctx){
        logger.info("新连接：{},通道已激活：{}",ctx.channel().localAddress().toString(),NettyUtils.getChannelId(ctx));
    }

    /**
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     * @param ctx
     */
    public void channelInactive(ChannelHandlerContext ctx){
        logger.info("连接：{},通道已关闭：{}",ctx.channel().localAddress().toString(),NettyUtils.getChannelId(ctx));
        // 关闭流
    }

    /**
     * 功能：读取客户端发送过来的信息
     * 调用时刻：This method is called with the received message, whenever new data is received from a sclient.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 重置心跳
        ThreadLocalAttr.setThreadAttribute("readerIdleTime",1);
        ThreadLocalAttr.setThreadAttribute("allIdleTime",1);
        byte[] byteArr;
        try{
            ByteBuf byteBuf = (ByteBuf) msg;
            byteArr = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(byteArr);
            logger.info("\n");
            logger.info("服务器收到客户端通道：{}，字节数据：{}",NettyUtils.getChannelId(ctx), ByteET.bytes2HexString(byteArr));

            byte decodeRes = protocolElecService.checkMsg(byteArr,NettyUtils.getChannelId(ctx));
            if (decodeRes == 0x02){// 单独校验鉴权，放在最前面,不一定能获取流水号，流水号用00000000
                byte[] backMsg = protocolElecService.encode(new byte[8],(byte)0x80,"请进行身份校验".getBytes()); // todo 未鉴权提示码，
                ThreadLocalAttr.setThreadAttribute("backMsg",backMsg);
                logger.info("数据异常，异常编码:{}",String.valueOf(decodeRes));
            }else if(decodeRes != 0x00){// 校验非正常时，相应错误信息,不一定能获取流水号，流水号用00000000
                byte[] backMsg = protocolElecService.encode(new byte[8],(byte)0x80,"数据非法".getBytes()); // todo 数据非法提示码
                ThreadLocalAttr.setThreadAttribute("backMsg",backMsg);
                logger.info("数据异常，异常编码:{}",String.valueOf(decodeRes));
            }else { // 正常情况
                boolean isDeal = true;
                logger.info("数据正常，信息编码:{}",String.valueOf(decodeRes));
                // 根据流水号 cid 判断数据(非心跳和鉴权)是否重复
                byte[] sn = ByteET.getByteArr(byteArr, 2, 8);
                if (byteArr[11] != 0x00 && byteArr[11] != 0x01 && protocolElecService.isDataRepeat(ByteET.bytes2Long(sn), NettyUtils.getChannelId(ctx), byteArr[11])){
                    logger.info("数据重复，不做处理，通道:{}，数据类型:{},流水号:{}",NettyUtils.getChannelId(ctx),byteArr[11],ByteET.bytes2Long(sn));
                    byte[] backMsg = protocolElecService.encode(sn,(byte)0x80,null);
                    ThreadLocalAttr.setThreadAttribute("backMsg",backMsg);
                    return;
                }
                // 处理心跳和鉴权事件
                if (byteArr[11] == 0x00){
                    // 心跳
                    byte[] backMsg = protocolElecService.encode(sn,(byte)0x80,null); // 编码心跳返回数据
                    ThreadLocalAttr.setThreadAttribute("backMsg",backMsg);
                    isDeal = false;
                }else if (byteArr[11] == 0x01){
                    // todo 鉴权，保存在缓存
                    byte[] checkCode = protocolElecService.getCheckCode(NettyUtils.getChannelId(ctx),byteArr);
                    byte[] backMsg = protocolElecService.encode(sn,(byte)0x81,checkCode); // 编码鉴权返回数据
                    ThreadLocalAttr.setThreadAttribute("backMsg",backMsg);
                    isDeal = false;
                }
                if (isDeal){
                    // 这里确认收到信息且为有效信息，返回流水号给客户端
                    byte[] backMsg = protocolElecService.encode(sn,(byte)0x80,null); // 编码流水号返回数据
                    ThreadLocalAttr.setThreadAttribute("backMsg",backMsg);
                    // 多线程处理数据
                    executorService.execute(() -> {
                        try {
                            // 需要线程间的数据共享时，必须考虑数据的异步，后期改用队列
                            dataAnalysisService.msgDealAndWriterToDB(byteArr,NettyUtils.getChannelId(ctx));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 功能：读取完毕客户端发送过来的数据之后的操作
     * @param ctx
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.info("服务端接收：{} 数据完毕......", NettyUtils.getChannelId(ctx));
        byte[] backMsg = (byte[]) ThreadLocalAttr.getThreadAttribute("backMsg");
        if (backMsg != null){
            ctx.writeAndFlush(Unpooled.copiedBuffer(backMsg));
            logger.info("服务端向客户端通道：{}，返回数据，字节：{}",NettyUtils.getChannelId(ctx),ByteET.bytes2HexString(backMsg));
            // 重置心跳
            ThreadLocalAttr.setThreadAttribute("writerIdleTime",1);
            ThreadLocalAttr.setThreadAttribute("allIdleTime",1);
        }else{
            logger.info("服务端接收客户端{}数据失败",NettyUtils.getChannelId(ctx));
        }

    }

    /**
     * 功能：服务端发生异常的操作
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 打印详细异常堆栈信息
        protocolElecService.doClosed(ctx);
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }

    /**
     * 服务端心跳事件跳转
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.info("通道：{} 心跳检测", NettyUtils.getChannelId(ctx));
        if (evt instanceof IdleStateEvent){
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
//                    logger.info("通道：{} 读空闲第{}次心跳", channelId, readerIdleTime++);
                    logger.info("通道：{} 读空闲第{}次心跳", NettyUtils.getChannelId(ctx), ThreadLocalAttr.getThreadAttrTimes("readerIdleTime"));
                    break;
                case WRITER_IDLE:
                    logger.info("通道：{} 写空闲第{}次心跳", NettyUtils.getChannelId(ctx), ThreadLocalAttr.getThreadAttrTimes("writerIdleTime"));
                    break;
                case ALL_IDLE:
                    logger.info("通道：{} 读写空闲第{}次心跳", NettyUtils.getChannelId(ctx), ThreadLocalAttr.getThreadAttrTimes("allIdleTime"));
                    break;
                default:
                    throw new IOException("未知的IdleStateEvent状态");
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
        if ((Integer)ThreadLocalAttr.getThreadAttribute("readerIdleTime") > ServerConfig.HEART_FREQUENCY.getValue()){
            logger.info("服务端读空闲心跳次数超限，关闭连接：{}" ,NettyUtils.getChannelId(ctx));
            ctx.close();
            protocolElecService.doClosed(ctx);
        }
        if ((Integer)ThreadLocalAttr.getThreadAttribute("writerIdleTime") > ServerConfig.HEART_FREQUENCY.getValue()){
            logger.info("服务端写空闲心跳次数超限，关闭连接：{}" ,NettyUtils.getChannelId(ctx));
            ctx.close();
            protocolElecService.doClosed(ctx);
        }
        if ((Integer)ThreadLocalAttr.getThreadAttribute("allIdleTime") > ServerConfig.HEART_FREQUENCY.getValue()){
            logger.info("服务端读写空闲心跳次数超限，关闭连接：{}" ,NettyUtils.getChannelId(ctx));
            ctx.close();
            protocolElecService.doClosed(ctx);
        }
    }

}

package com.viewhigh.hiot.elec.server;

import com.viewhigh.hiot.elec.common.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ClassName SocketServer
 * Description 初始化服务端
 * @Author huyang
 * Date 2019/5/16 14:12
 **/
@Component
public class SocketServer {

    private static final Logger logger = LogManager.getLogger(SocketServer.class.getName());
    @Autowired
    private ServerInitializer serverInitializer;

    /**
     * 初始化一个服务端应用
     */
    @Scheduled(initialDelay=5000, fixedRate=2*1000)
    public void initSocketServer() throws InterruptedException {
        logger.info("开始初始化服务端..........");
        //创建两个线程组，一个通常称为boss，一个通常称为worker
        // boss用于接收连接，并将接收的连接及时注册到worker
        // worker用于和客户端交互，处理这些连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup) // 绑定两个线程组
                    .channel(NioServerSocketChannel.class) //指定nio模式
                    .option(ChannelOption.SO_BACKLOG, ServerConfig.SO_BACKLOG.getValue()) // 设置tcp缓冲区
                    .option(ChannelOption.SO_SNDBUF, ServerConfig.SO_SNDBUF.getValue()) //设置发送缓冲区
                    .option(ChannelOption.SO_RCVBUF, ServerConfig.SO_RCVBUF.getValue()) //设置接收缓冲区
                    //在服务器端的handler()方法表示对bossGroup起作用，而childHandler表示对wokerGroup起作用
                    .childHandler(serverInitializer);
//                    .childHandler(new ServerInitializer());
            // 服务器异步创建绑定,也可以在这里绑定端口,可以绑定多个端口
            ChannelFuture cf = sb.bind(ServerConfig.SERVER_PORT.getValue()).sync();
            logger.info("服务端启动成功，启动正在监听： {}", cf.channel().localAddress());
            cf.channel().closeFuture().sync(); // 关闭服务器通道
            logger.info("服务器通道已关闭！： {}", cf.channel().localAddress());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully().sync(); // 释放线程池资源
            bossGroup.shutdownGracefully().sync();
            logger.info("服务器资源已关闭！");
        }

    }

    public static void main(String[] strings) throws InterruptedException {
        new SocketServer().initSocketServer();
    }
}

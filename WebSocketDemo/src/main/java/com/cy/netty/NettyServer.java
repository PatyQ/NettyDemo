package com.cy.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    public static void main(String[] args) {

        //创建两个主从线程池
        EventLoopGroup master = new NioEventLoopGroup();
        EventLoopGroup slave = new NioEventLoopGroup();

        //创建服务器的初始化引导对象
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        //配置引导对象
        serverBootstrap
                //设置当前Netty的线程模型
                .group(master,slave)
                //设置通道的类型
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerChannelHandler());
        //绑定端口 -- 不需要绑定地址
//        serverBootstrap.bind("127.0.0.1",8080); -- future 未来
        ChannelFuture future = serverBootstrap.bind(8080);
        try {
            future.sync();//同步阻塞
            System.out.println("端口绑定完成,服务器已经启动");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

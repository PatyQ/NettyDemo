package com.cy.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

    public static void main(String[] args) {

        //创建两个主从线程池 event 事件 Loop 环
        EventLoopGroup master = new NioEventLoopGroup();
        EventLoopGroup slave = new NioEventLoopGroup();

        //创建服务器的初始化引导对象
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        //配置引导对象 -- 设置当前netty线程模型
        serverBootstrap.group(master,slave)
                //设置channel的类型
                .channel(NioServerSocketChannel.class)
                //设置事件处理器
                .childHandler(new ServerChannelHandler());

        //绑定端口
        ChannelFuture future = serverBootstrap.bind(8080);//异步操作

        try {
            future.sync();
            System.out.println("绑定端口完成,服务器已经启动!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

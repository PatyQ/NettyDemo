package com.cy.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class NettyHttpDemo {

    public static void main(String[] args) {

        //创建两个主从线程池 -- Loop 环
        EventLoopGroup master = new NioEventLoopGroup();
        EventLoopGroup slave = new NioEventLoopGroup();

        //创建服务器的初始化引导对象
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        //配置引导对象
        serverBootstrap
                //设置当前的netty线程模型
                .group(master,slave)
                //设置channel的类型
                .channel(NioServerSocketChannel.class)
                //设置事件处理器
                .childHandler(new ChannelInitializer() {
                    @Override   //初始化管道
                    protected void initChannel(Channel channel) throws Exception {

                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        // Aggregator 聚合器
                        pipeline.addLast(new HttpObjectAggregator(1024 * 1024 * 10));

                        //自定义消息处理器处理器 - 处理Http请求
                        pipeline.addLast(new HttpChannelHandler());
                    }
                });

        //绑定端口
        ChannelFuture future = serverBootstrap.bind(8080);
        try {
            future.sync();//同步阻塞
            System.out.println("端口绑定完成,服务器启动");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

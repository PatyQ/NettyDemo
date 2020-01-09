package com.cy.httpserver;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class NetyHttpServer {
    public static void main(String[] args) {

        EventLoopGroup master = new NioEventLoopGroup();
        EventLoopGroup slave = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        ServerBootstrap bootstrap = serverBootstrap.group(master, slave);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer() {

            @Override
            protected void initChannel(Channel channel) throws Exception {

                //////////      过滤器链          //////////
                ChannelPipeline pipeline = channel.pipeline();

                //添加一个文件写入处理器
                pipeline.addLast(new ChunkedWriteHandler());
                //Netty自带的Http编解码组件HttpServerCodec
                pipeline.addLast(new HttpServerCodec());
                //聚合器
                pipeline.addLast(new HttpObjectAggregator(1024 * 1024));

                //配置服务器管道处理程序
                pipeline.addLast(new HttpRequestChannelHandler());
            }
        });

        try {
            serverBootstrap.bind(80).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

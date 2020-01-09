package com.cy.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class NettyClient {

    public static void main(String[] args) {

        //创建引导对象
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                //设置服务端消息处理器 -- 接收服务端消息
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext chc, ByteBuf buf) throws Exception {
                        System.out.println("服务端:" + buf.toString());
                    }
                });


        //连接服务端
        ChannelFuture future = bootstrap.connect("127.0.0.1", 8080);

        try {
            future.sync();
            System.out.println("连接服务器成功");

            Scanner scanner = new Scanner(System.in);
            while (true){
                System.out.println("请输入");
                String content = scanner.next();

                Channel channel = future.channel();
                byte[] bytes = content.getBytes("UTF-8");
                ByteBuf byteBuf = Unpooled.buffer(bytes.length);
                byteBuf.writeBytes(bytes);
                channel.writeAndFlush(byteBuf);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

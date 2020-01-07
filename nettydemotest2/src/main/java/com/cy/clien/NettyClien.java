package com.cy.clien;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;
import java.util.Scanner;

public class NettyClien {

    public static void main(String[] args) {

        //创建引导对象
        Bootstrap bootstrap = new Bootstrap();
        //设置线程模型
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                //设置服务端消息处理器 handler 处理者
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {

                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                        System.out.println("接收到服务端的消息 :" + byteBuf.toString(Charset.forName("utf-8")));
                    }
                });

        //连接服务器
        ChannelFuture future = bootstrap.connect("127.0.0.1",8080);
        try {
            future.sync();//同步阻塞线程
            System.out.println("连接服务器成功!");

            //循环向服务器发送信息
            Scanner scanner = new Scanner(System.in);
            while (true){
                System.out.println("请输入发送的信息:");
                String msg = scanner.next();

                //将信息发送到服务端
                Channel channel = future.channel();//获得连接通道
                byte[] bytes = msg.getBytes("utf-8");
                ByteBuf buffer = Unpooled.buffer(bytes.length);
                buffer.writeBytes(bytes);
                channel.writeAndFlush(buffer);//将信息写入管道发送给服务端

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

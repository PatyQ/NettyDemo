package com.cy.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable //多个客户端共享
public class ServerChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {

    List<Channel> channels = new ArrayList<>();

    @Override
    protected void channelRead0(ChannelHandlerContext chc, ByteBuf buf) throws Exception {
        System.out.println("接收到客户端的消息: " + buf.toString());

        //将消息发送到客户端
        for (Channel channel : channels) {
            if (channel != chc.channel()){
                ByteBuf bf = Unpooled.copiedBuffer(buf);
                channel.writeAndFlush(bf);
            }
        }

    }

    /**
     * 重写管道注册方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有一个客户端连接了服务器!");
        channels.add(ctx.channel());
    }
}

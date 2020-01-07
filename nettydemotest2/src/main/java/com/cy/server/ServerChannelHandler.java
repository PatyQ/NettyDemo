package com.cy.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;
//inbound 入栈   simple 简单的

@ChannelHandler.Sharable
public class ServerChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {

    List<Channel> channels = new ArrayList<>();


    /**
     * 监听客户端注册到服务器上
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("一个客户端已连接");
        channels.add(ctx.channel());
    }

    //消息处理的方法
    @Override
    protected void channelRead0(ChannelHandlerContext chc, ByteBuf byteBuf) throws Exception {
        System.out.println("接收到客户端的消息" + byteBuf.toString());

        //将消息群发给其他客户端  unpooled 未共享
        for (Channel channel : channels) {
            if (channel != chc.channel()){//发信息给所有非当前客户端
                ByteBuf buf = Unpooled.copiedBuffer(byteBuf);
                channel.writeAndFlush(buf);
            }
        }
    }
}

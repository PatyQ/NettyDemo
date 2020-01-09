package com.cy.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext chc, FullHttpRequest request) throws Exception {

        System.out.println("接收到的数据 :" + request);

        System.out.println("请求的方法 " + request.method());

        System.out.println("测试 "+ request.content());
    }
}

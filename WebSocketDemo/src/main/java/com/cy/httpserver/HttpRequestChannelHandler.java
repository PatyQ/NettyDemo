package com.cy.httpserver;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class HttpRequestChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private String myaddress = "D:\\Test";

    @Override
    protected void channelRead0(ChannelHandlerContext chc, FullHttpRequest request) throws Exception {

        //判断方法类型   Handler 通道管理者
        HttpMethod method = request.method();
        System.out.println("获取到的方法" + method);
        if (!method.name().equals("GET")){
            setError(chc,"不要发post请求了");
            return;
        }
        //处理uri
        String uri = request.uri();
        //获取到的uri中有中文的处理
        uri = URLDecoder.decode(uri,"UTF-8");
        System.out.println("路径: "+ uri);

        //获得对应的路径
        File file = new File(myaddress,uri);
        //如果路径不存在
        if (!file.exists()){
            setError(chc, "莫的这个路径");
            return;
        }

        if (file.isDirectory()){
            //是路径 - directHandler 路径处理器
            isDirectHandler(chc,uri,file);

        }if (file.isFile()){
            //是文件
            isfilehandler(chc,file);
            return;
        }
    }

    /**
     * 是文件
     * @param chc
     * @param file
     */
    private void isfilehandler(final ChannelHandlerContext chc, File file) {

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);

        // 设置响应头 -- 设置响应体
        response.headers().add("Context-type","application/octet-stream");
        response.headers().add("Content-Length", file.length());

        //直接返回response对象,返回文件下载信息
        chc.writeAndFlush(response);

        // chunked 分块
        try {
            ChunkedNioFile nioFile = new ChunkedNioFile(file, 1024 * 1024);
            ChannelFuture future = chc.writeAndFlush(nioFile);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()){
                        System.out.println("下载完成");
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            chc.close();
        }

    }

    /**
     * 是路径
     * @param chc
     * @param uri
     * @param file
     */
    private void isDirectHandler(ChannelHandlerContext chc, String uri, File file) {

        //获取文件改路径,以及其下自路径
        File[] files = file.listFiles();
        String html = "<html><head><meta charset=\"UTF-8\"></head><ul>";

        for (File fs : files) {
            //如果是初始路径不加"/",否则需要加/
            if(uri.endsWith("/")){
                html += "<li><a href='" + uri + fs.getName() + "'>(" + (fs.isFile() ? "文件" : "文件夹") + ")" + fs.getName() + "</a></li>";
            } else {
                html += "<li><a href='" + uri + "/" + fs.getName() + "'>(" + (fs.isFile() ? "文件" : "文件夹") + ")" + fs.getName() + "</a></li>";
            }
            html += "</ul></html>";
        }

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.ACCEPTED);
        //  设置响应头 -- 设置响应体
        HttpHeaders headers = response.headers().add("Context-type","text/html;charset=utf-8");
        try {
            response.content().writeBytes(html.getBytes("utf-8"));
            //将response响应给客户端
            chc.writeAndFlush(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    /**
     *  -- full 完整的 -- Default 默认的 -- headers 头文件
     * @param chx
     * @param error
     */
    private void setError(ChannelHandlerContext chx, String error){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);

        response.headers().add("Context-type","text/html;charset=utf-8");

        try {
            response.content().writeBytes(("<html><head><meta charset=\"UTF-8\"></head><body><h1>" + error + "</h1></body></html>").getBytes("utf-8"));
            //将response响应给客户端
            chx.writeAndFlush(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally{
            chx.close();
        }

    }
}

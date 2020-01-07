package com.cy.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NIOServer {

    private static List<SocketChannel> socketChannels = new ArrayList<>();

    /**
     * NIO服务端
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        //创建一个NIO服务对象 - 监听端口,客户端连接
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //绑定端口
        serverSocketChannel.bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);//设置阻塞模式

        //创建多路复用器 -- selector 选择器
        Selector selector = Selector.open();
        //将serversocketchannel注册到多路复用器上  ----注册的类型-注册的事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //轮询多路复用器
        while (true){
            //调用多路复用器,如果多路复用器事件返回值大于0,则说明有事件发生
            int select = selector.select();
            if (select > 0){//有事件发生获取事件 -- selectionkeys 中包装了 事件类型和channel --(连接或消息)
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                //循环处理事件 -- 放入新集合,删除旧集合
                for (SelectionKey selectionKey : new HashSet<>(selectionKeys)) {
                    selectionKeys.remove(selectionKey);

                    //处理事件 -- 如果是连接事件
                    if (selectionKey.isAcceptable()){
                        System.out.println("有一个客户端连接");
                        ServerSocketChannel socketChannel = (ServerSocketChannel)selectionKey.channel();
                        SocketChannel accept = socketChannel.accept();//等待客户端连接
                        //设置socket的通道类型
                        accept.configureBlocking(false);
                        //将多路复用器注册到socket上
                        accept.register(selector,SelectionKey.OP_READ);
                        //将用户连接的套接字通道统一管理
                        socketChannels.add(accept);

                    }else if (selectionKey.isReadable()){
                        //否则判断用户发送的是否为消息
                        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();

                        //获得用户发送的消息
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 10);
                        socketChannel.read(byteBuffer);//将读到的信息写入数组中

                        //循环发送给所有客户端对象
                        for (SocketChannel channel : socketChannels) {
                            if (channel != socketChannel){//发送给所有非当前channel的客户端
                                byteBuffer.flip();//写之前反转一下
                                channel.write(byteBuffer);
                            }
                        }
                    }

                }
            }

        }

    }
}

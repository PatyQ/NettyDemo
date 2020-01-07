package com.cy.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NIOClien {

    public static SocketChannel socketChannel;

    static {
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8080));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {

//        final SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8080));
        //设置为阻塞式(默认为阻塞式)

        new Thread(){

            @Override
            public void run() {
                while (true){
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 10);
                    try {
                        int len = socketChannel.read(byteBuffer);
                        //打印服务器的数据
                        byteBuffer.flip();
                        byte[] bytes = byteBuffer.array();
                        System.out.println("获的的信息:" + new String(bytes,0,len));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        //向服务器发送信息
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println("请输入:");
            String next = scanner.next();

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 10);
            byteBuffer.put(next.getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
        }

    }
}

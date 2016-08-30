package com.example.nio.niosocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by guolei on 16-8-6.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */


public class SocketChannelServer {

    public static void main(String[] args){
        ServerSocketChannel serverSocketChannel;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(3334));
            while (true){
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                socketChannel.read(byteBuffer);
                System.err.println(new String(byteBuffer.array()).trim());
                byteBuffer.clear();
                byteBuffer.put(new String("好的，我收到了").getBytes("utf-8"));
                byteBuffer.flip();
                socketChannel.write(byteBuffer);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

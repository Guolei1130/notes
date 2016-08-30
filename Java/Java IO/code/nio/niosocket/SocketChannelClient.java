package com.example.nio.niosocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by guolei on 16-8-6.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */


public class SocketChannelClient {

    public static void main(String[] args){
        SocketChannel socketChannel;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("192.168.0.114",3334));
            socketChannel.configureBlocking(false);
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.put(new String("hello，我来自客户端").trim().getBytes("utf-8"));
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
            new Thread(new ReadFromServer(socketChannel)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ReadFromServer implements Runnable{
        private SocketChannel socketChannel;

        public ReadFromServer(SocketChannel socketChannel){
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            while (true){
                try {
                    while (socketChannel.read(byteBuffer) != -1){
                        if (byteBuffer.get(0) != 0){
                            System.err.println(new String(byteBuffer.array()).trim());
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}

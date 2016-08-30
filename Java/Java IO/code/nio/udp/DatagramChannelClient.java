package com.example.nio.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Created by guolei on 16-8-7.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */


public class DatagramChannelClient {

    public static void main(String[] args){
        DatagramChannel channel ;
        try {
            channel = DatagramChannel.open();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.put(new String("你好，我是客户端").getBytes("utf-8"));
            byteBuffer.flip();
            channel.send(byteBuffer,new InetSocketAddress("192.168.0.114",3335));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

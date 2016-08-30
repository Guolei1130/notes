package com.example.nio.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import static jdk.nashorn.internal.objects.NativeFunction.bind;

/**
 * Created by guolei on 16-8-7.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */


public class DatagramChannelServer {

    public static void main(String[] args){
        DatagramChannel datagramChannel;
        try {
            datagramChannel = DatagramChannel.open();
            datagramChannel.socket().bind(new InetSocketAddress(3335));
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            datagramChannel.receive(byteBuffer);
            System.err.println(new String(byteBuffer.array()).trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

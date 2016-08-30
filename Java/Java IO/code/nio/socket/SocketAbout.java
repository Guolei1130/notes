package com.example.nio.socket;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
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


public class SocketAbout {
    public static void main(String[] args){
        try {
//            SocketChannel socketChannel = SocketChannel.open();
//            socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(),3333));
//
//            //从SocketChannel读取数据
////            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
////            socketChannel.read(byteBuffer);
//            //从SocketChannel写入出具
//            ByteBuffer byteBuffer1 = ByteBuffer.allocate(1024);
//            byteBuffer1.put(new String("你好，我是客户端").getBytes("utf-8"));
//            socketChannel.write(byteBuffer1);
//
//            //关闭
////            socketChannel.close();
            Socket socket = new Socket("192.168.0.114",10000);
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
//            BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
//            out.println(line.readLine());
//            out.flush();
            OutputStream os = socket.getOutputStream();
            os.write(new String("hello，我是客户端").getBytes("utf-8"));
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}

package com.example.nio.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by guolei on 16-8-6.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */


public class SocketSer {

    public static void main(String[] args){
        ServerSocket serverSocket = null;
        Socket client = null ;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            serverSocket = new ServerSocket(10000);
            while (true){
                client = serverSocket.accept();
                System.err.println("客户端连接进来了");
                bis = new BufferedInputStream(client.getInputStream());
                byte[] in = new byte[1024];
                while (bis.read(in)!=-1){
                    System.err.println("服务端接收到的数据"+new String(in).trim());
                }
                bos = new BufferedOutputStream(client.getOutputStream());
                bos.write(new String("好的，我收到了").getBytes("utf-8"));
                bos.flush();
                bos.close();
                bis.close();
//                client.close();
//                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

}

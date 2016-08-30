package com.example.nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by guolei on 16-8-7.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */

/**
 * 参考资料
 *      http://blog.csdn.net/u013256816/article/details/51457215
 *      http://blog.csdn.net/chenxuegui1234/article/details/17979725
 *      http://kinghoo.iteye.com/blog/2300701
 *      http://www.blogjava.net/heyang/archive/2011/01/03/342193.html
 *      https://examples.javacodegeeks.com/core-java/nio/channels/selector-channels/java-nio-channels-selector-example/
 */

public class Server {

    public static void main(String[] args){

        Selector selector;
        ServerSocketChannel ssc ;

        try {
            selector = Selector.open();
            ssc = ServerSocketChannel.open();
            ssc.socket().bind(new InetSocketAddress(3333));
            ssc.configureBlocking(false);
            int ops = ssc.validOps();
            ssc.register(selector, ops,null);

            while (true){
                if (selector.select(3000) == 0){
                    System.err.println("没有合适的，继续等待");
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()){
                        SocketChannel socketChannel = ssc.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector,SelectionKey.OP_READ);
                        System.err.println("Connection accept : -----------" + socketChannel.getLocalAddress());
                    }else if (key.isReadable()){
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        socketChannel.read(byteBuffer);
                        String string = new String(byteBuffer.array()).trim();
                        System.err.println("Message received---->" + string);
                        if ("end".equals(string)){
                            socketChannel.register(selector,SelectionKey.OP_WRITE);
                        }
                    }else if (key.isWritable()){
                        byte[] b = new String("好的，你的消息我收到了").getBytes("utf-8");
                        ByteBuffer byteBuffer = ByteBuffer.wrap(b);
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        socketChannel.write(byteBuffer);
                        socketChannel.close();
                        ssc.close();
                    }
                    iterator.remove();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

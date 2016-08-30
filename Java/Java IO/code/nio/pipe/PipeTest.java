package com.example.nio.pipe;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * Created by guolei on 16-8-7.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */


/**
 * 可以在线程之间传递数据
 */

public class PipeTest {

    public static void main(String[] args){
        Pipe pipe;
        Pipe.SinkChannel sinkChannel;
        Pipe.SourceChannel sourceChannel;

        try {
            pipe = Pipe.open();
            new Thread(new ReadWork(pipe.source())).start();
            new Thread(new WriteWork(pipe.sink())).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class WriteWork implements Runnable{

        public Pipe.SinkChannel sinkChannel;

        public WriteWork(Pipe.SinkChannel sinkChannel){
            this.sinkChannel = sinkChannel;
        }

        @Override
        public void run() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            try {
                byteBuffer.put(new String("输入的数据").getBytes("utf-8"));
                byteBuffer.flip();
                sinkChannel.write(byteBuffer);
                byteBuffer.clear();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static class ReadWork implements Runnable{

        public Pipe.SourceChannel sourceChannel;

        public ReadWork(Pipe.SourceChannel sourceChannel){
            this.sourceChannel = sourceChannel;
        }

        @Override
        public void run() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            while (true){
                try {
                    while (sourceChannel.read(byteBuffer) != -1){
                        System.err.println("读取到的数据 "+ new String(byteBuffer.array()).trim());
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package com.example.nio.mapped;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by guolei on 16-8-8.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */


public class mapped {

    static int length = 0x8FFFFFF ;//128M
    public static void main(String[] args){
        try {
            MappedByteBuffer mappedByteBuffer = new RandomAccessFile("./1.txt","rw").getChannel().map(
                    FileChannel.MapMode.READ_WRITE,0,length);
            for (int i = 0; i < length; i++) {
                mappedByteBuffer.put((byte)'x');
            }
            System.err.println("end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

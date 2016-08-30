package com.example.nio.FileChannelText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by guolei on 16-8-6.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */


public class FileChannelDemo {

    public static void main(String[] args){
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile("./1.txt","rw").getChannel();
            ByteBuffer bf = ByteBuffer.allocate(1024);
            fc.read(bf);
            bf.flip();
            while (bf.hasRemaining()){
                System.err.println((char) bf.get());
            }
            fc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fc.isOpen()){
                try {
                    fc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

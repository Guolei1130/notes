package com.example.iostream;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by guolei on 16-8-5.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */


public class PipeStream {
//http://xouou.iteye.com/blog/1333475
    public static void main(String[] args){
        PipedInputStream pis = new PipedInputStream();
        PipedOutputStream pos = new PipedOutputStream();

        try {
            pis.connect(pos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new ReadThread(pis)).start();
        new Thread(new WriteThread(pos)).start();
    }

    static class ReadThread implements Runnable{

        private PipedInputStream pis ;

        public ReadThread(PipedInputStream pis){
            this.pis = pis ;
        }

        @Override
        public void run() {
            byte[] b = new byte[1024];
            try {
                if (null != pis){
                    System.err.println(getDate() + "  等待另一头输入数据");
                    while (pis.read(b) != -1){
                        System.err.println(getDate()+ "  读取成功,数据为:"+new String(b).trim());
                    }
                    pis.close();
                }else {
                    System.err.println("pis is null");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class WriteThread implements Runnable{

        private PipedOutputStream pos;

        public WriteThread(PipedOutputStream pos){
            this.pos = pos ;
        }

        @Override
        public void run() {
            byte[] b = new byte[1024];
            try {
                if (null != pos){
                    Thread.sleep(5000);
                    pos.write("这是输入的数据".getBytes("utf-8"));
                    System.err.println(getDate()+"  输入数据成功");
                    pos.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

        return simpleDateFormat.format(new Date().getTime());
    }
}

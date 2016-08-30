package com.example.compress.gzip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static javafx.scene.input.KeyCode.G;

/**
 * Created by guolei on 16-8-9.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */


public class GzipCompress {
    public static void main(String[] args){
        compress();
        uncompress();
    }



    /**
     * 压缩
     */
    private static void compress() {
        try {
            //创建GZIP 文件 .gz
            File file = new File("./demo.gz");
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();

            byte[] b_1 = new String("测试gzip压缩").getBytes("utf-8");
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(file));
            gzipOutputStream.write(b_1);
            gzipOutputStream.flush();
            gzipOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void uncompress() {
        File file = new File("./demo.gz");
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(file));
            byte[] b = new byte[1024];
            while (gzipInputStream.read(b) != -1){
                System.err.println("读取到的内容");
                System.err.println(new String(b).trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

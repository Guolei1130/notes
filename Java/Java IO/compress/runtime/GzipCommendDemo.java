package com.example.compress.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static javafx.scene.input.KeyCode.F;

/**
 * Created by guolei on 16-8-10.
 * ????????????????????????????????????
 * |        ??????????          |
 * |        QQ:1120832563             |
 * ????????????????????????????????????
 */


public class GzipCommendDemo {

    public static void main(String[] args){
        String systemType = System.getProperty("os.name");

        Runtime runtime = Runtime.getRuntime();
//        compress(runtime);
        uncompress(runtime);
    }

    private static void compress(Runtime runtime){
        try {
            Process p = runtime.exec("gzip 3.txt");
//            p.waitFor();
//            int value = p.exitValue();
//            System.err.println("this is result-->" + value);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(p.getInputStream(),"utf-8"));
            String s ;
            while ((s=bufferedReader.readLine()) != null){
//                new FileOutputStream(new File("./3.txt")).write(s.getBytes("utf-8"));
                System.err.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    private static void uncompress(Runtime runtime){
        try {
            Process p = runtime.exec("gunzip 3.txt.gz");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(p.getErrorStream(),"utf-8"));
            String s ;
            while ((s=bufferedReader.readLine()) != null){
                new FileOutputStream(new File("./3.txt")).write(s.getBytes("utf-8"));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

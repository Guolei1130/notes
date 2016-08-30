package com.example.compress.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static jdk.nashorn.internal.runtime.ScriptingFunctions.exec;

/**
 * Created by guolei on 16-8-9.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */


public class ZipCommendDemo {

    public static void main(String[] args){
        String systemType = System.getProperty("os.name");

        Runtime runtime = Runtime.getRuntime();
        compress(runtime);
        uncompress(runtime);
    }

    private static void compress(Runtime runtime){
        try {
            Process p = runtime.exec("zip ./dir/zip/demo.zip ./1.txt ./2.txt");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(p.getInputStream(),"utf-8"));
            String s ;
            while ((s=bufferedReader.readLine()) != null){
                System.err.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void uncompress(Runtime runtime){
        try {
            Process p = runtime.exec("unzip ./dir/zip/demo.zip -d ./dir/unzip/");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(p.getInputStream(),"utf-8"));
            String s ;
            while ((s=bufferedReader.readLine()) != null){
                System.err.println(s);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

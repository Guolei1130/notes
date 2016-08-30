package com.example.compress.zip;

/**
 * Created by guolei on 16-8-9.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * |        没有神兽，风骚依旧！          |
 * |        QQ:1120832563             |
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 对多个文件进行压缩解压
 */
public class ZipCompress {

    private static String[] path = {"./2.txt","./3.txt"};
    public static void main(String[] args){
        compress();
        uncompress();
        zipFile();
    }



    /**
     * 压缩
     */
    private static void compress() {
        FileOutputStream fos;
        CheckedOutputStream cos ;
        ZipOutputStream zos ;
        BufferedOutputStream bos;
        try {
            fos = new FileOutputStream("./dir/demo.zip");
            cos = new CheckedOutputStream(fos,new Adler32());
            zos = new ZipOutputStream(cos);
            bos = new BufferedOutputStream(zos);
            zos.setComment("this is comment message");
            for (String s : path) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(s));
                zos.putNextEntry(new ZipEntry(s));
                int c;
                while ((c = bufferedReader.read()) != -1){
                    bos.write(c);
                }
                bufferedReader.close();
                bos.flush();
            }
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void uncompress() {
        FileInputStream fis;
        try {
            fis = new FileInputStream("./dir/demo.zip");
            CheckedInputStream cis = new CheckedInputStream(fis,new Adler32());
            ZipInputStream zis = new ZipInputStream(cis);
            BufferedInputStream bis = new BufferedInputStream(zis);
            ZipEntry zipENtry ;
            while ((zipENtry = zis.getNextEntry()) != null){
                System.err.println("read file--->" + zipENtry);
                int x;
                while ((x = bis.read()) != -1){
                    System.err.println(x);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void zipFile() {
        try {
            ZipFile zipFile = new ZipFile("./dir/demo.zip");
            Enumeration  e = zipFile.entries();
            while (e.hasMoreElements()){
                ZipEntry z = (ZipEntry) e.nextElement();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

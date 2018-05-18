package com.jdddata.middleware.databus;

import com.jdddata.middleware.databus.common.PropertiesUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PropertiesRWTest {

    private static final ReadWriteLock lock = new ReentrantReadWriteLock();


    private static final File f = new File("C:\\Users\\gezhiwei\\Desktop\\application.properties");

    public void trest() {
    }

    public static void modify(String key, String value) throws IOException {

        lock.writeLock().lock();
        try {
            Properties properties = read();
            properties.replace(key, value);
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            properties.store(fileOutputStream, null);

            fileOutputStream.close();
        } finally {
            lock.writeLock().unlock();
        }

    }

    public static Properties read() throws IOException {
        lock.readLock().lock();
        try {
            return PropertiesUtil.loadProperties(f);
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void main(String[] args) {


        Thread modify = new Thread(() -> {
            try {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    modify("status", "running");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        modify.setName("first-gezhiwei");
        modify.start();

        Thread modify2 = new Thread(() -> {
            try {
                for (int i = Integer.MAX_VALUE; i > 1; i--) {
                    modify("status", "stopping");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        modify2.setName("sec-gezhiwei");
        modify2.start();

        Thread read = new Thread(() -> {
            try {
                for (; ; ) {
                    Properties properties = read();
                    System.out.println(properties.getProperty("status"));
//                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        read.start();


    }
}

package com.jdddata.middleware.databus;

import com.jdddata.middleware.databus.common.PropertiesHelper;

import java.util.Properties;

public class PropertiesRWTest {


    //            f = new File("C:\\Users\\gezhiwei\\Desktop\\application.properties");
    public static void main(String[] args) {


//        test1();
        Properties read = PropertiesHelper.read();
        String rocketmqNameServerAddress = read.getProperty("rocketmqNameServerAddress");
        System.out.println(rocketmqNameServerAddress);

    }

    private static void test1() {
        Thread modify = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                PropertiesHelper.modify("status", "running");
            }
        });
        modify.setName("first-gezhiwei");
        modify.start();

        Thread modify2 = new Thread(() -> {
            for (int i = Integer.MAX_VALUE; i > 1; i--) {
                PropertiesHelper.modify("status", "stopping");
            }
        });
        modify2.setName("sec-gezhiwei");
        modify2.start();

        Thread read = new Thread(() -> {
            for (; ; ) {
                Properties properties = PropertiesHelper.read();
                System.out.println(properties.getProperty("status"));
//                    Thread.sleep(1000);
            }
        });
        read.start();
    }
}

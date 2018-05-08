package com.jdddata.middleware.databus;

import org.springframework.util.ClassUtils;

import java.io.File;

public class DiretoryTest {

    public static void main(String[] args) {
        String path = ClassUtils.getDefaultClassLoader().getResource("config").getPath();
        System.out.println(path);
        File configFiles = new File(path);
        for (File file : configFiles.listFiles()) {
            System.out.println(file.getName());
        }

    }
}

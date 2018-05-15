package com.jdddata.middleware.databus;

import org.springframework.util.ClassUtils;

import java.io.File;

public class TestListFile {
    public static void main(String[] args) {
        String config = ClassUtils.getDefaultClassLoader().getResource("config").getPath();
        File files = new File(config);
        for (File file : files.listFiles()) {
            System.out.println(file.getName());
        }
    }

}

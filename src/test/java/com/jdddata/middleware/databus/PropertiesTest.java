package com.jdddata.middleware.databus;

import com.jdddata.middleware.databus.common.PropertiesUtil;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class PropertiesTest {
    public static void main(String[] args) throws IOException {
        Properties properties = PropertiesUtil.loadProperties(new File(ClassUtils.getDefaultClassLoader().getResource("config/init.properties").getPath()));
        for (Map.Entry<Object, Object> objectObjectEntry : properties.entrySet()) {
            System.out.println(objectObjectEntry.getKey() + ": " + objectObjectEntry.getValue());
        }
    }
}

package com.jdddata.middleware.databus;

import com.jdddata.middleware.databus.cron.CronUploadJobHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class DataBusApplication {
    private static final String INIT_FILE = "init.properties";
    private static final String REGISTER_HOST = "register.host";
    private static final String UPLOAD_DELAY = "upload.delay";

    public static void main(String[] args) {

        boolean initFileExist = false;
        String path = ClassUtils.getDefaultClassLoader().getResource("config").getPath();
        File f = new File(path);
        if (!f.isDirectory() && f.listFiles().length <= 0) {
            System.out.println("qidongshibai qingjiancha");
            return;
        }
        FileInputStream fileInputStream = null;
        try {
            for (File file : f.listFiles()) {
                if (INIT_FILE.equalsIgnoreCase(file.getName())) {
                    initFileExist = true;
                    Properties properties = new Properties();
                    fileInputStream = new FileInputStream(file);
                    properties.load(fileInputStream);

                    String host = properties.getProperty(REGISTER_HOST);
                    String seconds = properties.getProperty(UPLOAD_DELAY);
                    if (StringUtils.isBlank(host) || StringUtils.isBlank(seconds)) {
                        System.out.println("init ip is null or empty please check");
                        return;
                    }
                    CronUploadJobHelper.INSTANCE.schedule(host, Integer.valueOf(seconds));
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("qidongshibai qingjiancha");
            return;
        } catch (NumberFormatException num) {
            System.out.println("");
            return;
        } finally {
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    System.out.println("qidongshibai qingjiancha");
                }
            }
        }

        if (!initFileExist) {
            System.out.println("init file is not exist");
            return;
        }
        SpringApplication.run(DataBusApplication.class, args);
    }
}

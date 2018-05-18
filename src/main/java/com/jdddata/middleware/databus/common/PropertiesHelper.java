package com.jdddata.middleware.databus.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PropertiesHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesHelper.class);
    private static final String APPLICATION_PATH = "config/application.properties";
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();


    private static File f = null;


    static {
        try {
            f = new File(ClassUtils.getDefaultClassLoader().getResource(APPLICATION_PATH).getPath());
            if (!f.exists()) {
                throw new FileNotFoundException("application file is not exsit");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            System.exit(-1);
        }
    }

    public static void modify(String key, String value) {

        lock.writeLock().lock();
        try {
            Properties properties = read();
            if (null == properties) {
                throw new FileNotFoundException("application file is not exist");
            }
            properties.replace(key, value);
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            properties.store(fileOutputStream, null);

            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
            lock.writeLock().unlock();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            lock.writeLock().unlock();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static Properties read() {
        lock.readLock().lock();
        try {
            return PropertiesUtil.loadProperties(f);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            lock.writeLock().unlock();
        } finally {
            lock.readLock().unlock();
        }
        return null;
    }


}

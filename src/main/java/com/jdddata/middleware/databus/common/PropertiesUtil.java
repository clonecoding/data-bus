package com.jdddata.middleware.databus.common;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

  private static Logger log = LoggerFactory.getLogger(PropertiesUtil.class);
  private String filePath;
  private Properties objProperties;

  public PropertiesUtil() {
  }

  public PropertiesUtil(String filePath) {
    this.filePath = filePath;
    FileInputStream inStream = null;
    try {
      File file = new File(filePath);
      if (!FileUtil.isRegularFile(file.toPath())) {
        log.error("Not a regular file");
        return;
      }
      inStream = new FileInputStream(file);
      this.objProperties = new Properties();
      this.objProperties.load(inStream);
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
    } finally {
      IoStreamUtil.closeStream(inStream);
    }
  }

  public static String getResource(String baseName, Locale locale, String key) {
    ResourceBundle rb = ResourceBundle.getBundle(baseName, locale);
    if (rb.containsKey(key)) {
      return rb.getString(key);
    }
    return "";
  }

  public static Properties loadProperties(File file)
      throws IOException {
    if (!file.exists()) {
      return null;
    }
    Properties prop = new Properties();
    if (!FileUtil.isRegularFile(file.toPath())) {
      log.error("Not a regular file");
      return prop;
    }
    InputStream is = new FileInputStream(file);
    try {
      prop.load(is);
      return prop;
    } finally {
      close(is);
    }
  }

  public static void close(Closeable obj) {
    try {
      if (obj != null) {
        obj.close();
      }
    } catch (IOException e) {
      log.error("", e);
    }
  }

  public void saveFile(String desc) {
    FileOutputStream outStream = null;
    try {
      File file = new File(this.filePath);
      outStream = new FileOutputStream(file);
      this.objProperties.store(outStream, desc);
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
    } finally {
      IoStreamUtil.closeStream(outStream);
    }
  }

  public String getValue(String key) {
    return this.objProperties.getProperty(key);
  }

  public String getValue(String key, String defaultValue) {
    return this.objProperties.getProperty(key, defaultValue);
  }

  public void removeValue(String key) {
    this.objProperties.remove(key);
  }

  public void setValue(String key, String value) {
    this.objProperties.setProperty(key, value);
  }
}

package com.jdddata.middleware.databus.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtil {

  public static boolean isRegularFile(Path path) {
    boolean flag = false;
    File file = path.toFile();
    try {
      if ((!file.isDirectory()) && (!file.exists())) {
        flag = file.createNewFile();
      }
      BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class,
          new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
      if (!attr.isRegularFile()) {
        if (flag) {
          boolean result = file.delete();
          if (result) {
            return true;
          }
          return false;
        }
        return false;
      }
    } catch (IOException e) {
      if (flag) {
        boolean result = file.delete();
        if (result) {
          return true;
        }
        return false;
      }
      return false;
    }
    if (flag) {
      boolean result = file.delete();
      if (result) {
        return true;
      }
      return false;
    }
    return true;
  }
}

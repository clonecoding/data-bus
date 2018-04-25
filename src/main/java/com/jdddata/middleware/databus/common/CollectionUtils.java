package com.jdddata.middleware.databus.common;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CollectionUtils {

  public static boolean isCollectionNull(Collection c) {
    return null == c;
  }

  public static <T> boolean isCollectionEmpty(Collection<T> c) {
    return (null == c) || (c.isEmpty());
  }

  public static <T> T getCollectionHead(Collection<T> collections) {
    if (isCollectionEmpty(collections)) {
      return null;
    }
    return (T) collections.iterator().next();
  }

  public static <K, V> boolean isMapEmpty(Map<K, V> map) {
    return (null == map) || (map.isEmpty());
  }

  public static boolean isArrayEmpty(Object[] array) {
    return (null == array) || (array.length == 0);
  }

  public static Integer getMinNotExistInteger(List<Integer> integers) {
    Integer integer = Integer.valueOf(0);
    if ((null != integers) && (0 < integers.size())) {
      for (int index = 0; ; index++) {
        if (!integers.contains(Integer.valueOf(index))) {
          integer = Integer.valueOf(index);
          break;
        }
      }
    }
    return integer;
  }
}

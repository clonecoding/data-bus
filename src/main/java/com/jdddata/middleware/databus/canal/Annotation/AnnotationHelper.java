package com.jdddata.middleware.databus.canal.Annotation;

import java.util.HashSet;
import java.util.Set;
import org.reflections.Reflections;

public class AnnotationHelper {

  private static final Set<Class<?>> CANAL_MQ_SERVICE = new HashSet<>();

  private static final Set<Class<?>> CANAL_MSG_PROCESS = new HashSet<>();


  public synchronized static void init() {
    Reflections reflections = new Reflections("com.jdddata.middleware");
    CANAL_MQ_SERVICE.addAll(reflections.getTypesAnnotatedWith(CanalMQService.class));
    CANAL_MSG_PROCESS.addAll(reflections.getTypesAnnotatedWith(CanalMsgProcessService.class));
  }

  public static Set<Class<?>> getCanalMqService() {
    return CANAL_MQ_SERVICE;
  }


  public static Set<Class<?>> getCanalMsgProcess() {
    return CANAL_MSG_PROCESS;
  }
}

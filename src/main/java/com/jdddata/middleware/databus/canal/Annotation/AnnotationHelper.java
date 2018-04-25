package com.jdddata.middleware.databus.canal.Annotation;

import com.jdddata.common.collection.ConcurrentHashSet;
import org.reflections.Reflections;

import java.util.Set;

public class AnnotationHelper {
    private static final Set<Class<?>> CANAL_MQ_SERVICE = new ConcurrentHashSet<>();

    private static final Set<Class<?>> CANAL_MSG_PROCESS = new ConcurrentHashSet<>();


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

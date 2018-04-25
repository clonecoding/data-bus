package com.jdddata.middleware.databus.canal;

import com.jdddata.middleware.databus.canal.Annotation.AnnotationHelper;
import com.jdddata.middleware.databus.canal.api.ICanalMqService;
import com.jdddata.middleware.databus.canal.context.CanalContext;
import com.jdddata.middleware.databus.common.DataBusInvocation;
import com.jdddata.middleware.databus.rocketmq.CanalMQProducer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Set;

public class CanalMQFactory {

    public synchronized static ICanalMqService createInstance(CanalContext context) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ICanalMqService realSubject = createRealSubject(context);
        Class<? extends ICanalMqService> realSubjectClass = realSubject.getClass();
        return (ICanalMqService) Proxy.newProxyInstance(realSubjectClass.getClassLoader(), realSubjectClass.getInterfaces(), new DataBusInvocation(realSubject));
    }

    private synchronized static ICanalMqService createRealSubject(CanalContext context) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Set<Class<?>> canalMqServices = AnnotationHelper.getCanalMqService();
        for (Class<?> aClass : canalMqServices) {
            if (aClass.getSimpleName().equalsIgnoreCase(context.getMqType())) {

                java.lang.reflect.Constructor<?> constructor = aClass.getConstructor(new Class[]{context.getClass()});
                return (ICanalMqService) constructor.newInstance(context);
            }
        }
        return CanalMQProducer.instance(context);

    }
}

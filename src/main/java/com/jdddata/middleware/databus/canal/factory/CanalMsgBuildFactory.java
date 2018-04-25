package com.jdddata.middleware.databus.canal.factory;

import com.jdddata.middleware.databus.canal.Annotation.AnnotationHelper;
import com.jdddata.middleware.databus.canal.api.ICanalBuildMsg;
import com.jdddata.middleware.databus.canal.context.CanalContext;
import com.jdddata.middleware.databus.canal.msghandler.defaulthandler.DefaultMsgHandler;
import com.jdddata.middleware.databus.common.DataBusInvocation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Set;

public class CanalMsgBuildFactory {

  public synchronized static ICanalBuildMsg createInstance(CanalContext context)
      throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    ICanalBuildMsg realSubject = createRealSubject(context);
    Class<? extends ICanalBuildMsg> realSubjectClass = realSubject.getClass();
    return (ICanalBuildMsg) Proxy
        .newProxyInstance(realSubjectClass.getClassLoader(), realSubjectClass.getInterfaces(),
            new DataBusInvocation(realSubject));
  }

  private synchronized static ICanalBuildMsg createRealSubject(CanalContext context)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

    Set<Class<?>> canalMqServices = AnnotationHelper.getCanalMsgProcess();
    for (Class<?> aClass : canalMqServices) {
      if (aClass.getSimpleName().equalsIgnoreCase(context.getMqType())) {

        java.lang.reflect.Constructor<?> constructor = aClass
            .getConstructor(new Class[]{context.getClass()});
        return (ICanalBuildMsg) constructor.newInstance(context);
      }
    }
    return DefaultMsgHandler.instance(context);

  }
}

package com.jdddata.middleware.databus.canal;

import com.jdddata.middleware.databus.canal.api.ICanalMqService;
import com.jdddata.middleware.databus.canal.context.CanalContext;
import java.lang.reflect.InvocationTargetException;

import com.jdddata.middleware.databus.canal.factory.CanalMQFactory;
import org.junit.Test;

public class CanalMQFactoryTest {

  @Test
  public void test() {
    try {
      CanalContext canalContext = new CanalContext();
      canalContext.setMqType("RocketMQServiceProducer");
      ICanalMqService instance = CanalMQFactory.createInstance(canalContext);
//            Boolean aBoolean = instance.sendOrderly(new Message(8L));
//            System.out.println(aBoolean);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

}
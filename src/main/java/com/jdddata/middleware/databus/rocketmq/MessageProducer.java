package com.jdddata.middleware.databus.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class MessageProducer extends DefaultMQProducer implements DisposableBean {

  /**
   * logger
   */
  private static final Logger log = LoggerFactory.getLogger(MessageProducer.class);

  public MessageProducer(String groupName) {
    super(groupName);

  }


  @Override
  public void destroy() throws Exception {

  }


}

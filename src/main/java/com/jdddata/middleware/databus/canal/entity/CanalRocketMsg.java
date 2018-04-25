package com.jdddata.middleware.databus.canal.entity;

import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;

public class CanalRocketMsg {

    private String topic;

    private Message msg;

    private MessageQueueSelector selector;

    private Object arg;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public MessageQueueSelector getSelector() {
        return selector;
    }

    public void setSelector(MessageQueueSelector selector) {
        this.selector = selector;
    }

    public Object getArg() {
        return arg;
    }

    public void setArg(Object arg) {
        this.arg = arg;
    }
}

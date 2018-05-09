package com.jdddata.middleware.databus.canal.context;

public class CanalContext {

    /**
     * 解析类的类类型
     */
    private String msgHandlerType;

    /**
     * zk地址
     */
    private String zkAddress;

    /**
     * canal的destination
     */
    private String destination;

    /**
     * rocktmq地址
     */
    private String rocketmqAddress;

    /**
     * topic前缀
     */
    private String topicPrefix;

    /**
     * mq类型 这里写class类的小写
     */
    private String mqType;

    /**
     * rocketmq相关
     */
    private String mqProducerGroup;

    /**
     * rocketmq相关
     */
    private String mqProducerName;

    private Integer mqProducerMsgMaxSize;

    private Integer mqProducerSendMsgTimeout;

    public String getMsgHandlerType() {
        return msgHandlerType;
    }

    public void setMsgHandlerType(String msgHandlerType) {
        this.msgHandlerType = msgHandlerType;
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getRocketmqAddress() {
        return rocketmqAddress;
    }

    public void setRocketmqAddress(String rocketmqAddress) {
        this.rocketmqAddress = rocketmqAddress;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    public void setTopicPrefix(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }


    public String getMqType() {
        return mqType;
    }

    public void setMqType(String mqType) {
        this.mqType = mqType;
    }

    public String getMqProducerGroup() {
        return mqProducerGroup;
    }

    public void setMqProducerGroup(String mqProducerGroup) {
        this.mqProducerGroup = mqProducerGroup;
    }

    public String getMqProducerName() {
        return mqProducerName;
    }

    public void setMqProducerName(String mqProducerName) {
        this.mqProducerName = mqProducerName;
    }

    public Integer getMqProducerMsgMaxSize() {
        return mqProducerMsgMaxSize;
    }

    public void setMqProducerMsgMaxSize(Integer mqProducerMsgMaxSize) {
        this.mqProducerMsgMaxSize = mqProducerMsgMaxSize;
    }

    public Integer getMqProducerSendMsgTimeout() {
        return mqProducerSendMsgTimeout;
    }

    public void setMqProducerSendMsgTimeout(Integer mqProducerSendMsgTimeout) {
        this.mqProducerSendMsgTimeout = mqProducerSendMsgTimeout;
    }

    @Override
    public String toString() {
        return "CanalContext{" +
                "msgHandlerType='" + msgHandlerType + '\'' +
                ", zkAddress='" + zkAddress + '\'' +
                ", destination='" + destination + '\'' +
                ", rocketmqAddress='" + rocketmqAddress + '\'' +
                ", topicPrefix='" + topicPrefix + '\'' +
                ", mqType='" + mqType + '\'' +
                ", mqProducerGroup='" + mqProducerGroup + '\'' +
                ", mqProducerName='" + mqProducerName + '\'' +
                ", mqProducerMsgMaxSize=" + mqProducerMsgMaxSize +
                ", mqProducerSendMsgTimeout=" + mqProducerSendMsgTimeout +
                '}';
    }
}

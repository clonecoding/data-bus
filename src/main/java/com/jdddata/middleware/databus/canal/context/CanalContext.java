package com.jdddata.middleware.databus.canal.context;

import java.util.Properties;

public class CanalContext {

    /**
     * 别名 和 配置文件同名
     */
    private String contextName;

    /**
     * canal 一个概念，表示一个连接
     */
    private String destination;

    /**
     * Zookeeper address
     */
    private String parseCanalMessageType;

    /**
     * MQ的类型，本程序默认rocketmq
     */
    private String mqType;

    /**
     * topic 前缀， 可以为空
     */
    private String topicPrefix;

    /**
     * zk
     */
    private String zkAddress;


    /**
     * * rocketmq config
     *
     *
     *
     *
     *
     *
     *
     * ****
     */

    /**
     * producer group name
     */
    private String rocketMQGroupName;

    /**
     * name server
     */
    private String nameServerAddress;

    /**
     * rocketmq instance name
     */
    private String rocketmqInstanceName;

    /**
     * 每条信息最大的长度
     */
    private String rocketmqPerMessageSize;

    /**
     * 超时时间
     */
    private String rocketmqTimeOut;

    private String status;

    public static CanalContext covert(Properties properties) {
        CanalContext canalContext = new CanalContext();
        canalContext.setContextName(properties.getProperty("contextname"));
        canalContext.setDestination(properties.getProperty("destination"));
        canalContext.setParseCanalMessageType(properties.getProperty("parseCanalMessageType"));
        canalContext.setMqType(properties.getProperty("MQType"));
        canalContext.setZkAddress(properties.getProperty("zookeeper"));
        canalContext.setTopicPrefix(properties.getProperty("topicPrefix"));
        canalContext.setRocketMQGroupName(properties.getProperty("rocketmqGroupName"));
        canalContext.setNameServerAddress(properties.getProperty("rocketmqNameServerAddress"));
        canalContext.setRocketmqInstanceName(properties.getProperty("rocketmqinstanceName"));
        canalContext.setRocketmqPerMessageSize(properties.getProperty("rocketmqPerMessageSize"));
        canalContext.setRocketmqTimeOut(properties.getProperty("rocketmqSendTimeout"));
        canalContext.setStatus(properties.getProperty("status"));
        return canalContext;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getParseCanalMessageType() {
        return parseCanalMessageType;
    }

    public void setParseCanalMessageType(String parseCanalMessageType) {
        this.parseCanalMessageType = parseCanalMessageType;
    }

    public String getMqType() {
        return mqType;
    }

    public void setMqType(String mqType) {
        this.mqType = mqType;
    }

    public String getTopicPrefix() {
        return topicPrefix;
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public void setTopicPrefix(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }

    public String getRocketMQGroupName() {
        return rocketMQGroupName;
    }

    public void setRocketMQGroupName(String rocketMQGroupName) {
        this.rocketMQGroupName = rocketMQGroupName;
    }

    public String getNameServerAddress() {
        return nameServerAddress;
    }

    public void setNameServerAddress(String nameServerAddress) {
        this.nameServerAddress = nameServerAddress;
    }

    public String getRocketmqInstanceName() {
        return rocketmqInstanceName;
    }

    public void setRocketmqInstanceName(String rocketmqInstanceName) {
        this.rocketmqInstanceName = rocketmqInstanceName;
    }

    public String getRocketmqPerMessageSize() {
        return rocketmqPerMessageSize;
    }

    public void setRocketmqPerMessageSize(String rocketmqPerMessageSize) {
        this.rocketmqPerMessageSize = rocketmqPerMessageSize;
    }

    public String getRocketmqTimeOut() {
        return rocketmqTimeOut;
    }

    public void setRocketmqTimeOut(String rocketmqTimeOut) {
        this.rocketmqTimeOut = rocketmqTimeOut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CanalContext{" +
                "contextName='" + contextName + '\'' +
                ", destination='" + destination + '\'' +
                ", parseCanalMessageType='" + parseCanalMessageType + '\'' +
                ", mqType='" + mqType + '\'' +
                ", topicPrefix='" + topicPrefix + '\'' +
                ", zkAddress='" + zkAddress + '\'' +
                ", rocketMQGroupName='" + rocketMQGroupName + '\'' +
                ", nameServerAddress='" + nameServerAddress + '\'' +
                ", rocketmqInstanceName='" + rocketmqInstanceName + '\'' +
                ", rocketmqPerMessageSize='" + rocketmqPerMessageSize + '\'' +
                ", rocketmqTimeOut='" + rocketmqTimeOut + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}


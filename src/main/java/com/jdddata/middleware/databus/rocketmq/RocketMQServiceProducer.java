package com.jdddata.middleware.databus.rocketmq;

import com.jdddata.middleware.databus.canal.Annotation.CanalMQService;
import com.jdddata.middleware.databus.canal.api.ICanalMqService;
import com.jdddata.middleware.databus.canal.context.CanalContext;
import com.jdddata.middleware.databus.canal.entity.CanalRocketMsg;
import com.jdddata.middleware.databus.exception.RocketMQException;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.remoting.exception.RemotingException;

@CanalMQService
public class RocketMQServiceProducer implements ICanalMqService {

    private static RocketMQServiceProducer instance;
    private String groupName;
    private String namesrvAddr;
    private String instanceName;
    private Integer maxMessageSize;
    private Integer sendMsgTimeout;

    private MessageProducer producer;

    public RocketMQServiceProducer(CanalContext context) {
        this.namesrvAddr = context.getNameServerAddress();
        this.groupName = context.getRocketMQGroupName();
        this.instanceName = context.getRocketmqInstanceName();
        if (!StringUtils.isBlank(context.getRocketmqPerMessageSize())) {
            this.maxMessageSize = Integer.valueOf(context.getRocketmqPerMessageSize());
        }
        if (!StringUtils.isBlank(context.getRocketmqTimeOut())) {
            this.sendMsgTimeout = Integer.valueOf(context.getRocketmqTimeOut());
        }
    }

    public synchronized static ICanalMqService instance(CanalContext context) {
        if (null == instance) {
            instance = new RocketMQServiceProducer(context);
            try {
                instance.init();
            } catch (RocketMQException e) {
                //
            }
        }
        return instance;
    }

    public void init() throws RocketMQException {
        if (StringUtils.isBlank(groupName)) {
            throw new RocketMQException("groupName is blank");
        }
        if (StringUtils.isBlank(namesrvAddr)) {
            throw new RocketMQException("nameServerAddr is blank");
        }
        if (StringUtils.isBlank(instanceName)) {
            throw new RocketMQException("instanceName is blank");
        }
        producer = new MessageProducer(groupName);
        producer.setNamesrvAddr(namesrvAddr);
        producer.setInstanceName(instanceName);
        if (null != maxMessageSize && maxMessageSize != 0) {
            producer.setMaxMessageSize(maxMessageSize);
        }
        if (null != maxMessageSize && maxMessageSize != 0) {
            producer.setSendMsgTimeout(sendMsgTimeout);
        }
        try {
            producer.start();
        } catch (MQClientException e) {
            throw new RocketMQException("producer启动失败", e);
        }
        addShutdownHook();
    }


    public void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (null != producer) {
                    producer.shutdown();
                }
            } catch (Exception e) {
                // Ignore
            }
        }));
    }

    @Override
    public boolean sendOrderly(CanalRocketMsg message) {

        try {
            SendResult sendResult = producer
                    .send(message.getMsg(), message.getSelector(), message.getArg());
            if (sendResult == null || !SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                return false;
            }
        } catch (MQClientException e) {
            return false;
        } catch (RemotingException e) {
            return false;
        } catch (MQBrokerException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }
}

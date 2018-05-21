package com.jdddata.middleware.databus.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import com.jdddata.middleware.databus.canal.api.ICanalBuildMsg;
import com.jdddata.middleware.databus.canal.api.ICanalMqService;
import com.jdddata.middleware.databus.canal.api.Startable;
import com.jdddata.middleware.databus.canal.context.CanalContext;
import com.jdddata.middleware.databus.canal.entity.CanalRocketMsg;
import com.jdddata.middleware.databus.canal.factory.CanalMQFactory;
import com.jdddata.middleware.databus.canal.factory.CanalMsgBuildFactory;
import com.jdddata.middleware.databus.common.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * canal 启动类，负责初始化rocketmq，新开启一个destination线程 并进行destination线程管理
 */
public enum CanalClient implements Startable {

    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(CanalClient.class);

    private Thread.UncaughtExceptionHandler handler = null;

    private volatile boolean running = false;

    private Thread thread;

    private CanalConnector connector;

    private String destination;

    private ICanalBuildMsg iCanalBuildMsg;

    private ICanalMqService iCanalMqService;

    private CanalContext context;


    CanalClient() {
        try {
            Properties properties = PropertiesHelper.read();
            CanalContext context = CanalContext.covert(properties);
            this.context = context;
            this.iCanalBuildMsg = CanalMsgBuildFactory.createInstance(context);
            this.iCanalMqService = CanalMQFactory.createInstance(context);
            this.destination = context.getDestination();
        } catch (InvocationTargetException e) {
            return;
        } catch (NoSuchMethodException e) {
            return;
        } catch (InstantiationException e) {
            return;
        } catch (IllegalAccessException e) {
            return;
        }
    }

    @Override
    public void start() {
        handler = (t, e) -> LOGGER.error("parse events has an error", e);

        LOGGER.debug("destination {} is start");
        connector = CanalConnectors.newClusterConnector(context.getZkAddress(), context.getDestination(), "", "");

        thread = new Thread(() -> {
            process();
        });

        thread.setUncaughtExceptionHandler(handler);
        thread.start();
        running = true;
    }

    private void process() {
        int batchSize = 1;
        while (running) {
            try {
                MDC.put("destination", destination);
                connector.connect();
                connector.subscribe();
                while (running) {
                    Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                    long batchId = message.getId();
                    int size = message.getEntries().size();
                    if (batchId == -1 || size == 0) {
                        connector.ack(message.getId());
                    } else {

                        CanalRocketMsg canalRocketMsg = iCanalBuildMsg.buildMsg(message);

                        boolean success = iCanalMqService.sendOrderly(canalRocketMsg);
                        if (success) {
                            // 提交确认
                            connector.ack(message.getId());
                        } else {
                            // 处理失败, 回滚数据
                            connector.rollback(message.getId());
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("process error!", e);
            } finally {
                connector.disconnect();
                MDC.remove("destination");
            }
        }
    }


    @Override
    public void stop() {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            if (!running) {
                return;
            }
            running = false;
            if (null != thread || thread.isAlive()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }));

    }

}

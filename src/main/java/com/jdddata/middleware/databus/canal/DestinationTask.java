package com.jdddata.middleware.databus.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import com.jdddata.middleware.databus.canal.api.ICanalBuildMsg;
import com.jdddata.middleware.databus.canal.api.ICanalMqService;
import com.jdddata.middleware.databus.canal.context.CanalContext;
import com.jdddata.middleware.databus.canal.entity.CanalRocketMsg;
import com.jdddata.middleware.databus.canal.factory.CanalMQFactory;
import com.jdddata.middleware.databus.canal.factory.CanalMsgBuildFactory;
import com.jdddata.middleware.databus.common.DataBusConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class DestinationTask implements Runnable {


    private static final Logger LOGGER = LoggerFactory.getLogger(DestinationTask.class);
    private CanalContext context;

    private boolean running;


    public DestinationTask(CanalContext context) {
        this.context = context;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            process(context);
        } catch (NoSuchMethodException e) {
            LOGGER.error(e.getMessage());
        } catch (InstantiationException e) {
            LOGGER.error(e.getMessage());
        } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage());
        } catch (InvocationTargetException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void process(CanalContext context)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        LOGGER.info("{} is start",context.getContextName());
        //canal init
        CanalConnector connector = CanalConnectors
                .newClusterConnector(context.getZkAddress(), context.getDestination(), "", "");
        ICanalBuildMsg iCanalBuildMsg = CanalMsgBuildFactory.createInstance(context);
        ICanalMqService iCanalMqService = CanalMQFactory.createInstance(context);
        while (running) {
            connector.connect();
            connector.subscribe();
            Message message = connector.getWithoutAck(DataBusConstants.BATCH_SIZE);
            if (message.getId() == -1 || message.getEntries().size() == 0) {
                continue;
            }

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

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

}

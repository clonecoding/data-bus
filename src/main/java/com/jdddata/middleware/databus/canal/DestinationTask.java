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

import java.lang.reflect.InvocationTargetException;

public class DestinationTask implements Runnable {


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
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void process(CanalContext context)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

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

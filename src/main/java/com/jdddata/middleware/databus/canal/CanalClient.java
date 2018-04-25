package com.jdddata.middleware.databus.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import com.jdddata.middleware.databus.canal.Annotation.AnnotationHelper;
import com.jdddata.middleware.databus.canal.api.ICanalBuildMsg;
import com.jdddata.middleware.databus.canal.api.ICanalMqService;
import com.jdddata.middleware.databus.canal.api.Startable;
import com.jdddata.middleware.databus.canal.context.CanalContext;
import com.jdddata.middleware.databus.canal.entity.CanalRocketMsg;
import com.jdddata.middleware.databus.common.DataBusConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * canal 启动类，负责初始化rocketmq，新开启一个destination线程
 * 并进行destination线程管理
 */
public enum CanalClient implements Startable {

    INSTANCE;

    private static final Map<String, Long> canalThreadManagerCache = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(CanalClient.class);

    /**
     * 标识.控制流程
     */
    private volatile boolean running = false;


    private CanalClient() {
        AnnotationHelper.init();
    }

    @Override
    public void start(CanalContext context) {

        LOGGER.debug("destination {} is start");


        Long threadId = canalThreadManagerCache.get(context.getDestination());

        if (threadId != null) {
            LOGGER.error("");
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                running = true;
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
        });
        thread.start();
        thread.setDaemon(true);
        long id = thread.getId();
        canalThreadManagerCache.put(context.getDestination(), id);
    }

    private void process(CanalContext context) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        //canal init
        CanalConnector connector = CanalConnectors.newClusterConnector(context.getZkAddress(), context.getDestination(), "", "");
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


    @Override
    public void stop(String destination) {
        this.running = false;
        Long threadId = canalThreadManagerCache.get(destination);
        if (threadId != null) {
            ThreadGroup group = Thread.currentThread().getThreadGroup();
            while (group != null) {
                Thread[] threads = new Thread[(int) (group.activeCount() * 1.2)];
                int count = group.enumerate(threads, true);
                for (int i = 0; i < count; i++) {
                    if (threadId == threads[i].getId()) {
                        if (null != threads[i] && threads[i].isAlive()) {
                            Runtime.getRuntime().addShutdownHook(threads[i]);
                            canalThreadManagerCache.remove(destination);
                            break;
                        }
                    }
                }
                group = group.getParent();
            }
        }
    }
}

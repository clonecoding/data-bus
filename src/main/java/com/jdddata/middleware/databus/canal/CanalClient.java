package com.jdddata.middleware.databus.canal;

import com.google.common.collect.Maps;
import com.jdddata.middleware.databus.canal.Annotation.AnnotationHelper;
import com.jdddata.middleware.databus.canal.api.Startable;
import com.jdddata.middleware.databus.canal.context.CanalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * canal 启动类，负责初始化rocketmq，新开启一个destination线程 并进行destination线程管理
 */
public enum CanalClient implements Startable {

    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(CanalClient.class);

    private static final Map<String, TaskReferrer> STRING_DESTINATION_TASK_MAP = Maps.newConcurrentMap();

    private static final HashMap<String, String> statusMap = Maps.newHashMap();


    CanalClient() {
        AnnotationHelper.init();
    }

    @Override
    public void start(CanalContext context) {

        LOGGER.debug("destination {} is start");


        DestinationTask task = new DestinationTask(context);
        Thread thread = new Thread(task);
        thread.start();
        STRING_DESTINATION_TASK_MAP.put(context.getDestination(), new TaskReferrer(thread, task));

    }

    public Map getAllDestionStatus() {
        synchronized (statusMap) {
            statusMap.clear();
            for (Map.Entry<String, TaskReferrer> entry : STRING_DESTINATION_TASK_MAP.entrySet()) {
                boolean alive = entry.getValue().getThread().isAlive();
                if (!alive) {
                    STRING_DESTINATION_TASK_MAP.remove(entry.getKey());
                }
                statusMap.put(entry.getKey(), String.valueOf(alive));
            }
            return statusMap;
        }
    }

    @Override
    public void stop(String destination) {
        TaskReferrer taskReferrer = STRING_DESTINATION_TASK_MAP.get(destination);
        taskReferrer.getTask().setRunning(false);
        STRING_DESTINATION_TASK_MAP.remove(destination);
    }

}

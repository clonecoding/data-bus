package com.jdddata.middleware.databus.canal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jdddata.middleware.databus.canal.Annotation.AnnotationHelper;
import com.jdddata.middleware.databus.canal.api.Startable;
import com.jdddata.middleware.databus.canal.context.CanalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * canal 启动类，负责初始化rocketmq，新开启一个destination线程 并进行destination线程管理
 */
public enum CanalClient implements Startable {

    INSTANCE;

    private static final Map<String, Long> canalThreadManagerCache = new ConcurrentHashMap<>();
    private static final Map<String, String> canalThreadStatus = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(CanalClient.class);

    private static final Map<String, DestinationTask> STRING_DESTINATION_TASK_MAP = Maps.newConcurrentMap();


    CanalClient() {
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
        DestinationTask task = new DestinationTask(context);
        Thread thread = new Thread(task);
        thread.start();
        long id = thread.getId();
        STRING_DESTINATION_TASK_MAP.put(context.getDestination(), task);
        canalThreadManagerCache.put(context.getDestination(), id);
        canalThreadStatus.put(context.getDestination(), "start");
    }

    public Map getAllDestionStatus() {
        Set<String> destnationSet = canalThreadStatus.keySet();
        List<Long> threadLong = Lists.newArrayList();
        for (Map.Entry<String, Long> stringLongEntry : canalThreadManagerCache.entrySet()) {
            if (destnationSet.contains(stringLongEntry.getKey())) {
                threadLong.add(stringLongEntry.getValue());
            }
        }
        return Maps.newHashMap();

    }

    @Override
    public void stop(String destination) {
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

        DestinationTask task = STRING_DESTINATION_TASK_MAP.get(destination);
        task.setRunning(false);
        STRING_DESTINATION_TASK_MAP.remove(destination);
    }


}

package com.jdddata.middleware.databus.cron;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时任务，向中心服务器上报本节点信息
 */
public enum CronUploadTask {

    INSTANCE;

    private static final long TIME_DELAY = 3000L;

    CronUploadTask() {

    }

    public static void schedule() {
        Timer timer = new Timer();
        timer.schedule(new CronTask(), TIME_DELAY);
    }

    static class CronTask extends TimerTask {

        @Override
        public void run() {

        }
    }

}

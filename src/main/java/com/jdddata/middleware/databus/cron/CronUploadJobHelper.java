package com.jdddata.middleware.databus.cron;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

public enum CronUploadJobHelper {

    INSTANCE;
    PoolingHttpClientConnectionManager cm = null;

    CronUploadJobHelper() {

    }

    public PoolingHttpClientConnectionManager getCm(){
        LayeredConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        cm.setMaxTotal(10);
        cm.setDefaultMaxPerRoute(20);
        return cm;
    }

    public static void schedule(String host, Integer seconds) {
        try {

            //1、获得一个scheduler
            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler scheduler = sf.getScheduler();
            //2、获得一个jobDetail
            JobDetail job = JobBuilder.newJob(CronUploadJob.class)
                    .withIdentity("myJob")
                    .build();
            job.getJobDataMap().put("host", host);
            job.getJobDataMap().put("cm", CronUploadJobHelper.INSTANCE.getCm());

            //3、获得一个trigger
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(seconds)
                            .repeatForever())
                    .build();
            //4、把任务和触发器放到scheduler中
            scheduler.scheduleJob(job, trigger);
            //5、开始任务调度
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        schedule("https://www.baidu.com",5);
    }
}



package com.jdddata.middleware.databus.cron;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.IOException;

public class CronUploadJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        String host = (String) jobExecutionContext.getJobDetail().getJobDataMap().get("host");
        PoolingHttpClientConnectionManager cm = (PoolingHttpClientConnectionManager) jobExecutionContext.getJobDetail().getJobDataMap().get("cm");

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        HttpGet httpget = new HttpGet(host);

//                httpPost.setEntity();

        try {
            CloseableHttpResponse execute = httpClient.execute(httpget);
            for (Header header : execute.getAllHeaders()) {
                System.out.println(header.getName() + ": " + header.getValue());
            }
            execute.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

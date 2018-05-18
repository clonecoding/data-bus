package com.jdddata.middleware.databus.cron;

import com.alibaba.fastjson.JSONArray;
import com.jdddata.middleware.databus.canal.context.CanalContext;
import com.jdddata.middleware.databus.common.PropertiesHelper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class CronUploadJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {


        String host = (String) jobExecutionContext.getJobDetail().getJobDataMap().get("host");
        PoolingHttpClientConnectionManager cm = (PoolingHttpClientConnectionManager) jobExecutionContext.getJobDetail().getJobDataMap().get("cm");


        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        HttpPost httpPost = new HttpPost(host);
        String body = JSONArray.toJSONString(CanalContext.covert(PropertiesHelper.read()));
        try {
            StringEntity stringEntity = new StringEntity(body);
            httpPost.setEntity(stringEntity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            CloseableHttpResponse execute = httpClient.execute(httpPost);
            String s = EntityUtils.toString(execute.getEntity());
            System.out.println(s);
            execute.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

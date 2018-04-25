package com.jdddata.middleware.databus.canal.api;


import com.jdddata.middleware.databus.canal.entity.CanalRocketMsg;

public interface ICanalMqService {
    boolean sendOrderly(CanalRocketMsg message);
}

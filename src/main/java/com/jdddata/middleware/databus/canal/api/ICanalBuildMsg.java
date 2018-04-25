package com.jdddata.middleware.databus.canal.api;


import com.alibaba.otter.canal.protocol.Message;
import com.jdddata.middleware.databus.canal.entity.CanalRocketMsg;

public interface ICanalBuildMsg {

  CanalRocketMsg buildMsg(Message message);
}

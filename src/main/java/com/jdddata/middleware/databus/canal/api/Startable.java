package com.jdddata.middleware.databus.canal.api;

import com.jdddata.middleware.databus.canal.context.CanalContext;

public interface Startable {


  void stop(String destination);

  void start(CanalContext context);
}

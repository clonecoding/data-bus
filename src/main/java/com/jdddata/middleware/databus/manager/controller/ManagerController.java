package com.jdddata.middleware.databus.manager.controller;

import com.jdddata.middleware.databus.canal.CanalClient;
import com.jdddata.middleware.databus.canal.context.CanalContext;
import com.jdddata.middleware.databus.common.PropertiesUtil;
import com.jdddata.middleware.databus.manager.controller.dto.CanalContextDto;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ManagerController {

  private static final String START = "start";
  private static final String STOP = "stop";

  private static final String SEP = File.separator;
  public static final String CANAL_COTEXT_DIRECTORY =
      SEP + "data" + SEP + "work" + SEP + "data-bus" + SEP + "cotext";


  @RequestMapping(value = "canal/{operate}", method = RequestMethod.POST)
  public String startCanal(@PathVariable String operate,
      @RequestBody CanalContextDto canalContextDto) {

    if (START.equalsIgnoreCase(operate)) {
      if (null != canalContextDto) {
        CanalContext context = new CanalContext();
        context.setMsgHandlerType(canalContextDto.getMsgHandlerType());
        context.setZkAddress(canalContextDto.getZkAddress());
        context.setDestination(canalContextDto.getDestination());
        context.setRocketmqAddress(canalContextDto.getRocketmqAddress());
        context.setTopicPrefix(canalContextDto.getTopicPrefix());
        context.setMqType(canalContextDto.getMqType());
        context.setMqProducerGroup(canalContextDto.getMqProducerGroup());
        context.setMqProducerName(canalContextDto.getMqProducerName());
        context.setMqProducerMsgMaxSize(canalContextDto.getMqProducerMsgMaxSize());
        context.setMqProducerSendMsgTimeout(canalContextDto.getMqProducerSendMsgTimeout());
        CanalClient.INSTANCE.start(context);
        return "ok";
      }

      File f = new File(CANAL_COTEXT_DIRECTORY);
      File[] files = f.listFiles();
      if (null != files && files.length > 0) {

        for (File file : files) {
          try {
            Properties properties = PropertiesUtil.loadProperties(file);
            CanalContext canalContext = new CanalContext();
            canalContext.setMsgHandlerType(properties.getProperty("MsgHandlerType"));
            canalContext.setZkAddress(properties.getProperty("ZkAddress"));
            canalContext.setDestination(properties.getProperty("Destination"));
            canalContext.setRocketmqAddress(properties.getProperty("RocketmqAddress"));
            canalContext.setTopicPrefix(properties.getProperty("TopicPrefix"));
            canalContext.setMqType(properties.getProperty("MqType"));
            canalContext.setMqProducerGroup(properties.getProperty("MqProducerGroup"));
            canalContext.setMqProducerName(properties.getProperty("MqProducerName"));
            canalContext.setMqProducerMsgMaxSize(
                Integer.valueOf(properties.getProperty("MqProducerMsgMaxSize")));
            canalContext.setMqProducerSendMsgTimeout(
                Integer.valueOf(properties.getProperty("MqProducerSendMsgTimeout")));
            CanalClient.INSTANCE.start(canalContext);

          } catch (IOException e) {

          }

        }
      } else {
        CanalContext context = new CanalContext();
        context.setMsgHandlerType(canalContextDto.getMsgHandlerType());
        context.setZkAddress(canalContextDto.getZkAddress());
        context.setDestination(canalContextDto.getDestination());
        context.setRocketmqAddress(canalContextDto.getRocketmqAddress());
        context.setTopicPrefix(canalContextDto.getTopicPrefix());
        context.setMqType(canalContextDto.getMqType());
        context.setMqProducerGroup(canalContextDto.getMqProducerGroup());
        context.setMqProducerName(canalContextDto.getMqProducerName());
        context.setMqProducerMsgMaxSize(canalContextDto.getMqProducerMsgMaxSize());
        context.setMqProducerSendMsgTimeout(canalContextDto.getMqProducerSendMsgTimeout());
        CanalClient.INSTANCE.start(context);
      }
    }
    return null;
  }

}

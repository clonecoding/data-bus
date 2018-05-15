package com.jdddata.middleware.databus.manager.controller;

import com.jdddata.middleware.databus.common.PropertiesUtil;
import com.jdddata.middleware.databus.exception.ValidatorException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


@Controller
public class ManagerController {

    private static final String START = "start";
    private static final String STOP = "stop";

    private static final String SEP = File.separator;
    public static final String CANAL_COTEXT_DIRECTORY =
            SEP + "data" + SEP + "work" + SEP + "data-bus" + SEP + "cotext";


    @RequestMapping(value = "canal/{operate}/{contextName}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> startCanal(@PathVariable String operate, @PathVariable("contextName") String contextName) {

        String fileName = contextName + ".properties";
        try {
            File configs = new File(ClassUtils.getDefaultClassLoader().getResource("config").getPath());
            final ConcurrentHashMap<String, File> fileName_File_map = new ConcurrentHashMap<>();
            for (File file : configs.listFiles()) {
                fileName_File_map.put(file.getName(), file);
            }
            if (fileName_File_map.contains(fileName)) {
                Properties properties = PropertiesUtil.loadProperties(fileName_File_map.get(fileName));
                checkOutBeforStarting(contextName, properties);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("errMsg", contextName + ": config file is not exist");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ValidatorException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("errMsg", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    private void checkOutBeforStarting(String fileNameWithoutPrefix, Properties properties) throws ValidatorException {
        /**
         * 当前该destination的连接状态
         */
        String status = (String) properties.get("status");

        /**
         * 当前该destination的文件名字或者叫别名
         */
        String contextname = (String) properties.get("contextname");

        /**
         * destination
         */
        String destination = (String) properties.get("destination");

        /**
         * canal连接的zk地址
         */
        String zookeeper = (String) properties.get("zookeeper");

        /**
         * 解析类型，databus内置多种解析类型，选择其中一种解析类型，解析canal的数据1
         */
        String parseCanalMessageType = (String) properties.get("parseCanalMessageType");

        /**
         * MQ类型，默认使用rocketmq
         */
        String MQType = (String) properties.get("MQType");

        /**
         * MQ topic前缀，可配置字段
         */
        String topicPrefix = (String) properties.get("topicPrefix");

        /**
         * rocketmq group的名字
         */
        String rocketmqGroupName = (String) properties.get("rocketmqGroupName");

        /**
         * rocketmq nameserver 地址
         */
        String rocketmqNameServerAddress = (String) properties.get("rocketmqNameServerAddress");

        /**
         * rocketmq 每条消息最大字节
         */
        String rocketmqPerMessageSize = (String) properties.get("rocketmqPerMessageSize");

        String rocketmqinstanceName = (String) properties.get("rocketmqinstanceName");

        String rocketmqSendTimeout = (String) properties.get("rocketmqSendTimeout");

        isBank(status, contextname, destination, zookeeper, parseCanalMessageType, MQType, rocketmqGroupName, rocketmqNameServerAddress, rocketmqSendTimeout);

        if ("running".equalsIgnoreCase(status)) {
            throw new ValidatorException("your application is running");
        }


        if (!fileNameWithoutPrefix.equalsIgnoreCase(contextname)) {
            throw new ValidatorException("the config content is not belong to " + fileNameWithoutPrefix + ".properties");
        }

    }

    private void isBank(String status, String contextname, String destination, String zookeeper, String parseCanalMessageType, String MQType, String rocketmqGroupName, String rocketmqNameServerAddress, String rocketmqSendTimeout) throws ValidatorException {
        if (StringUtils.isBlank(status)) {
            throw new ValidatorException("there is no status in properties,please config it again");
        }
        if (StringUtils.isBlank(contextname)) {
            throw new ValidatorException("there is no contextname in properties,please config it again");
        }
        if (StringUtils.isBlank(destination)) {
            throw new ValidatorException("there is no destination in properties,please config it again");
        }
        if (StringUtils.isBlank(zookeeper)) {
            throw new ValidatorException("there is no zookeeper address in properties,please config it again");
        }
        if (StringUtils.isBlank(parseCanalMessageType)) {
            throw new ValidatorException("you must choose one of the parse ways in properties,please config it again");
        }
        if (StringUtils.isBlank(MQType)) {
            throw new ValidatorException("you must choose one of the MQs in properties,please config it again");
        }
        if (StringUtils.isBlank(rocketmqGroupName)) {
            throw new ValidatorException("there is no rocketmqGroupName in properties,please config it again");
        }
        if (StringUtils.isBlank(rocketmqNameServerAddress)) {
            throw new ValidatorException("there is no rocketmqNameServerAddress in properties,please config it again");
        }
        if (StringUtils.isBlank(rocketmqSendTimeout)) {
            throw new ValidatorException("there is no rocketmqSendTimeout in properties,please config it again");
        }
    }

}

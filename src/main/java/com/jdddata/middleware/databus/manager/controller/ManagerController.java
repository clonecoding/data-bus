package com.jdddata.middleware.databus.manager.controller;

import com.jdddata.middleware.databus.canal.CanalClient;
import com.jdddata.middleware.databus.canal.context.CanalContext;
import com.jdddata.middleware.databus.common.CanalStatus;
import com.jdddata.middleware.databus.common.PropertiesUtil;
import com.jdddata.middleware.databus.exception.ValidatorException;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkTimeoutException;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


@Controller
public class ManagerController {

    @RequestMapping(value = "canal/start/{contextName}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> startCanal(@PathVariable("contextName") String contextName) {

        String fileName = contextName + ".properties";
        try {
            File configs = new File(ClassUtils.getDefaultClassLoader().getResource("config").getPath());
            final ConcurrentHashMap<String, File> fileName_File_map = new ConcurrentHashMap<>();
            for (File file : configs.listFiles()) {
                fileName_File_map.put(file.getName(), file);
            }
            if (fileName_File_map.containsKey(fileName)) {
                File file = fileName_File_map.get(fileName);
                Properties properties = PropertiesUtil.loadProperties(file);

                checkOutBeforStarting(contextName, properties);

                CanalClient.INSTANCE.start(CanalContext.covert(properties));
                properties.replace("status", "running");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                properties.store(fileOutputStream, null);
                fileOutputStream.close();

                Map<String, Object> map = new HashMap<>();
                map.put("errMsg", "");
                return new ResponseEntity<>(map, HttpStatus.OK);

            }
            Map<String, Object> map = new HashMap<>();
            map.put("errMsg", contextName + ": config file is not exist");
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException | ValidatorException e) {
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
        if (StringUtils.isBlank(status)) {
            throw new ValidatorException("there is no status in properties,please config it again");
        }
        if (CanalStatus.RUNNING.getValue().equalsIgnoreCase(status)) {
            throw new ValidatorException("your application is running");
        }

        /**
         * 当前该destination的文件名字或者叫别名
         */
        String contextname = (String) properties.get("contextname");
        if (!fileNameWithoutPrefix.equalsIgnoreCase(contextname)) {
            throw new ValidatorException("the config content is not belong to " + fileNameWithoutPrefix + ".properties");
        }
        if (StringUtils.isBlank(contextname)) {
            throw new ValidatorException("there is no contextname in properties,please config it again");
        }
        /**
         * destination
         */
        String destination = (String) properties.get("destination");
        if (StringUtils.isBlank(destination)) {
            throw new ValidatorException("there is no destination in properties,please config it again");
        }
        /**
         * canal连接的zk地址
         */
        String zookeeper = (String) properties.get("zookeeper");
        if (StringUtils.isBlank(zookeeper)) {
            throw new ValidatorException("there is no zookeeper address in properties,please config it again");
        }
        checkZK(destination, zookeeper);

        /**
         * 解析类型，databus内置多种解析类型，选择其中一种解析类型，解析canal的数据1
         */
        String parseCanalMessageType = (String) properties.get("parseCanalMessageType");

        /**
         * MQ类型，默认使用rocketmq
         */
        String MQType = (String) properties.get("MQType");
        if (StringUtils.isBlank(parseCanalMessageType)) {
            throw new ValidatorException("you must choose one of the parse ways in properties,please config it again");
        }
        if (StringUtils.isBlank(MQType)) {
            throw new ValidatorException("you must choose one of the MQs in properties,please config it again");
        }

        /**
         * MQ topic前缀，可配置字段
         */
        String topicPrefix = (String) properties.get("topicPrefix");

        /**
         * rocketmq group的名字
         */
        String rocketmqGroupName = (String) properties.get("rocketmqGroupName");
        if (StringUtils.isBlank(rocketmqGroupName)) {
            throw new ValidatorException("there is no rocketmqGroupName in properties,please config it again");
        }

        /**
         * rocketmq nameserver 地址
         */
        String rocketmqNameServerAddress = (String) properties.get("rocketmqNameServerAddress");
        if (StringUtils.isBlank(rocketmqNameServerAddress)) {
            throw new ValidatorException("there is no rocketmqNameServerAddress in properties,please config it again");
        }
        /**
         * rocketmq 每条消息最大字节
         */
        String rocketmqPerMessageSize = (String) properties.get("rocketmqPerMessageSize");
        if (!StringUtils.isBlank(rocketmqPerMessageSize)) {
            for (char c : rocketmqPerMessageSize.toCharArray()) {
                if (!Character.isDigit(c)) {
                    throw new ValidatorException("rocketmqPerMessageSize must be digit");
                }
            }
        }

        String rocketmqinstanceName = (String) properties.get("rocketmqinstanceName");

        String rocketmqSendTimeout = (String) properties.get("rocketmqSendTimeout");

        if (!StringUtils.isBlank(rocketmqSendTimeout)) {
            for (char c : rocketmqPerMessageSize.toCharArray()) {
                if (!Character.isDigit(c)) {
                    throw new ValidatorException("rocketmqPerMessageSize must be digit");
                }
            }
        }


    }

    private void checkZK(String destination, String zookeeper) throws ValidatorException {
        try {
            ZkClient zkClient = new ZkClient(zookeeper, 5000);

            List<String> destinations = zkClient.getChildren("/otter/canal/destinations");
            if (!destinations.contains(destination)) {
                throw new ValidatorException("please make sure destination " + destination + "of the canal server is running or destination is right");
            }

            if (!zkClient.exists("/otter/canal/destinations/" + destination)) {
                throw new ValidatorException("destain " + destination + "of the canal server is not running please start it");
            }

        } catch (ZkTimeoutException timeoutException) {
            throw new ValidatorException(destination + ": " + timeoutException.getMessage());
        }

    }


}

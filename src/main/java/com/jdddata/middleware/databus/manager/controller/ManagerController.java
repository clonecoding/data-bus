package com.jdddata.middleware.databus.manager.controller;

import com.jdddata.middleware.databus.canal.CanalClient;
import com.jdddata.middleware.databus.common.CanalStatus;
import com.jdddata.middleware.databus.common.PropertiesHelper;
import com.jdddata.middleware.databus.exception.ValidatorException;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkTimeoutException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


@Controller
public class ManagerController {

    @RequestMapping(value = "canal/start", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> startCanal() {

        try {
            Properties properties = PropertiesHelper.read();

            checkOutBeforStarting(properties);

            CanalClient.INSTANCE.start();
            PropertiesHelper.updateStatus(CanalStatus.RUNNING.getValue());

            Map<String, Object> map = new HashMap<>();
            map.put("errMsg", "");

            return new ResponseEntity<>(map, HttpStatus.OK);

        } catch (ValidatorException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("errMsg", e.getMessage());
            return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @RequestMapping(value = "canal/stop", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> stopCanal() {

        CanalClient.INSTANCE.stop();
        Map<String, Object> map = new HashMap<>();
        map.put("errMsg", "");
        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    private void checkOutBeforStarting(Properties properties) throws ValidatorException {
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
            zkClient.close();

        } catch (ZkTimeoutException timeoutException) {
            throw new ValidatorException(destination + ": " + timeoutException.getMessage());
        }

    }


}

package com.jdddata.middleware.databus.zk;

import com.jdddata.middleware.databus.exception.ValidatorException;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkTimeoutException;

import java.util.List;

public class ZkTest {
    public static void main(String[] args) throws ValidatorException {
        try {
            ZkClient zkClient = new ZkClient("10.33.93.101:211", 1000);

            List<String> children1 = zkClient.getChildren("/otter/canal/destinations");
            System.out.println(children1);
            /**
             * 如果这个list不够
             */

        } catch (ZkTimeoutException timeout) {
            System.out.println("asdfasdf" + timeout.getMessage());
            throw new ValidatorException(timeout.getMessage());
        }
    }
}

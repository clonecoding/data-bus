package com.jdddata.middleware.databus.zk;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class Connection implements Watcher {
    private static final int DEFAULT_SESSIONTIMEOUT = 5 * 1000;
    private final String connectionString;
    private final int sessionTimeout;
    private ZooKeeper zooKeeper;
    private volatile boolean connected;

    public boolean isConnected() {
        return connected;
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public Connection(String connectionString, int sessionTimeout) {
        this.connectionString = connectionString;
        this.sessionTimeout = sessionTimeout;
        try {
            zooKeeper = new ZooKeeper(connectionString, sessionTimeout, this, false);
            connected = true;
        } catch (IOException e) {
            return;
        }
    }

    public Connection(String connectionString) {
        this(connectionString, DEFAULT_SESSIONTIMEOUT);
    }

    public void process(WatchedEvent event) {
        System.out.println(event);
        //TODO://
    }

    public static void main(String[] args) throws KeeperException, InterruptedException {
        String connectionString = "10.33.93.101:2181,10.33.93.101:2182,10.33.93.101:2183";
        Connection connection = new Connection(connectionString);
        ZooKeeper zooKeeper = connection.getZooKeeper();
//        System.out.println(zooKeeper.getChildren("/", false));
//        System.out.println(zooKeeper.getChildren("/zookeeper", false));
//        System.out.println(zooKeeper.getChildren("/zookeeper/quota", false));
//        System.out.println(zooKeeper.getChildren("/otter", false));
//        System.out.println(zooKeeper.getChildren("/otter/canal", false));
        Stat value = new Stat();
        byte[] data = zooKeeper.getData("/otter/canal/destinations/example/1001/cursor", connection, value);

        JSONArray jsonObject = JSONObject.parseArray(new String(data));

        System.out.println(jsonObject);

//        System.out.println(new String(data));

    }

}

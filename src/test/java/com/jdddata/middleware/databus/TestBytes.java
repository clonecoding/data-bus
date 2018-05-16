package com.jdddata.middleware.databus;

public class TestBytes {
    public static void main(String[] args) {
        String a = "{\n" +
                "\t\"@type\": \"com.alibaba.otter.canal.protocol.position.LogPosition\",\n" +
                "\t\"identity\": {\n" +
                "\t\t\"slaveId\": -1,\n" +
                "\t\t\"sourceAddress\": {\n" +
                "\t\t\t\"address\": \"ssdata-foundation-01.cs1cloud.internal\",\n" +
                "\t\t\t\"port\": 3306\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"postion\": {\n" +
                "\t\t\"included\": false,\n" +
                "\t\t\"journalName\": \"mysql-bin.000001\",\n" +
                "\t\t\"position\": 1433,\n" +
                "\t\t\"serverId\": 1,\n" +
                "\t\t\"timestamp\": 1525687533000\n" +
                "\t}\n" +
                "}\n";
        System.out.println(a.getBytes().length);
    }
}

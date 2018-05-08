package com.jdddata.middleware.databus;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Iptest {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        System.out.println(inetAddress);
    }
}

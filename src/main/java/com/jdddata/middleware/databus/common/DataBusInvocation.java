package com.jdddata.middleware.databus.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DataBusInvocation implements InvocationHandler {

    private Object target;

    public DataBusInvocation(Object object) {
        this.target = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


        return method.invoke(target, args);


    }
}

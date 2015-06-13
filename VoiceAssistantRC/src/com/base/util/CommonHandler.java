package com.base.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class CommonHandler implements InvocationHandler {

    private Object obj;
    
    @Override
    public Object invoke(Object arg0, Method arg1, Object[] arg2)
            throws Throwable {

        return null;
    }

    public CommonHandler(Object obj) {
        obj = obj;
    }

    public static Object newInstance(Object obj) {
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj
                .getClass().getInterfaces(), new CommonHandler(obj));
    }

}

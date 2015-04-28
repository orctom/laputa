package com.orctom.laputa;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.lang.reflect.Method;

/**
 * Created by hao on 4/28/15.
 */
public class ServiceInterceptor {

    @RuntimeType
    public Object intercept(@AllArguments Object[] allArguments, @Origin Method method) {
        System.out.println("method name: " + method.getName());
        for (Object arg : allArguments) {
            System.out.println("arg: " + arg);
        }
        return "dummy from ServiceInterceptor";
    }





    private String getEndpointURL(String serviceId) {
        //TODO
        return "http://dummy:8080/context";
    }
}

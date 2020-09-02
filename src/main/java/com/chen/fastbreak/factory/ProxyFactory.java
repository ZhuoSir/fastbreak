package com.chen.fastbreak.factory;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyFactory implements MethodInterceptor {

    private AtomicInteger times = new AtomicInteger(0);

    private Object targetObject; // 代理的目标对象

    private ProxyCallBack proxyCallBack;

    public <T> T createProxyInstance(T targetObject, ProxyCallBack proxyCallBack) {
        this.targetObject = targetObject;
        this.proxyCallBack = proxyCallBack;

        Enhancer enhancer = new Enhancer(); // 该类用于生成代理对象
        enhancer.setSuperclass(this.targetObject.getClass()); // 设置目标类为代理对象的父类
        enhancer.setCallback(this); // 设置回调用对象为本身

        return (T) enhancer.create();
    }

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        if (proxyCallBack != null) {
            if (proxyCallBack.beforeExecute() == 1) {

            }
        }
        Object result = null;
        try {
             result = methodProxy.invoke(targetObject, objects);
        } catch (Exception e) {
            if (proxyCallBack != null) {
                proxyCallBack.exeception(e);
            }
        }

        if (proxyCallBack != null) {
            proxyCallBack.afterExecute();
        }

        return result;
    }
}

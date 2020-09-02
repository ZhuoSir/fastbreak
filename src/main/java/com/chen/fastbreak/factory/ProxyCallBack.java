package com.chen.fastbreak.factory;

public interface ProxyCallBack {

    int beforeExecute();

    int afterExecute();

    void exeception(Exception e);
}

package com.liunx.rpc.service;

import com.liunx.rpc.common.annotations.ServiceInterface;

/**
 * 定义共享接口,默认暴露接口中的全部方法
 */
@ServiceInterface
public interface IHelloService {

    String sayHello(String content);
}

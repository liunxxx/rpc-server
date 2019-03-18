package com.liunx.rpc.provider;

import com.liunx.rpc.common.annotations.ServiceImplement;
import com.liunx.rpc.service.IHelloService;

/**
 * 共享接口的实现
 */
@ServiceImplement
public class HelloServiceImpl implements IHelloService {

    @Override
    public String sayHello(String content) {
        return "Hello" + content;
    }

}

package com.liunx.rpc;

import com.liunx.rpc.proxys.ServiceProxy;
import com.liunx.rpc.service.IHelloService;

public class ConsumerApp {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;
        ServiceProxy.init(host, port);
        IHelloService service = ServiceProxy.getProxy(IHelloService.class);
        String result = service.sayHello("liunx");
        System.out.println("====>" + result);
    }
}

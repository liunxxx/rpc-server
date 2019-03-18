package com.liunx.rpc.proxys;

import com.liunx.rpc.common.entity.RpcRequest;
import com.liunx.rpc.common.entity.RpcResponseFuture;
import com.liunx.rpc.common.utils.CommonUtils;
import com.liunx.rpc.consumer.ServiceDiscovery;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ServiceProxy {

    private static ServiceDiscovery serviceDiscovery;

    private static Channel channel;

    public static void init(String host, int port) {
        serviceDiscovery = new ServiceDiscovery(host, port);
        channel = serviceDiscovery.discovery();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("clazz must be a interface");
        }
        return (T) Proxy.newProxyInstance(CommonUtils.getClassLoader(), new Class<?>[]{clazz},
        new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String className = clazz.getName();
                String methodName = method.getName();
                RpcRequest request = new RpcRequest();
                request.setClassName(className);
                request.setMethodName(methodName);
                request.setArgs(args);
                ChannelFuture future = channel.writeAndFlush(request);
                RpcResponseFuture responseFuture = new RpcResponseFuture();
                responseFuture.setOpaque(request.getOpaque());
                serviceDiscovery.getConsumerHandler().putResponseFuture(responseFuture);
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            responseFuture.setSendRequestOK(true);
                            return;
                        } else {
                            responseFuture.setSendRequestOK(false);
                        }
                        responseFuture.setCause(future.cause());
                        responseFuture.setResult(null);
                    }
                });
                return responseFuture.awaitResult();
            }
        });
    }
}

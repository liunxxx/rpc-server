package com.liunx.rpc.publisher.handlers;

import com.liunx.rpc.common.entity.RpcRequest;
import com.liunx.rpc.common.entity.RpcResponse;
import com.liunx.rpc.common.enums.RequestType;
import com.liunx.rpc.common.utils.CommonUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static Map<String, Object> classNameMap;

    private static final String STARATEGY_PATH = "com.liunx.rpc.provider";

    static {
        classNameMap = CommonUtils.findClassLocal(STARATEGY_PATH);
        if (classNameMap == null) {
            classNameMap = new ConcurrentHashMap<String, Object>();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        String className = msg.getClassName();
        Object classObject = classNameMap.get(className);
        Class clazz = Class.forName(className);
        if (classObject == null) {
//            classObject = classNameMap.putIfAbsent(className, clazz.newInstance());
            throw new IllegalArgumentException("the service is not exists, className:" + className);
        }
        String methodName = msg.getMethodName();
        Object[] args = msg.getArgs();
        Class[] argsClass = null;
        if (args != null && args.length > 0) {
            argsClass = new Class[args.length];
            for (int i =0; i < args.length; i++) {
                argsClass[i] = args[i].getClass();
            }
        } else {
            argsClass = new Class[0];
        }
        Method method = clazz.getMethod(methodName, argsClass);
        Object result = method.invoke(classObject, args);
        RpcResponse response = new RpcResponse();
        response.setType(RequestType.RESPONSE_TYPE.getType());
        response.setClassName(className);
        response.setMethodName(methodName);
        response.setArgs(args);
        response.setResult(result);
        response.setOpaque(msg.getOpaque());
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        System.out.println("consumer is active, consumer address:" + address);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        System.out.println("consumer occur error, consumer address:" + address
                + ", errorMsg:" + cause.getMessage());
        ctx.channel().close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("consumer occur error and close success, consumer address:" + address);
                    return;
                }
                System.out.println("consumer occur error and close failed, consumer address:" + address);
            }
        });
    }
}

package com.liunx.rpc.handlers;

import com.liunx.rpc.common.entity.RpcResponse;
import com.liunx.rpc.common.entity.RpcResponseFuture;
import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcConsumerHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private Map<Integer, RpcResponseFuture> map = new ConcurrentHashMap<Integer, RpcResponseFuture>();

    public void putResponseFuture(RpcResponseFuture responseFuture) {
        if (responseFuture != null) {
            map.put(responseFuture.getOpaque(), responseFuture);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        System.out.println("consumer has connected, romote address:" + address);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        System.out.println("receive msg from remote" + msg);
        RpcResponseFuture responseFuture = map.get(msg.getOpaque());
        if (responseFuture != null) {
            responseFuture.setResult(msg.getResult());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        System.out.println("local occur error, disconnect from romote, romote address:" + address
                + ", errorMsg:" + cause.getMessage());
        ctx.channel().close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("local occur error and close success, romote address:" + address);
                    return;
                }
                System.out.println("local occur error and close failed, romote address:" + address);
            }
        });
    }


}

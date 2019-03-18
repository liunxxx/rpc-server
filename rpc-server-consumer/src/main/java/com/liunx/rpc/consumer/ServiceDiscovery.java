package com.liunx.rpc.consumer;

import com.liunx.rpc.common.entity.RpcRequest;
import com.liunx.rpc.common.enums.SerializeType;
import com.liunx.rpc.common.handlers.RpcRouteDecoder;
import com.liunx.rpc.common.handlers.RpcRouteEncoder;
import com.liunx.rpc.common.utils.CommonUtils;
import com.liunx.rpc.handlers.RpcConsumerHandler;
import com.liunx.rpc.service.IHelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import javax.annotation.Resource;

public class ServiceDiscovery {

    private String host;

    private int port;

    private RpcConsumerHandler consumerHandler;

    public ServiceDiscovery(String host, int port) {
        this.host = host;
        this.port = port;
        consumerHandler = new RpcConsumerHandler();
    }

    public RpcConsumerHandler getConsumerHandler() {
        return consumerHandler;
    }

    public Channel discovery() {
        if (CommonUtils.isBlank(host) || port < 0) {
            throw new IllegalArgumentException("you must set both remote host and port");
        }
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler());
                            pipeline.addLast(new RpcRouteEncoder());
                            pipeline.addLast(new RpcRouteDecoder());
                            pipeline.addLast(consumerHandler);
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            return future.channel();
            //future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}

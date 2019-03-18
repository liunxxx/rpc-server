package com.liunx.rpc.publisher;

import com.liunx.rpc.common.handlers.RpcRouteDecoder;
import com.liunx.rpc.common.handlers.RpcRouteEncoder;
import com.liunx.rpc.publisher.handlers.RpcProviderHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * 发布服务
 */
public class ServicePublisher {

    private ServerBootstrap serverBootstrap;

    private final int port;

    protected ServicePublisher(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void publish() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler());
                            pipeline.addLast(new RpcRouteEncoder());
                            pipeline.addLast(new RpcRouteDecoder());
                            pipeline.addLast(new RpcProviderHandler());

                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

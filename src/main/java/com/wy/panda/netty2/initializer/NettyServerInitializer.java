package com.wy.panda.netty2.initializer;

import com.wy.panda.bootstrap.ServerConfig;
import com.wy.panda.mvc.DispatchServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;

public abstract class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    protected EventExecutorGroup eventExecutors;
    protected DispatchServlet servlet;
    protected ServerConfig config;

    public NettyServerInitializer(DispatchServlet servlet, EventExecutorGroup eventExecutors, ServerConfig config) {
        this.servlet = servlet;
        this.eventExecutors = eventExecutors;
        this.config = config;
    }

    public abstract void initBootstrap(ServerBootstrap bootstrap);

}

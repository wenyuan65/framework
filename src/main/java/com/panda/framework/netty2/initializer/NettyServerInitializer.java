package com.panda.framework.netty2.initializer;

import com.panda.framework.bootstrap.ServerConfig;
import com.panda.framework.mvc.DispatchServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public abstract class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    protected DispatchServlet servlet;
    protected ServerConfig config;

    public NettyServerInitializer(DispatchServlet servlet, ServerConfig config) {
        this.servlet = servlet;
        this.config = config;
    }

    public abstract void initBootstrap(ServerBootstrap bootstrap);

}

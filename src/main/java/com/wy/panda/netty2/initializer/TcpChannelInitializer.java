package com.wy.panda.netty2.initializer;

import com.wy.panda.bootstrap.ServerConfig;
import com.wy.panda.mvc.DispatchServlet;
import com.wy.panda.netty2.handler.DispatchChannelHandler;
import com.wy.panda.netty2.handler.PandaTcpMessageDecoder;
import com.wy.panda.netty2.handler.PandaTcpRequestDecoder;
import com.wy.panda.netty2.handler.PandaTcpResponseEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;

public class TcpChannelInitializer extends NettyServerInitializer {

	public TcpChannelInitializer(DispatchServlet servlet, EventExecutorGroup eventExecutors, ServerConfig config) {
		super(servlet, eventExecutors, config);
	}

	@Override
	public void initBootstrap(ServerBootstrap bootstrap) {
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, WriteBufferWaterMark.DEFAULT);
		bootstrap.childOption(ChannelOption.SO_BACKLOG, 1024);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);

		bootstrap.childHandler(this);
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline cp = ch.pipeline();
		cp.addLast(eventExecutors, new PandaTcpMessageDecoder());
		cp.addLast(eventExecutors, new PandaTcpRequestDecoder());
		cp.addLast(eventExecutors, new PandaTcpResponseEncoder());
		
		// 添加command处理器
		cp.addLast(new DispatchChannelHandler(servlet, config.isUseSession()));
	}

}

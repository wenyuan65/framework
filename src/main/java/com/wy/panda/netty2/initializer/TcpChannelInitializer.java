package com.wy.panda.netty2.initializer;

import com.wy.panda.bootstrap.ServerConfig;
import com.wy.panda.mvc.DispatchServlet;
import com.wy.panda.netty2.handler.DispatchChannelHandler;
import com.wy.panda.netty2.handler.PandaTcpMessageDecoder;
import com.wy.panda.netty2.handler.PandaTcpRequestDecoder;
import com.wy.panda.netty2.handler.PandaTcpResponseEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;

public class TcpChannelInitializer extends ChannelInitializer<SocketChannel> {

	private EventExecutorGroup eventExecutors;
	private DispatchServlet servlet;
	private ServerConfig config;

	public TcpChannelInitializer(DispatchServlet servlet, EventExecutorGroup eventExecutors, ServerConfig config) {
		this.servlet = servlet;
		this.eventExecutors = eventExecutors;
		this.config = config;
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

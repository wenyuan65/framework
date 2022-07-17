package com.wy.panda.netty2.initializer;

import com.wy.panda.bootstrap.ServerConfig;
import com.wy.panda.mvc.DispatchServlet;
import com.wy.panda.netty2.handler.DispatchChannelHandler;
import com.wy.panda.netty2.handler.PandaHttpRequestDecoder;
import com.wy.panda.netty2.handler.PandaHttpResponseEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.EventExecutorGroup;

public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

	private EventExecutorGroup eventExecutors;
	private DispatchServlet servlet;
	private ServerConfig config;

	public HttpChannelInitializer(DispatchServlet servlet, EventExecutorGroup eventExecutors, ServerConfig config) {
		this.servlet = servlet;
		this.eventExecutors = eventExecutors;
		this.config = config;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline cp = ch.pipeline();
		cp.addLast(eventExecutors, new HttpResponseEncoder());
		cp.addLast(eventExecutors, new HttpRequestDecoder());
		cp.addLast(eventExecutors, new HttpObjectAggregator(1024 * 1024));
		cp.addLast(eventExecutors, new ChunkedWriteHandler());
		cp.addLast(eventExecutors, new PandaHttpResponseEncoder());
		cp.addLast(eventExecutors, new PandaHttpRequestDecoder());
		
		// 添加command处理器
		cp.addLast(new DispatchChannelHandler(servlet, config.isUseSession()));
	}

}

package com.wy.panda.netty2.initializer;

import com.wy.panda.bootstrap.ServerConfig;
import com.wy.panda.mvc.DispatchServlet;
import com.wy.panda.netty2.handler.DispatchChannelHandler;
import com.wy.panda.netty2.handler.PandaHttpRequestDecoder;
import com.wy.panda.netty2.handler.PandaHttpResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.EventExecutorGroup;

public class HttpChannelInitializer extends NettyServerInitializer {

	public HttpChannelInitializer(DispatchServlet servlet, EventExecutorGroup eventExecutors, ServerConfig config) {
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

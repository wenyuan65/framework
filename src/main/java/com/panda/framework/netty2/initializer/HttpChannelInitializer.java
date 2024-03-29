package com.panda.framework.netty2.initializer;

import com.panda.framework.bootstrap.ServerConfig;
import com.panda.framework.netty2.handler.DispatchChannelHandler;
import com.panda.framework.netty2.handler.PandaHttpRequestDecoder;
import com.panda.framework.mvc.DispatchServlet;
import com.panda.framework.netty2.handler.PandaHttpResponseEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpChannelInitializer extends NettyServerInitializer {

	public HttpChannelInitializer(DispatchServlet servlet, ServerConfig config) {
		super(servlet, config);
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
		cp.addLast(new HttpResponseEncoder());
		cp.addLast(new HttpRequestDecoder());
		cp.addLast(new HttpObjectAggregator(1024 * 1024));
		cp.addLast(new ChunkedWriteHandler());
		cp.addLast(new PandaHttpResponseEncoder());
		cp.addLast(new PandaHttpRequestDecoder());
		
		// 添加command处理器
		cp.addLast(new DispatchChannelHandler(servlet, config.isUseSession()));
	}

}

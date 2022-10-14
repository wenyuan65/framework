package com.wy.panda.netty2.initializer;

import com.wy.panda.bootstrap.ServerConfig;
import com.wy.panda.mvc.DispatchServlet;
import com.wy.panda.netty2.handler.*;
import com.wy.panda.rpc.handler.RpcResponseCodec;
import com.wy.panda.rpc.serilizable.Serializable;
import com.wy.panda.rpc.serilizable.SerializerManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;

public class TcpChannelInitializer extends NettyServerInitializer {

	public TcpChannelInitializer(DispatchServlet servlet, ServerConfig config) {
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
		Serializable serializer = SerializerManager.getInstance().getSerializer();

		ChannelPipeline cp = ch.pipeline();
		cp.addLast(new PandaTcpMessageDecoder());
		cp.addLast(new PandaTcpCommRequestDecoder());
		cp.addLast(new PandaTcpGlobalRequestDecoder());
		cp.addLast(new PandaTcpRpcRequestDecoder(config.isRpcCompress()));
		cp.addLast(new PandaTcpResponseEncoder());

//		cp.addLast(new RpcRequestCodec(config.isRpcCompress(), serializer));
		cp.addLast(new RpcResponseCodec(config.isRpcCompress(), serializer));

		// 添加command处理器
		cp.addLast(new DispatchChannelHandler(servlet, config.isUseSession()));
	}

}

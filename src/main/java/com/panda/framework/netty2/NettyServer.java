package com.panda.framework.netty2;

import java.util.Objects;

import com.panda.framework.netty2.initializer.NettyServerInitializer;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 服务器
 * @author wenyuan
 */
public class NettyServer {
	
	private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

	/** 服务器名称 */
	private String name;
	/** netty配置 */
	private NettyServerConfig config;
	/** 服务器初始化定制配置 */
	private NettyServerInitializer initializer;
	
	/** netty启动类 */
	private ServerBootstrap bootstrap = new ServerBootstrap(); 
	/** netty线程 */
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	
	public NettyServer(String name, NettyServerConfig config, NettyServerInitializer initializer) {
		Objects.requireNonNull(config, "Netty config cann't be null");
		Objects.requireNonNull(initializer, "Netty initializer cann't be null");
		
		this.name = name;
		this.config = config;
		this.initializer = initializer;
	}
	
	public void init() throws Exception {
		// EventLoop
		bossGroup = config.isEpoll() ? new EpollEventLoopGroup(config.getBossEventLoopNum()) : new NioEventLoopGroup(config.getBossEventLoopNum());
		workerGroup = config.isEpoll() ? new EpollEventLoopGroup(config.getWorkerEventLoopNum()) : new NioEventLoopGroup(config.getWorkerEventLoopNum());
		bootstrap.group(bossGroup, workerGroup);
		bootstrap.channel(config.isEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);

		bootstrap.option(ChannelOption.ALLOCATOR, config.isUsedPooled() ? PooledByteBufAllocator.DEFAULT : UnpooledByteBufAllocator.DEFAULT);

		initializer.initBootstrap(bootstrap);
	}
	
	public void start() {
		try {
			int port = config.getPort();
			
			ChannelFuture f = bootstrap.bind(port).sync();
			f.addListener(new GenericFutureListener<ChannelFuture>() {

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) {
						logger.info("{} bind on port {} success", name, port);
					} else {
						logger.info("{} bind on port {} fail", name, port);
					}
				}
			});
		} catch (Exception e) {
			logger.error("Netty server start error:", e);
			
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}

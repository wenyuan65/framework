package com.wy.panda.netty2;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
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
	/** channel初始化配置 */
	private ChannelInitializer<? extends Channel> initializer;
	
	/** netty启动类 */
	private ServerBootstrap bootstrap = new ServerBootstrap(); 
	/** netty线程 */
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	
	public NettyServer(String name, NettyServerConfig config, ChannelInitializer<? extends Channel> initializer) {
		Objects.requireNonNull(config, "Netty config cann't be null");
		Objects.requireNonNull(initializer, "Netty initializer cann't be null");
		
		this.name = name;
		this.config = config;
		this.initializer = initializer;
	}
	
	public void init() throws Exception {
		// EventLoop
		bossGroup = config.isEpoll() ? new EpollEventLoopGroup(config.getBossEventLoopNum()) 
				: new NioEventLoopGroup(config.getBossEventLoopNum());
		workerGroup = config.isEpoll() ? new EpollEventLoopGroup(config.getWorkerEventLoopNum()) 
				: new NioEventLoopGroup(config.getWorkerEventLoopNum());
		bootstrap.group(bossGroup, workerGroup);
		bootstrap.channel(config.isEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.ALLOCATOR, config.isUsedPooled() ? PooledByteBufAllocator.DEFAULT 
				: UnpooledByteBufAllocator.DEFAULT);
		
		Map<String, Object> options = config.getOptions();
		Map<String, Object> childOptions = config.getChildOptions();
		if (options != null) {
			for (Entry<String, Object> entry : options.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				bootstrap.option(ChannelOption.valueOf(key), value);
			}
		}
		if (childOptions != null) {
			for (Entry<String, Object> entry : childOptions.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				bootstrap.childOption(ChannelOption.valueOf(key), value);
			}
		}
		
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		
		bootstrap.childHandler(initializer);
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

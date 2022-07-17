package com.wy.panda.netty;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import com.wy.panda.common.ReflactUtil;
import com.wy.panda.netty.config.NettyConfig;
import com.wy.panda.netty2.NettyServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

public class NettyServerBootstrap implements BeanFactoryAware {

	private static final Logger log = LoggerFactory.getLogger(NettyServerBootstrap.class);
	
	private final NettyConfig config;
	
	private boolean started;

	private BeanFactory beanFactory;

	public NettyServerBootstrap(NettyConfig config) {
		if (config == null) {
			throw new RuntimeException("netty config is null");
		}
		
		this.config = config;
	}
	
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	public void start() throws ClassNotFoundException {
		if (started) {
			return;
		}
		started = true;
		
		ServerBootstrap bootstrap = new ServerBootstrap();
		// EventLoop
		int bossGroupThreadNum = Integer.parseInt(config.getBossEventLoopNum());
		int workerGroupThreadNum = Integer.parseInt(config.getWorkerEventLoopNum());
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(bossGroupThreadNum);
		NioEventLoopGroup workerGroup = new NioEventLoopGroup(workerGroupThreadNum);
		bootstrap.group(bossGroup, workerGroup);
		
		// channel class
		String channelClassName = config.getChannelClassName();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Class<?> channelClass = classLoader.loadClass(channelClassName);
		Class<? extends ServerChannel> serverChannelClass = ReflactUtil.cast(channelClass, ServerChannel.class);
		bootstrap.channel(serverChannelClass);
		
		// option
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
		
		// ChannelHandler
		final List<String> childHandlers = config.getChildHandlers();
		final BeanFactory beanFacroty = this.beanFactory;
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				
				for (String handler : childHandlers) {
					pipeline.addLast(beanFacroty.getBean(handler, ChannelHandler.class));
				}
			}
		});
		
		try {
			int port = Integer.parseInt(config.getPort());
			bootstrap.bind(port).sync();
			
			log.info("bind on port:{}", port);
		} catch (Exception e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
}

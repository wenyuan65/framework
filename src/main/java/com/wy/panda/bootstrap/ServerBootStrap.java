package com.wy.panda.bootstrap;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang3.StringUtils;

import com.wy.panda.bootstrap.initlize.ContextInitlizer;
import com.wy.panda.concurrent.DefaultThreadFactory;
import com.wy.panda.concurrent.DefaultUncaughtExceptionHandler;
import com.wy.panda.config.Configuration;
import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;
import com.wy.panda.mvc.DispatchServlet;
import com.wy.panda.mvc.ServletContext;
import com.wy.panda.mvc.config.DispatchServletConfig;
import com.wy.panda.mvc.intercept.Interceptor;
import com.wy.panda.netty2.NettyServer;
import com.wy.panda.netty2.NettyServerConfig;
import com.wy.panda.netty2.initializer.HttpChannelInitializer;
import com.wy.panda.netty2.initializer.HttpsChannelInitializer;
import com.wy.panda.netty2.initializer.TcpChannelInitializer;
import com.wy.panda.session.SessionManager;
import com.wy.panda.spring.ObjectFactory;

import io.netty.channel.nio.NioEventLoopGroup;

public class ServerBootStrap {
	
	private static final Logger log = LoggerFactory.getLogger(ServerBootStrap.class);

	public void startup() throws Throwable {
		System.setProperty("panda.output.enhanceclass", "true");
		Thread.currentThread().setContextClassLoader(new PandaClassLoader());
		
		// 读取配置文件
		ServerConfig config = loadConfig();
		
		// servlet应用环境
		ServletContext servletContext = new ServletContext();
		// 初始化系统环境
		initContext(servletContext, config);
		
		// 初始化DispatchServlet
		DispatchServlet servlet = initServletDispatch(servletContext, config);
		
		// 初始化服务器
		initProtocols(servlet, config);
		
		// session机制
		if (config.isUseSession()) {
			SessionManager.getInstance().start();
		}
		// 未捕获异常处理
		Thread.setDefaultUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler());
	}

	/**
	 * 初始化DispatchServlet
	 * @param servletContext
	 * @param config
	 * @return
	 * @throws ClassNotFoundException
	 * @throws Exception
	 * @throws Throwable
	 */
	private DispatchServlet initServletDispatch(ServletContext servletContext, ServerConfig config)
			throws ClassNotFoundException, Exception, Throwable {
		DispatchServletConfig servletConfig = new DispatchServletConfig();
		servletConfig.setCompress(config.isCompress());
		servletConfig.setScanPath(config.getScanPath());
		DispatchServlet servlet = new DispatchServlet(servletConfig, servletContext);
		
		// 添加拦截器
		String interceptorNames = config.getInterceptorNames();
		String[] interceptorNameArray = interceptorNames.split(",");
		List<Interceptor> interceptorList = new ArrayList<>();
		for (String interceptorName : interceptorNameArray) {
			Class<?> clazz = Class.forName(interceptorName);
			if (!Interceptor.class.isAssignableFrom(clazz)) {
				throw new Exception("unkown interceptor" + interceptorName);
			}
			
			Object obj = ObjectFactory.getObject(clazz, "");
			interceptorList.add((Interceptor)obj);
		}
		servlet.addInterceptors(interceptorList);
		servlet.init();
		
		return servlet;
	}

	/**
	 * 启动服务器，监听端口
	 * @param servlet
	 * @param config
	 * @throws Exception
	 */
	private void initProtocols(DispatchServlet servlet, ServerConfig config) throws Exception {
		int eventGroupNum = config.getMsgProcessEventGroupNum();
		NioEventLoopGroup group = new NioEventLoopGroup(eventGroupNum, 
				new DefaultThreadFactory("EventExecutor", eventGroupNum));
		
		boolean success = false;
		if (config.isHttpsEnable()) {
			SSLContext sslContext = createSSLContext(config);
			HttpsChannelInitializer channelInitializer = new HttpsChannelInitializer(servlet, group, sslContext, config);
			NettyServer server = new NettyServer("https", config.getHttpsServerConfig(), channelInitializer);
			server.init();
			server.start();
			success = true;
		}
		if (config.isHttpEnable()) {
			HttpChannelInitializer channelInitializer = new HttpChannelInitializer(servlet, group, config);
			NettyServer server = new NettyServer("http", config.getHttpServerConfig(), channelInitializer);
			server.init();
			server.start();
			success = true;
		}
		if (config.isTcpEnable()) {
			TcpChannelInitializer channelInitializer = new TcpChannelInitializer(servlet, group, config);
			NettyServer server = new NettyServer("tcp", config.getTcpServerConfig(), channelInitializer);
			server.init();
			server.start();
			success = true;
		}
		if (!success) {
			throw new Exception("no server boostraped");
		}
	}

	/**
	 * 应用环境初始化
	 * @param servletContext
	 * @param config
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws Exception
	 */
	private void initContext(ServletContext servletContext, ServerConfig config)
			throws Throwable {
		String contextInitlizerNames = config.getContextInitlizerNames();
		String[] contextInitlizerArray = contextInitlizerNames.split(",");
		for (String contextInitlizerName : contextInitlizerArray) {
			if (StringUtils.isBlank(contextInitlizerName)) {
				continue;
			}
			
			Class<?> clazz = Class.forName(contextInitlizerName);
			Object obj = clazz.newInstance();
			if (ContextInitlizer.class.isAssignableFrom(clazz)) {
				ContextInitlizer initlizer = (ContextInitlizer) obj;
				initlizer.initContext(servletContext, config);
			}
		}
		
		String contextServerInitListener = config.getContextServerInitListener();
		String[] contextServerInitListeners = contextServerInitListener.split(",");
		for (String listener : contextServerInitListeners) {
			if (StringUtils.isBlank(listener)) {
				continue;
			}
			
			Class<?> clazz = Class.forName(listener);
			if (InitListener.class.isAssignableFrom(clazz)) {
				InitListener initListener = (InitListener) ObjectFactory.getObject(clazz, null);
				initListener.init(servletContext);
			}
		}
	}
	
	/**
	 * 读取配置文件
	 * @return
	 */
	private ServerConfig loadConfig() {
		ServerConfig config = new ServerConfig();
		
		String useSession = Configuration.getProperty("session.enable");
		config.setUseSession("true".equalsIgnoreCase(useSession));
		
		String ContextInitlizerNames = Configuration.getProperty("context.initlizers");
		config.setContextInitlizerNames(ContextInitlizerNames);
		
		String contextServerInitListener = Configuration.getProperty("context.server.initListener");
		config.setContextServerInitListener(contextServerInitListener);
		
		String compress = Configuration.getProperty("compress");
		config.setCompress("true".equalsIgnoreCase(compress));
		
		String scanPath = Configuration.getProperty("scan.path");
		config.setScanPath(scanPath.trim());
		
		String interceptors = Configuration.getProperty("interceptors");
		config.setInterceptorNames(interceptors.trim());
		
		String msgProcessEventGroupNum = Configuration.getProperty("msgProcessEventGroupNum");
		if (StringUtils.isNotBlank(msgProcessEventGroupNum)) {
			config.setMsgProcessEventGroupNum(Integer.parseInt(msgProcessEventGroupNum));
		}
		
		String usePool = Configuration.getProperty("usePool");
		if (StringUtils.isNotBlank(usePool)) {
			config.setUsePool(Boolean.parseBoolean(usePool));
		}
		
		String epoll = Configuration.getProperty("epoll");
		if (StringUtils.isNotBlank(epoll)) {
			config.setEpoll(Boolean.parseBoolean(epoll));
		}
		
		String httpEnable = Configuration.getValue("http", "enable");
		config.setHttpEnable("true".equalsIgnoreCase(httpEnable));
		if (config.isHttpEnable()) {
			NettyServerConfig serverConfig = parseProtocolsConfig("http");
			serverConfig.setUsedPooled(config.isUsePool());
			serverConfig.setEpoll(config.isEpoll());
			config.setHttpServerConfig(serverConfig);
		}
		
		String tcpEnable = Configuration.getValue("tcp", "enable");
		config.setTcpEnable("true".equalsIgnoreCase(tcpEnable));
		if (config.isTcpEnable()) {
			NettyServerConfig serverConfig = parseProtocolsConfig("tcp");
			serverConfig.setUsedPooled(config.isUsePool());
			serverConfig.setEpoll(config.isEpoll());
			config.setTcpServerConfig(serverConfig);
		}
		
		String httpsEnable = Configuration.getValue("https", "enable");
		config.setHttpsEnable("true".equalsIgnoreCase(httpsEnable));
		if (config.isHttpsEnable()) {
			NettyServerConfig serverConfig = parseProtocolsConfig("https");
			serverConfig.setUsedPooled(config.isUsePool());
			serverConfig.setEpoll(config.isEpoll());
			config.setHttpsServerConfig(serverConfig);
		}
		
		return config;
	}
	
	/**
	 * 解析网络协议配置
	 * @param protocol
	 * @return
	 */
	public NettyServerConfig parseProtocolsConfig(String protocol) {
		NettyServerConfig nettyConfig = new NettyServerConfig();
		nettyConfig.setServerName(protocol);
		
		String value = Configuration.getValue(protocol, "bossThreadNum");
		if (StringUtils.isNotBlank(value)) {
			nettyConfig.setBossEventLoopNum(Integer.parseInt(value));
		}
		
		value = Configuration.getValue(protocol, "workerThreadNum");
		if (StringUtils.isNotBlank(value)) {
			nettyConfig.setWorkerEventLoopNum(Integer.parseInt(value));
		}
		
		try {
			value = Configuration.getValue(protocol, "port");
			nettyConfig.setPort(Integer.parseInt(value));
		} catch (Exception e) {
			log.error("protocol:{} port config error", protocol);
		}
		// TODO: channel配置
//				http.option.TCP_NODELAY = true
//				http.childOption.TCP_NODELAY = true
		return nettyConfig;
	}
	
	/**
	 * 读取https端口的sslContext
	 * @param config
	 * @return
	 * @throws Exception
	 */
	private SSLContext createSSLContext(ServerConfig config) throws Exception {
		String protocol = Configuration.getProperty("ssl.protocol");
		String keyAlgorithm = Configuration.getProperty("ssl.key.algorithm");
		String keyType = Configuration.getProperty("ssl.key.type");
		String keyStore = Configuration.getProperty("ssl.key.store");
		String keyStorePassword = Configuration.getProperty("ssl.key.storePassword");
		String trustAlgorithm = Configuration.getProperty("ssl.trust.algorithm");
		String trustType = Configuration.getProperty("ssl.trust.type");
		String trustStore = Configuration.getProperty("ssl.trust.store");
		String trustStorePassword = Configuration.getProperty("ssl.trust.storePassword");
		String keyKeyPassword = Configuration.getProperty("ssl.key.keyPassword");
		String needClientAuth = Configuration.getProperty("ssl.needClientAuth");
		
		log.info("{} ==> {}", "protocol", protocol);
		log.info("{} ==> {}", "keyAlgorithm", keyAlgorithm);
		log.info("{} ==> {}", "keyType", keyType);
		log.info("{} ==> {}", "keyStore", keyStore);
		log.info("{} ==> {}", "keyStorePassword", keyStorePassword);
		log.info("{} ==> {}", "trustAlgorithm", trustAlgorithm);
		log.info("{} ==> {}", "trustType", trustType);
		log.info("{} ==> {}", "trustStore", trustStore);
		log.info("{} ==> {}", "trustStorePassword", trustStorePassword);
		log.info("{} ==> {}", "keyKeyPassword", keyKeyPassword);
		log.info("{} ==> {}", "needClientAuth", needClientAuth);
		
		SSLContext ctx = SSLContext.getInstance(protocol);
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(keyAlgorithm);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(trustAlgorithm);
		KeyStore ks = KeyStore.getInstance(keyType);
		KeyStore tks = KeyStore.getInstance(trustType);
		ks.load(new FileInputStream(keyStore), keyStorePassword.toCharArray());
		tks.load(new FileInputStream(trustStore), trustStorePassword.toCharArray());
		kmf.init(ks, keyKeyPassword.toCharArray());
		tmf.init(tks);
		ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		
		config.setNeedClientAuth("true".equalsIgnoreCase(needClientAuth));
		
		return ctx;
	}
	
}

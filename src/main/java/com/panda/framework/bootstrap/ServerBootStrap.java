package com.panda.framework.bootstrap;

import com.panda.framework.bootstrap.initlize.ContextInitlizer;
import com.panda.framework.concurrent.DefaultUncaughtExceptionHandler;
import com.panda.framework.session.SessionManager;
import com.panda.framework.common.SystemProperty;
import com.panda.framework.config.Configuration;
import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;
import com.panda.framework.mvc.DispatchServlet;
import com.panda.framework.mvc.ServletContext;
import com.panda.framework.mvc.config.DispatchServletConfig;
import com.panda.framework.mvc.intercept.Interceptor;
import com.panda.framework.netty2.NettyServer;
import com.panda.framework.netty2.NettyServerConfig;
import com.panda.framework.netty2.initializer.NettyServerInitializer;
import com.panda.framework.spring.ObjectFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ServerBootStrap {
	
	private static final Logger log = LoggerFactory.getLogger(ServerBootStrap.class);

	public void startup() throws Throwable {
		System.setProperty(SystemProperty.ENHANCE_CLASS_OUTPUT, "true");
		Thread.currentThread().setContextClassLoader(new PandaClassLoader());
		
		// 读取配置文件
		ServerConfig config = loadConfig();
		
		// servlet应用环境
		ServletContext servletContext = new ServletContext();
		// 初始化系统环境
		initContext(servletContext, config);
		
		// 初始化DispatchServlet
		final DispatchServlet servlet = initServletDispatch(servletContext, config);
		
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
		servletConfig.setCoreThreadPoolSize(config.getCoreThreadPoolSize());
		servletConfig.setAsyncThreadPoolSize(config.getAsyncThreadPoolSize());

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
		boolean success = false;
		if (config.isHttpsEnable()) {
			initServer("https", servlet, config, config.getHttpsServerConfig());
			success = true;
		}
		if (config.isHttpEnable()) {
			initServer("http", servlet, config, config.getHttpServerConfig());
			success = true;
		}
		if (config.isTcpEnable()) {
			initServer("tcp", servlet, config, config.getTcpServerConfig());
			success = true;
		}
		if (!success) {
			throw new Exception("no server boostraped");
		}
	}

	private void initServer(String serverName, DispatchServlet servlet, ServerConfig config, NettyServerConfig httpsServerConfig) throws Exception {
		String nettyServerInitializerClazz = httpsServerConfig.getNettyServerInitializerClazz();
		
		Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(nettyServerInitializerClazz);
		Constructor<?> constructor = clazz.getDeclaredConstructor(DispatchServlet.class, EventExecutorGroup.class, ServerConfig.class);
		NettyServerInitializer nettyServerInitializer = (NettyServerInitializer)constructor.newInstance(servlet, config);

		NettyServer server = new NettyServer(serverName, httpsServerConfig, nettyServerInitializer);
		server.init();
		server.start();
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
				InitListener initListener = ObjectFactory.getObject(clazz, null);
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
		
		setBooleanProperties(config, "useSession", "session.enable");
		setStringProperties(config, "ContextInitlizerNames", "context.initlizers", false);
		setStringProperties(config, "contextServerInitListener", "context.server.initListener", false);
		setBooleanProperties(config, "compress", "compress");

		setStringProperties(config, "scanPath", "scan.path", false);
		setStringProperties(config, "interceptorNames", "interceptors", false);
		setStringProperties(config, "coreThreadPoolSize", "coreThreadPoolSize", true);
		setStringProperties(config, "asyncThreadPoolSize", "asyncThreadPoolSize", true);
		setBooleanProperties(config, "usePool", "usePool");
		setBooleanProperties(config, "epoll", "epoll");

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
		
		setIntProperties(nettyConfig, "bossEventLoopNum", protocol + "." + "bossThreadNum");
		setIntProperties(nettyConfig, "workerEventLoopNum", protocol + "." + "workerThreadNum");
		setStringProperties(nettyConfig, "nettyServerInitializerClazz", protocol + "." + "serverInitializer", true);
		setIntProperties(nettyConfig, "port", protocol + "." + "port");

		return nettyConfig;
	}

	private void setStringProperties(Object config, String fieldName, String propertyName, boolean useDefaultWhenBlank) {
		try {
			String value = Configuration.getProperty(propertyName);
			if (useDefaultWhenBlank && StringUtils.isBlank(value)) {
				return;
			}

			Field field = config.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(config, value);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void setIntProperties(Object config, String fieldName, String propertyName) {
		try {
			String value = Configuration.getProperty(propertyName);
			if (StringUtils.isBlank(value)) {
				return;
			}

			Field field = config.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(config, Integer.parseInt(value));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void setBooleanProperties(Object config, String fieldName, String propertyName) {
		try {
			String value = Configuration.getProperty(propertyName);
			boolean boolValue = "true".equalsIgnoreCase(value);

			Field field = config.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(config, boolValue);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

}

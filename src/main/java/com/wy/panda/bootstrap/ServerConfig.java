package com.wy.panda.bootstrap;

import com.wy.panda.netty2.NettyServerConfig;

public class ServerConfig {
	
	/** 当前服务器是否使用session */
	private boolean useSession = false;
	
	/** 环境初始化 */
	private String ContextInitlizerNames = "";
	
	/** 服务器初始化 */
	private String contextServerInitListener = "";

	private String applicationContextPath = "applicationContext.xml";
	
	/** 接受的数据是否启用压缩 */
	private boolean compress = false;
	/** 接受的rpc请求数据是否启用压缩 */
	private boolean rpcCompress = false;
	/** servlet扫描路径 */
	private String scanPath = "";
	
	private String interceptorNames = "";
	
	/** 核心线程池线程数 */
	private int coreThreadPoolSize = 8;
	/** 异步线程池线程数 */
	private int asyncThreadPoolSize = 4;

	/** netty使用内存池 */
	private boolean usePool = true;
	
	/** netty使用epoll模型 */
	private boolean epoll = true;
	
	/** 启动http服务器 */
	private boolean httpEnable = false;
	/** http服务器配置 */
	private NettyServerConfig httpServerConfig = new NettyServerConfig();
	
	/** 启动https服务器 */
	private boolean httpsEnable = false;
	/** https服务器配置 */
	private NettyServerConfig httpsServerConfig = new NettyServerConfig();
	/** 是否开启客户端验证 */
	private boolean needClientAuth = false; 
	
	/** 启动tcp服务器 */
	private boolean tcpEnable = false;
	/** tcp服务器配置 */
	private NettyServerConfig tcpServerConfig = new NettyServerConfig();
	
	public boolean isUseSession() {
		return useSession;
	}
	public void setUseSession(boolean useSession) {
		this.useSession = useSession;
	}
	public String getContextServerInitListener() {
		return contextServerInitListener;
	}
	public void setContextServerInitListener(String contextServerInitListener) {
		this.contextServerInitListener = contextServerInitListener;
	}
	public String getContextInitlizerNames() {
		return ContextInitlizerNames;
	}
	public void setContextInitlizerNames(String contextInitlizerNames) {
		ContextInitlizerNames = contextInitlizerNames;
	}
	public String getApplicationContextPath() {
		return applicationContextPath;
	}
	public void setApplicationContextPath(String applicationContextPath) {
		this.applicationContextPath = applicationContextPath;
	}
	public boolean isCompress() {
		return compress;
	}
	public void setCompress(boolean compress) {
		this.compress = compress;
	}
	public boolean isRpcCompress() {
		return rpcCompress;
	}
	public void setRpcCompress(boolean rpcCompress) {
		this.rpcCompress = rpcCompress;
	}
	public String getScanPath() {
		return scanPath;
	}
	public void setScanPath(String scanPath) {
		this.scanPath = scanPath;
	}
	public String getInterceptorNames() {
		return interceptorNames;
	}
	public void setInterceptorNames(String interceptorNames) {
		this.interceptorNames = interceptorNames;
	}
	public boolean isUsePool() {
		return usePool;
	}
	public void setUsePool(boolean usePool) {
		this.usePool = usePool;
	}
	public boolean isEpoll() {
		return epoll;
	}
	public void setEpoll(boolean epoll) {
		this.epoll = epoll;
	}
	public int getCoreThreadPoolSize() {
		return coreThreadPoolSize;
	}
	public void setCoreThreadPoolSize(int coreThreadPoolSize) {
		this.coreThreadPoolSize = coreThreadPoolSize;
	}
	public int getAsyncThreadPoolSize() {
		return asyncThreadPoolSize;
	}
	public void setAsyncThreadPoolSize(int asyncThreadPoolSize) {
		this.asyncThreadPoolSize = asyncThreadPoolSize;
	}
	public boolean isHttpEnable() {
		return httpEnable;
	}
	public void setHttpEnable(boolean httpEnable) {
		this.httpEnable = httpEnable;
	}
	public NettyServerConfig getHttpServerConfig() {
		return httpServerConfig;
	}
	public void setHttpServerConfig(NettyServerConfig httpServerConfig) {
		this.httpServerConfig = httpServerConfig;
	}
	public boolean isHttpsEnable() {
		return httpsEnable;
	}
	public void setHttpsEnable(boolean httpsEnable) {
		this.httpsEnable = httpsEnable;
	}
	public NettyServerConfig getHttpsServerConfig() {
		return httpsServerConfig;
	}
	public void setHttpsServerConfig(NettyServerConfig httpsServerConfig) {
		this.httpsServerConfig = httpsServerConfig;
	}
	public boolean isNeedClientAuth() {
		return needClientAuth;
	}
	public void setNeedClientAuth(boolean needClientAuth) {
		this.needClientAuth = needClientAuth;
	}
	public boolean isTcpEnable() {
		return tcpEnable;
	}
	public void setTcpEnable(boolean tcpEnable) {
		this.tcpEnable = tcpEnable;
	}
	public NettyServerConfig getTcpServerConfig() {
		return tcpServerConfig;
	}
	public void setTcpServerConfig(NettyServerConfig tcpServerConfig) {
		this.tcpServerConfig = tcpServerConfig;
	}
	
}

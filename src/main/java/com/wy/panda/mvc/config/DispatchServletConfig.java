package com.wy.panda.mvc.config;

public class DispatchServletConfig {

	private String scanPath;
	
	private boolean compress;
	/** 核心线程池线程数 */
	private int coreThreadPoolSize;
	/** 异步线程池线程数 */
	private int asyncThreadPoolSize;
	

	public String getScanPath() {
		return scanPath;
	}

	public void setScanPath(String scanPath) {
		this.scanPath = scanPath;
	}

	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
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
}

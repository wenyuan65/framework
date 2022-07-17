package com.wy.panda.mvc.config;

public class DispatchServletConfig {

	private String scanPath;
	
	private boolean compress;

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
	
}

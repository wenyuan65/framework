package com.panda.framework.protocol;

public enum Protocols {
	
	CLIENT(1),
	CLUSTER_CLIENT(2),
	RPC(3),
	PUSH(4);
	
	private int protocolType;
	
	private Protocols(int protocolType) {
		this.protocolType = protocolType;
	}
	
	public int getProtocolType() {
		return protocolType;
	}
	
}

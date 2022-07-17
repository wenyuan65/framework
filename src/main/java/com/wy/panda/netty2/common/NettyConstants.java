package com.wy.panda.netty2.common;

public class NettyConstants {

//	packageType 	1字节，0滚服包，1大区包（此类型下，包体serverType、serverId为0）
	/** 包类型， 0滚服 */
	public static final int PACKAGE_TYPE_COMM_MODEL = 0;
	/** 包类型， 0集群 */
	public static final int PACKAGE_TYPE_CLUSTER_MODEL = 1;
	
	/** 包类型， command部分长度 */
	public static final int PACKAGE_COMMAND_LENGTH = 32;
	
	
	
}

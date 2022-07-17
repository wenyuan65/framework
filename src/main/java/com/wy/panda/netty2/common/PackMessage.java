package com.wy.panda.netty2.common;

import io.netty.buffer.ByteBuf;

public class PackMessage {

	private int length;
	private int packageType;
	private int requestId;
	private ByteBuf packBuf;
	
	private int serverType;
	private int serverId;
	private String command;
	private byte[] data;
	
	public PackMessage(int length, int packageType, int requestId, ByteBuf packBuf) {
		this.length = length;
		this.packageType = packageType;
		this.requestId = requestId;
		this.packBuf = packBuf;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPackageType() {
		return packageType;
	}

	public void setPackageType(int packageType) {
		this.packageType = packageType;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public ByteBuf getPackBuf() {
		return packBuf;
	}

	public void setPackBuf(ByteBuf packBuf) {
		this.packBuf = packBuf;
	}

	public int getServerType() {
		return serverType;
	}

	public void setServerType(int serverType) {
		this.serverType = serverType;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
}

package com.wy.panda.rpc.handler;

import java.util.List;

import com.wy.panda.common.TextUtil;
import com.wy.panda.protocol.Protocols;
import com.wy.panda.rpc.RpcRequest;
import com.wy.panda.rpc.RpcRequestParams;
import com.wy.panda.rpc.serilizable.Serializable;
import com.wy.panda.util.ByteUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

public class RpcRequestCodec extends ByteToMessageCodec<RpcRequest>{

	private final boolean compress;
	private final Serializable serializer;
	
	public RpcRequestCodec(boolean compress, Serializable serializer) {
		this.compress = compress;
		this.serializer = serializer;
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, RpcRequest msg, ByteBuf out) throws Exception {
		byte[] content = serializer.writeObject(msg.getParam());
		if (compress) {
			content = ByteUtils.compress(content);
		}
		
		byte[] commandBuf = new byte[32];
		byte[] command = TextUtil.toByte(msg.getCommand());
		
		System.arraycopy(command, 0, commandBuf, 0, Math.min(commandBuf.length, command.length));
		
		int len = 41 + content.length;// 4 + 1 + 4 + 32 + content.length
		ByteBuf buffer = ctx.alloc().buffer(len);
		buffer.writeInt(len);
		buffer.writeByte(Protocols.RPC.getProtocolType()); // 协议类型
		buffer.writeInt(msg.getRequestId());
		buffer.writeBytes(commandBuf);
		buffer.writeBytes(content);
		
		// 发送
		out.writeBytes(buffer, len);
		
		buffer.release();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int readerIndex = in.readerIndex();
		int len = in.getInt(readerIndex); // 4 + 1 + 4 + 32
		if (in.readableBytes() < len || len < 41) {
			return;
		}
		
		byte protocolType = in.getByte(readerIndex + 4);
		if (protocolType != Protocols.RPC.getProtocolType()) {
			return;
		}
		
		in.skipBytes(5);
		byte[] commandBuf = new byte[32];
		byte[] content = new byte[len - 41];
		int requestId = in.readInt();
		in.readBytes(commandBuf);
		in.readBytes(content);
		
		String command = TextUtil.toString(commandBuf).trim();
		if (compress) {
			content = ByteUtils.decompress(content);
		}
		Object result = serializer.readObject(content);
		
		RpcRequest request = new RpcRequest();
		request.setRequestId(requestId);
		request.setCommand(command);
		request.setParam((RpcRequestParams) result);
		
		out.add(request);
	}

}

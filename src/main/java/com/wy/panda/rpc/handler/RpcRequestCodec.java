package com.wy.panda.rpc.handler;

import com.wy.panda.netty2.common.PackType;
import com.wy.panda.rpc.RpcRequest;
import com.wy.panda.rpc.RpcRequestParams;
import com.wy.panda.rpc.serilizable.Serializable;
import com.wy.panda.util.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

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
		
		int command1 = msg.getCommand();
		int len = 13 + content.length;// 4 + 1 + 4 + 4 + content.length
		ByteBuf buffer = ctx.alloc().buffer(len);
		buffer.writeInt(len);
		buffer.writeByte(PackType.RpcRequest.getPackType()); // 协议类型
		buffer.writeInt(msg.getRequestId());
		buffer.writeInt(command1);
		buffer.writeBytes(content);
		
		// 发送
		out.writeBytes(buffer, len);
		
		buffer.release();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int readerIndex = in.readerIndex();
		int len = in.getInt(readerIndex); // 4 + 1 + 4 + 4
		if (in.readableBytes() < len || len < 13) {
			return;
		}
		
		byte protocolType = in.getByte(readerIndex + 4);
		if (protocolType != PackType.RpcRequest.getPackType()) {
			return;
		}
		
		in.skipBytes(5);
		byte[] content = new byte[len - 41];
		int requestId = in.readInt();
		int command = in.readInt();
		in.readBytes(content);
		
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

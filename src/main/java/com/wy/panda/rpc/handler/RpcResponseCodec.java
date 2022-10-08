package com.wy.panda.rpc.handler;

import com.wy.panda.netty2.common.PackType;
import com.wy.panda.rpc.RpcResponse;
import com.wy.panda.rpc.serilizable.Serializable;
import com.wy.panda.util.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class RpcResponseCodec extends ByteToMessageCodec<RpcResponse> {
	
	private final boolean compress;
	private final Serializable serializer;
	
	public RpcResponseCodec(boolean compress, Serializable serializer) {
		this.compress = compress;
		this.serializer = serializer;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, RpcResponse msg, ByteBuf out) throws Exception {
		byte[] content = serializer.writeObject(msg.getResult());
		if (compress) {
			content = ByteUtils.compress(content);
		}
		
		int len = 4 + 1 + 4 + content.length;
		ByteBuf buffer = ctx.alloc().buffer(len);
		buffer.writeInt(len);
		buffer.writeByte(PackType.RpcRequest.getPackType());
		buffer.writeInt(msg.getRequestId());
		buffer.writeBytes(content);
		
		// 发送
		out.writeBytes(buffer, len);
		
		buffer.release();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int readerIndex = in.readerIndex();
		int len = in.getInt(readerIndex); // 4 + 1 + 4
		if (in.readableBytes() < len || len - 9 < 0) {
			return;
		}
		
		byte protocolType = in.getByte(readerIndex + 4);
		if (protocolType != PackType.RpcRequest.getPackType()) {
			return;
		}
		
		in.skipBytes(5);
		int requestId = in.readInt();
		byte[] content = new byte[len - 9];
		in.readBytes(content);
		
		if (compress) {
			content = ByteUtils.decompress(content);
		}
		
		Object result = serializer.readObject(content);
		RpcResponse response = new RpcResponse(requestId);
		response.setResult(result);
		
		out.add(response);
	}

}

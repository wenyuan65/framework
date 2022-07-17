package com.wy.panda.netty2.handler;

import java.util.List;

import com.wy.panda.netty2.common.PackMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 解包器, 包体格式：
 * length 		4字节
 * packageType 	1字节，0滚服包
 * requestId 	4字节，0推送给前端消息或者http请求，正数前端请求，负数内部请求
 * command		32字节
 * data			n字节
 * 
 * @author wenyuan
 */
public class PandaTcpMessageDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int dataLen = in.getInt(in.readerIndex());
		if (dataLen == 0 || in.readableBytes() < dataLen + 4) {
			return;
		}
		
		ByteBuf packBuf = in.readBytes(dataLen + 4); 
		int length = packBuf.readInt();
		int packageType = (int)packBuf.readByte();
		int requestId = packBuf.readInt();
		PackMessage packMsg = new PackMessage(length, packageType, requestId, packBuf);
		
		out.add(packMsg);
	}
	
}

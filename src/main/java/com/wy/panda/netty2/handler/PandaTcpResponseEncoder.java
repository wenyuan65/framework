package com.wy.panda.netty2.handler;

import com.wy.panda.mvc.InvokeResult;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * length 		4字节
 * packageType 	1字节，0滚服包，1大区包（此类型下，包体serverType、serverId为0）
 * requestId 	4字节，0推送给前端消息或者http请求，正数前端请求，负数内部请求
 * data			n字节
 * 
 * @author wenyuan
 */
public class PandaTcpResponseEncoder extends MessageToByteEncoder<InvokeResult>{

	@Override
	protected void encode(ChannelHandlerContext ctx, InvokeResult msg, ByteBuf out) throws Exception {
		byte[] result = msg.getResult();
		int requestId = msg.getRequestId();
		
		out.writeInt(result.length + 5);
		out.writeByte(0);
		out.writeInt(requestId);
		out.writeBytes(result);
	}

}

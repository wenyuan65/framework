package com.panda.framework.netty2.handler;

import com.panda.framework.common.TextUtil;
import com.panda.framework.netty2.common.PackMessage;
import com.panda.framework.netty2.common.PackType;
import com.panda.framework.mvc.common.ProtocolType;
import com.panda.framework.mvc.domain.Request;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

/**
 * 解析滚服包
 * length 		4字节
 * packageType 	1字节
 * requestId 	4字节
 * command		4字节
 * serverType 	4字节
 * serverId 	4字节
 * data			n字节
 * @author wenyuan
 */
public class PandaTcpGlobalRequestDecoder extends PandaPackMessageDecoder {

	@Override
	protected void doDecode(ChannelHandlerContext ctx, PackMessage msg, List<Object> out) throws Exception {
		ByteBuf packBuf = msg.getPackBuf();

		Request request = new Request();
		InetSocketAddress clientAddress = (InetSocketAddress)ctx.channel().remoteAddress();
		request.setIp(clientAddress.getHostString());
		request.setRequestId(msg.getRequestId());
		request.setPlayerId(0);
		request.setProtocol(ProtocolType.TCP);
		request.setCode(msg.getCommand());

		// 请求目标服务器信息
		msg.setServerType(packBuf.readInt());
		msg.setServerId(packBuf.readInt());

		// data
		ByteBuf dataBuf = Unpooled.buffer(msg.getLength() - 17);
		packBuf.readBytes(dataBuf);
		request.setContents(dataBuf.array());
		dataBuf.release();
		packBuf.release();

		String params = TextUtil.toString(request.getContents());
		Map<String, String> parameterMap = TextUtil.parseParameterMap(params);
		request.setParamMap(parameterMap);

		// 本地处理
		out.add(request);
	}

	@Override
	protected boolean validatePackType(int packType) {
		return packType == PackType.GlobalRequest.getPackType();
	}
}

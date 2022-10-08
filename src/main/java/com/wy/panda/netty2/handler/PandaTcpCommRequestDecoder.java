package com.wy.panda.netty2.handler;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import com.wy.panda.common.TextUtil;
import com.wy.panda.mvc.common.ProtocolType;
import com.wy.panda.mvc.domain.Request;
import com.wy.panda.netty2.common.NettyConstants;
import com.wy.panda.netty2.common.PackMessage;

import com.wy.panda.netty2.common.PackType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

/**
 * 解析滚服包
 * length 		4字节
 * packageType 	1字节，0滚服包，1大区包（此类型下，包体serverType、serverId为0）
 * requestId 	4字节，0推送给前端消息或者http请求，正数前端请求，负数内部请求
 * command		4字节
 * data			n字节
 * @author wenyuan
 */
public class PandaTcpCommRequestDecoder extends PandaPackMessageDecoder {

	@Override
	protected void doDecode(ChannelHandlerContext ctx, PackMessage msg, List<Object> out) throws Exception {
		ByteBuf packBuf = msg.getPackBuf();

		int lenBeforeData = 9;
		Request request = new Request();
		InetSocketAddress clientAddress = (InetSocketAddress)ctx.channel().remoteAddress();
		request.setIp(clientAddress.getHostString());
		request.setRequestId(msg.getRequestId());
		request.setPlayerId(0);
		request.setProtocol(ProtocolType.TCP);
		request.setCode(msg.getCommand());

		// data
		ByteBuf dataBuf = Unpooled.buffer(msg.getLength() - lenBeforeData);
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
		return packType == PackType.CommRequest.getPackType();
	}
}

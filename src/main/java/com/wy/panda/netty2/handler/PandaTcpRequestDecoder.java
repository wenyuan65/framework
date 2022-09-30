package com.wy.panda.netty2.handler;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import com.wy.panda.common.TextUtil;
import com.wy.panda.mvc.common.ProtocolType;
import com.wy.panda.mvc.domain.Request;
import com.wy.panda.netty2.common.NettyConstants;
import com.wy.panda.netty2.common.PackMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * 解析滚服包
 * length 		4字节
 * packageType 	1字节，0滚服包，1大区包（此类型下，包体serverType、serverId为0）
 * requestId 	4字节，0推送给前端消息或者http请求，正数前端请求，负数内部请求
 * command		4字节
 * data			n字节
 * 或
 * length 		4字节
 * packageType 	1字节，0滚服包，1大区包（此类型下，包体serverType、serverId为0）
 * requestId 	4字节，0推送给前端消息或者http请求，正数前端请求，负数内部请求
 * command		4字节
 * serverType 	4字节
 * serverId 	4字节
 * data			n字节
 * @author wenyuan
 */
public class PandaTcpRequestDecoder extends MessageToMessageDecoder<PackMessage> {

	@Override
	protected void decode(ChannelHandlerContext ctx, PackMessage msg, List<Object> out) throws Exception {
		ByteBuf packBuf = msg.getPackBuf();

		int lenBeforeData = 9;
		Request request = new Request();
		InetSocketAddress clientAddress = (InetSocketAddress)ctx.channel().remoteAddress();
		request.setIp(clientAddress.getHostString());
		request.setRequestId(msg.getRequestId());
		request.setPlayerId(0);
		request.setProtocol(ProtocolType.TCP);
		request.setCode(packBuf.readInt());

		// 中间加入8字节的服务器信息
		if (msg.getPackageType() == NettyConstants.PACKAGE_TYPE_CLUSTER_MODEL) {
			msg.setServerType(packBuf.readInt());
			msg.setServerId(packBuf.readInt());
			// TODO: 远程转发

			lenBeforeData += 8;
		}

		// data
		ByteBuf dataBuf = Unpooled.buffer(msg.getLength() - lenBeforeData);
		packBuf.readBytes(dataBuf);
		request.setContents(dataBuf.array());
		dataBuf.release();
		packBuf.release();
		
		if (request.getRequestId() > 0) {
			String params = TextUtil.toString(request.getContents());
			Map<String, String> parameterMap = TextUtil.parseParameterMap(params);
			request.setParamMap(parameterMap);
		}
		
		// 本地处理
		out.add(request);
	}

}

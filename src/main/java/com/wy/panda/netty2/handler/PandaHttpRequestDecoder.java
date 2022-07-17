package com.wy.panda.netty2.handler;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.wy.panda.common.TextUtil;
import com.wy.panda.mvc.common.ProtocolType;
import com.wy.panda.mvc.domain.Request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

public class PandaHttpRequestDecoder extends ChannelInboundHandlerAdapter {
	
	/** 通用命令 */
	private static final String COMMON_COMMAND = "gateway";
	/** http中命令参数 */
	private static final String PARAM_COMMAND = "command";

	/** 匹配command和参数 */
	private Pattern p = Pattern.compile("^.*/root/(.*?)\\.action\\?(.*)$");
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!(msg instanceof FullHttpRequest)) {
			return;
		}
		
		FullHttpRequest httpRequest = (FullHttpRequest) msg;
		String uri = httpRequest.uri();
		Matcher matcher = p.matcher(uri);
		if (matcher.matches()) {
			if (matcher.groupCount() < 2) {
				return;
			}
			
			String command = matcher.group(1);
			String params = matcher.group(2);
			
			// 解析参数
			Map<String, String> parameterMap = new HashMap<>();
			if (StringUtils.isNotBlank(params)) {
				parameterMap.putAll(TextUtil.parseParameterMap(params));
			}
			ByteBuf content = httpRequest.content();
			if (content != null) {
				byte[] array = content.array();
				if (array.length > 0) {
					String params2 = TextUtil.toString(array);
					parameterMap.putAll(TextUtil.parseParameterMap(params2));
				}
			}
			
			// 如果command为gateway,命令从参数中寻找
			if (COMMON_COMMAND.equalsIgnoreCase(command)) {
				String realCommand = parameterMap.remove(PARAM_COMMAND);
				if (StringUtils.isBlank(realCommand)) {
					for (Entry<String, String> entry : parameterMap.entrySet()) {
						if ("command".equalsIgnoreCase(entry.getKey())) {
							realCommand = entry.getValue();
							break;
						}
					}
					parameterMap.remove(realCommand);
				}
				command = realCommand;
			}
			
			Request request = new Request();
			request.setCommand(command);
			InetSocketAddress clientAddress = (InetSocketAddress)ctx.channel().remoteAddress();
			request.setIp(clientAddress.getHostString());
			request.setRequestId(Integer.MAX_VALUE);
			request.setPlayerId(0);
			request.setProtocol(ProtocolType.HTTP);
			request.setParamMap(parameterMap);
			
			// 通知处理服务请求
			ctx.fireChannelRead(request);
		}
	}
	
}
package com.panda.framework.netty2.handler;

import java.util.Map;
import java.util.Map.Entry;

import com.panda.framework.mvc.InvokeResult;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class PandaHttpResponseEncoder extends ChannelOutboundHandlerAdapter {

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (!(msg instanceof InvokeResult)) {
			return;
		}
		
		InvokeResult result = (InvokeResult)msg; 
		if (result.getResult() == null) {
			return;
		}
		
		long fileLength = result.getResult().length;
		
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, 
				HttpResponseStatus.OK, Unpooled.copiedBuffer(result.getResult()));
//		boolean keepAlive = HttpUtil.isKeepAlive(response);
		
		DefaultHttpHeaders headers = new DefaultHttpHeaders();
		
		// 添加response中的header
		Map<String, String> headersMap = result.getHeaders();
		if (headersMap != null && headersMap.size() > 0) {
			for (Entry<String, String> entry : headersMap.entrySet()) {
				headers.add(entry.getKey(), entry.getValue());
			}
		}
		// 设置长度数据
		headers.add(HttpHeaderNames.CONTENT_LENGTH, fileLength);
		headers.add(HttpHeaderNames.CONTENT_TYPE, "text/json");
		
//		if(keepAlive){
//			headers.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//		}
		
		response.headers().add(headers);
		ctx.write(response);
		
//		ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
//		if(!keepAlive){
//			lastContentFuture.addListener(ChannelFutureListener.CLOSE);
//		}
	}
}
package com.panda.framework.rpc.handler;

import com.panda.framework.rpc.connection.DefaultConnection;
import com.panda.framework.rpc.RpcResponse;
import com.panda.framework.rpc.connection.Connection;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RpcClientHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof RpcResponse) {
			RpcResponse response = (RpcResponse) msg;
			Connection connection = ctx.channel().attr(DefaultConnection.CONNECTION).get();
			if (connection != null) {
				connection.handleResponse(response);
			}
		}
	}
	
}

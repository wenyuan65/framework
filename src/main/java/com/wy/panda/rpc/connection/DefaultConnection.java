package com.wy.panda.rpc.connection;

import com.wy.panda.rpc.RpcRequest;
import com.wy.panda.rpc.RpcResponse;
import com.wy.panda.rpc.future.InvokeFuture;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;

public class DefaultConnection extends AbstractConnection {
	
	public static final AttributeKey<Connection> CONNECTION = AttributeKey.valueOf("connection");
	
	private Channel channel;
	
	public DefaultConnection(Channel channel) {
		this.channel = channel;
		this.channel.attr(CONNECTION).set(this);
	}

	@Override
	public InvokeFuture sendRequest(RpcRequest request, RpcResponse response) {
		final InvokeFuture future = createInvokeFuture(request, response, null);
		addInvokeFuture(future);
		try {
			channel.writeAndFlush(request).addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture f) throws Exception {
					if (!f.isSuccess()) {
						future.setCause(f.cause());
						future.putResponse(null);
						removeInvokeFuture(future);
					}
				}
			});
		} catch (Exception e) {
			future.setCause(e);
			future.putResponse(null);
			removeInvokeFuture(future);
		}
		
		return future;
	}
	
	@Override
	public boolean checkActive() {
		return channel.isActive();
	}
	
	@Override
	public void close() {
		if (channel != null) {
			channel.close();
		}
	}

}

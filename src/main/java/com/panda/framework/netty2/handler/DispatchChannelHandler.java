package com.panda.framework.netty2.handler;

import com.panda.framework.session.SessionManager;
import com.panda.framework.log.Logger;
import com.panda.framework.log.LoggerFactory;
import com.panda.framework.mvc.DispatchServlet;
import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;
import com.panda.framework.rpc.RpcRequest;
import com.panda.framework.rpc.RpcResponse;
import com.panda.framework.session.Session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class DispatchChannelHandler extends ChannelInboundHandlerAdapter {
	
	private static final Logger log = LoggerFactory.getLogger(DispatchChannelHandler.class);
	
	private DispatchServlet servlet;
	
	private boolean useSession = false;
	
	private static final AttributeKey<String> sessionKey = AttributeKey.valueOf("session");
	
	public DispatchChannelHandler(DispatchServlet servlet, boolean useSession) {
		this.servlet = servlet;
		this.useSession = useSession;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		if (!useSession) {
			return;
		}
		
		Channel channel = ctx.channel();
		boolean hasAttr = channel.hasAttr(sessionKey);
		if (!hasAttr) {
			Session session = SessionManager.getInstance().getSession("", true);
			Attribute<String> attr = channel.attr(sessionKey);
			attr.setIfAbsent(session.getSessionId());
			
			log.info("netty channel active, session: {}", session.getSessionId());
		}
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof Request) {
			Request request = (Request)msg; 
			Response response = new Response();
			response.setCtx(ctx);
			
			if (useSession) {
				Attribute<String> attr = ctx.channel().attr(sessionKey);
				request.setSessionId(attr.get());
				
				response.setHeader(sessionKey.name(), attr.get());
			}
			
			servlet.dispatch(request, response);
		} else if (msg instanceof RpcRequest) {
			RpcRequest request = (RpcRequest)msg;
			RpcResponse response = new RpcResponse(request.getRequestId());
			response.setCtx(ctx);

			servlet.dispatch(request, response);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("uncatchedexception in netty", cause);
	}

}

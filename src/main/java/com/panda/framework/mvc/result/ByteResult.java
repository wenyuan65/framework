package com.panda.framework.mvc.result;

import com.panda.framework.mvc.InvokeResult;
import com.panda.framework.mvc.domain.Request;
import com.panda.framework.mvc.domain.Response;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

public class ByteResult extends AbstractResult {

	private byte[] result;
	
	public ByteResult(byte[] result) {
		this.result = result;
	}

	/**
	 * @see AbstractResult#prepare(Request, Response)
	 */
	@Override
	public void prepare(Request request, Response response) {
		response.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
	}

	/**
	 * @see AbstractResult#doRender(Request, Response)
	 */
	@Override
	public void doRender(Request request, Response response) {
		InvokeResult invokeResult = new InvokeResult();
		invokeResult.setRequestId(request.getRequestId());
		invokeResult.setResult(result);
		invokeResult.setHeaders(response.getHeaders());
		
		response.push(invokeResult);
	}
	
}

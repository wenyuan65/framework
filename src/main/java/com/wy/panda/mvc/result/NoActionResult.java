package com.wy.panda.mvc.result;

import com.wy.panda.common.TextUtil;
import com.wy.panda.mvc.InvokeResult;
import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

public class NoActionResult extends AbstractResult {
		
		private byte[] result = null;
		
		public NoActionResult(String command) {
			result = TextUtil.toByte("cannot found command:" + command);
		}

		/**
		 * @see com.wy.panda.mvc.result.AbstractResult#prepare(com.wy.panda.mvc.domain.Request, com.wy.panda.mvc.domain.Response)
		 */
		@Override
		public void prepare(Request request, Response response) {
			response.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_JSON.toString());
		}

		/**
		 * @see com.wy.panda.mvc.result.AbstractResult#doRender(com.wy.panda.mvc.domain.Request, com.wy.panda.mvc.domain.Response)
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
package com.wy.panda.mvc.result;

import com.wy.panda.mvc.InvokeResult;
import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileResult extends AbstractResult {

	private String fileName;
	private byte[] result;

	private Charset charset = StandardCharsets.UTF_8;

	public FileResult(String fileName, byte[] result) {
		this.fileName = fileName;
		this.result = result;
	}

	public FileResult(String fileName, byte[] result, Charset charset) {
		this.fileName = fileName;
		this.result = result;
		this.charset = charset;
	}

	/**
	 * @see AbstractResult#prepare(Request, Response)
	 */
	@Override
	public void prepare(Request request, Response response) {
		StringBuilder sb = new StringBuilder();
		sb.append(HttpHeaderValues.ATTACHMENT).append(";");

		try {
			sb.append(HttpHeaderValues.FILENAME).append("=").append(URLEncoder.encode(fileName, charset.name()));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		response.setHeader(HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderValues.APPLICATION_OCTET_STREAM.toString());
		response.setHeader(HttpHeaderNames.CONTENT_DISPOSITION.toString(), sb.toString());
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

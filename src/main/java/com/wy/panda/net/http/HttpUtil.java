package com.wy.panda.net.http;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpMessage;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;

public class HttpUtil {
	
	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
	
	/** 请求id */
	private static final AtomicInteger requestIdGenerator = new AtomicInteger();
	
	/**
	 * 生成请求id
	 * @return
	 */
	private static int getNextRequestId() {
		return requestIdGenerator.incrementAndGet();
	}
	
	/**
	 * 将params转化为str
	 * @param params
	 * @return
	 */
	public static String getParamStr(Map<String, String> params) {
		if (params == null || params.size() == 0) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		params.forEach((key, value) -> {
			if (sb.length() > 0) {
				sb.append('&');
			}
			
			sb.append(key).append('=').append(value);
		});
		
		return sb.toString();
	}
	
	/**
	 * 发起get请求
	 * @param url
	 * @return
	 */
	public static String send(String url) {
        return send(url, null, null);
	}
	
	/**
	 * 发起get请求
	 * @param url
	 * @param headers
	 * @return
	 */
	public static String send(String url, Map<String, String> headers) {
        return send(url, null, headers);
	}
	
	/**
	 * 发起get请求
	 * @param url
	 * @param param
	 * @param headers
	 * @return
	 */
	public static String send(String url, Map<String, String> param, Map<String, String> headers) {
		int requestId = getNextRequestId();
		
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
        	url = buildGetMethodUrl(url, param);
        	log.info("#HTTP#{}#{}#", requestId, url);
        	
            HttpGet httpget = new HttpGet(url);
            // 设置请求配置
            RequestConfig requestConfig = buildDefaultRequestConfig();
            httpget.setConfig(requestConfig);
            // 设置header
            setHeaders(httpget, headers);
            // 发起请求
            String result = httpclient.execute(httpget, new DefaultResponseHandler());
            log.info("#HTTP#{}#{}#", requestId, result);
            
            return result;
        } catch(Throwable e) {
        	String msg = String.format("#HTTP#%d#ERROR#", requestId);
        	log.info(msg, e);
        }
        
        return null;
	}
	
	/**
	 * 发起https请求
	 * @param url
	 * @return
	 */
	public static String sendSSL(String url) {
        return sendSSL(url, null, null);
	}
	
	/**
	 * 发起https请求
	 * @param url
	 * @param headers
	 * @return
	 */
	public static String sendSSL(String url, Map<String, String> headers) {
        return sendSSL(url, null, headers);
	}
	
	/**
	 * 发起https请求
	 * @param url
	 * @param param
	 * @param headers
	 * @return
	 */
	public static String sendSSL(String url, Map<String, String> param, Map<String, String> headers) {
		int requestId = getNextRequestId();
		
		try (CloseableHttpClient httpclient = createDefaultSSLHostClient()) {
			url = buildGetMethodUrl(url, param);
			log.info("#HTTPS#{}#{}#", requestId, url);
			
            HttpGet httpget = new HttpGet(url);
            
            // 设置请求配置
            RequestConfig requestConfig = buildDefaultRequestConfig();
            httpget.setConfig(requestConfig);
            
            // 设置header
            setHeaders(httpget, headers);
            
            String result = httpclient.execute(httpget, new DefaultResponseHandler());
            log.info("#HTTPS#{}#{}#", requestId, result);
            
            return result;
        } catch(Throwable e) {
        	String msg = String.format("#HTTPS#%d#ERROR#", requestId);
        	log.info(msg, e);
        }
        
        return null;
	}

	/**
	 * 创建默认的ssl客户端，信任所有服务端证书和hostname
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 * @throws KeyStoreException
	 */
	private static CloseableHttpClient createDefaultSSLHostClient()
			throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
		// 信任所有服务端证书
        SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {

			@Override
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
        	
        }).build();
        
        // 信任所有的hostname
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, NoopHostnameVerifier.INSTANCE);
        
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		return httpclient;
	}
	
	/**
	 * 创建带证书验证的sslClient
	 * @param keyStorePath
	 * @param password
	 * @param supportedProtocols
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static CloseableHttpClient createSSLClient(String keyStorePath, String password, String[] supportedProtocols)
			throws Exception {
      SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(new File(keyStorePath), 
    		  password.toCharArray(), new TrustSelfSignedStrategy()).build();
      
      SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
              sslcontext, supportedProtocols, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
      
      return HttpClients.custom().setSSLSocketFactory(sslsf).build();
	}
	
	/**
	 * 将param填入url中
	 * @param url
	 * @param param
	 * @return
	 */
	private static String buildGetMethodUrl(String url, Map<String, String> param) {
		if (param != null && param.size() > 0) {
			String paramStr = getParamStr(param);
			int index = url.indexOf("?");
			if (index > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append(url);
				
				if (index < url.length() - 1) {
					sb.append('&');
				}
				
				sb.append(paramStr);
				url = sb.toString();
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append(url).append('?').append(paramStr);
				url = sb.toString();
			}
		}
		
		return url;
	}
	
	/**
	 * 添加header
	 * @param httpMessage
	 * @param headers
	 */
	private static final void setHeaders(HttpMessage httpMessage, Map<String, String> headers) {
		if (headers != null && headers.size() > 0) {
        	headers.forEach((name, value) -> {
        		httpMessage.setHeader(name, value);
        	});
        }
	}
	
	/**
	 * get方法的默认config
	 * @return
	 */
	private static final RequestConfig buildDefaultRequestConfig() {
		return RequestConfig.custom()
				.setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000)
				.setSocketTimeout(5000)
				.setRedirectsEnabled(true).build();
	}

	public static void main(String[] args) throws ClientProtocolException, IOException {
//		String send = HttpUtil.sendSSL("https://106.14.223.175:10002/root/gateway.action?command=test@getTestInfo&&param1=123&&param2=1234");
//		System.out.println(send);
	}
	
}

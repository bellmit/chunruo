package com.chunruo.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

/**
 * HttpClient工具类
 * @author chunruo
 */
public class HttpClientUtil {
	private static final int timeOut = 60 * 1000;
	private static CloseableHttpClient httpClient = null;
	private final static Object syncLock = new Object();

	private static void config(HttpRequestBase httpRequestBase) {
		// 设置Header等
		// httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
		// httpRequestBase
		// .setHeader("Accept",
		// "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		// httpRequestBase.setHeader("Accept-Language",
		// "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
		// httpRequestBase.setHeader("Accept-Charset",
		// "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");

		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(timeOut)
				.setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
		httpRequestBase.setConfig(requestConfig);
	}
	
	
	/**
	 * POST请求URL获取内容
	 * @param url
	 * @param headers
	 * @param body
	 * @return
	 */
	public static byte[] postInputStream(String url, Map<String, String> headers, Map<String, Object> params) {
		CloseableHttpResponse response = null;
		try {
			HttpPost httppost = new HttpPost(url);
			config(httppost);

			// post请求头信息
			if(headers != null && headers.size() > 0){
				try {
					for(Entry<String, String> entry : headers.entrySet()){
						httppost.setHeader(entry.getKey(), entry.getValue());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			httppost.setEntity(new StringEntity(new Gson().toJson(params), Consts.UTF_8));
			response = getHttpClient(url).execute(httppost, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			
			InputStream inputStream = entity.getContent();
			if(inputStream != null){
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            byte[] buffer = new byte[1024];
	            int num = inputStream.read(buffer);
	            while (num != -1) {
	                baos.write(buffer, 0, num);
	                num = inputStream.read(buffer);
	            }
	            baos.flush();
	            return baos.toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	/**
	 * 创建HttpClient对象
	 * @param maxTotal
	 * @param maxPerRoute
	 * @param maxRoute
	 * @param hostname
	 * @param port
	 * @return
	 */
	public static CloseableHttpClient createHttpClient(int maxTotal,
			int maxPerRoute, int maxRoute, String hostname, int port) {
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
		LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
		Registry<ConnectionSocketFactory> registry = RegistryBuilder
				.<ConnectionSocketFactory> create().register("http", plainsf)
				.register("https", sslsf).build();

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
		// 将最大连接数增加
		cm.setMaxTotal(maxTotal);
		// 将每个路由基础的连接增加
		cm.setDefaultMaxPerRoute(maxPerRoute);
		HttpHost httpHost = new HttpHost(hostname, port);
		// 将目标主机的最大连接数增加
		cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

		// 请求重试处理
		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {
				if (executionCount >= 5) {
					// 如果已经重试了5次，就放弃
					return false;
				}
				if (exception instanceof NoHttpResponseException) {
					// 如果服务器丢掉了连接，那么就重试
					return true;
				}
				if (exception instanceof SSLHandshakeException) {
					// 不要重试SSL握手异常
					return false;
				}
				if (exception instanceof InterruptedIOException) {
					// 超时
					return false;
				}
				if (exception instanceof UnknownHostException) {
					// 目标服务器不可达
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {
					// 连接被拒绝
					return false;
				}
				if (exception instanceof SSLException) {
					// SSL握手异常
					return false;
				}

				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					// 如果请求是幂等的，就再次尝试
					return true;
				}
				return false;
			}
		};

		CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(cm)
				.setRetryHandler(httpRequestRetryHandler).build();

		return httpClient;
	}

	/**
	 * 获取HttpClient对象
	 * @param url
	 * @return
	 */
	public static CloseableHttpClient getHttpClient(String url) {
		String hostname = url.split("/")[2];
		int port = 80;
		if (hostname.contains(":")) {
			String[] arr = hostname.split(":");
			hostname = arr[0];
			port = Integer.parseInt(arr[1]);
		}
		if (httpClient == null) {
			synchronized (syncLock) {
				if (httpClient == null) {
					httpClient = createHttpClient(200, 40, 100, hostname, port);
				}
			}
		}
		return httpClient;
	}
	
	/**
	 * POST请求URL获取内容
	 * @param url
	 * @param headers
	 * @param body
	 * @return
	 */
	public static String postXML(String url, String xmlBody) {
		CloseableHttpResponse response = null;
		try {
			HttpPost httppost = new HttpPost(url);
			config(httppost);
			
			httppost.setHeader("Content-Type", "text/xml;charset=UTF-8");
			httppost.setEntity(new StringEntity(xmlBody, Consts.UTF_8));
			response = getHttpClient(url).execute(httppost, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, Consts.UTF_8);
			EntityUtils.consume(entity);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * post表单提交
	 * @param url
	 * @param headers
	 * @param body
	 * @return
	 */
	public static String postFrom(String url, Map<String, Object> paramMap) {
		CloseableHttpResponse response = null;
		try {
			HttpPost httppost = new HttpPost(url);
			config(httppost);
			
			// 对请求的表单域进行填充
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532);
			if(paramMap != null && paramMap.size() > 0) {
				ContentType contentType = ContentType.create("text/plain", Consts.UTF_8);
				for(Entry<String, Object> entry : paramMap.entrySet()) {
					if(entry.getValue() instanceof File) {
						multipartEntityBuilder.addPart(entry.getKey(), new FileBody((File)entry.getValue()));
					}else {
						String strBody = StringUtil.null2Str(entry.getValue());
						multipartEntityBuilder.addTextBody(entry.getKey(), strBody, contentType);
					}
				}
			}
			
			multipartEntityBuilder.setContentType(ContentType.MULTIPART_FORM_DATA);
			
			// 设置请求
			HttpEntity reqEntity = multipartEntityBuilder.build();
			httppost.setEntity(reqEntity);
			
			response = getHttpClient(url).execute(httppost, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, Consts.UTF_8);
			EntityUtils.consume(entity);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * POST请求URL获取内容
	 * @param url
	 * @param headers
	 * @param body
	 * @return
	 */
	public static String post(String url, Map<String, String> headers, String body) {
		CloseableHttpResponse response = null;
		try {
			HttpPost httppost = new HttpPost(url);
			config(httppost);

			// post请求头信息
			if(headers != null && headers.size() > 0){
				try {
					for(Entry<String, String> entry : headers.entrySet()){
						httppost.setHeader(entry.getKey(), entry.getValue());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			httppost.setEntity(new StringEntity(body, Consts.UTF_8));
			response = getHttpClient(url).execute(httppost, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, Consts.UTF_8);
			EntityUtils.consume(entity);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * POST请求URL获取内容
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String post(String url, Map<String, String> params) {
		CloseableHttpResponse response = null;
		try {
			HttpPost httppost = new HttpPost(url);
			config(httppost);

			// post请求参数
			if(params != null && params.size() > 0){
				try {
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					for(Entry<String, String> entry : params.entrySet()){
						nvps.add(new BasicNameValuePair(entry.getKey(), StringUtil.null2Str(entry.getValue())));
					}
					httppost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			response = getHttpClient(url).execute(httppost, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, Consts.UTF_8);
			EntityUtils.consume(entity);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * GET请求URL获取内容
	 * @param url
	 * @return
	 */
	public static String get(String url) {
		return HttpClientUtil.get(url, null);
	}
	
	/**
	 * GET请求URL获取内容
	 * @param url
	 * @return
	 */
	public static String get(String url, Map<String, String> params) {
		return HttpClientUtil.get(url, null, params);
	}

	/**
	 * GET请求URL获取内容
	 * @param url
	 * @return
	 */
	public static String get(String url, Map<String, String> headers, Map<String, String> params) {
		CloseableHttpResponse response = null;
		try {			
			// get请求参数
			if(params != null && params.size() > 0){
				try {
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					for(Entry<String, String> entry : params.entrySet()){
						nvps.add(new BasicNameValuePair(entry.getKey(), StringUtil.null2Str(entry.getValue())));
					}
					url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
						
			HttpGet httpget = new HttpGet(url);
			// post请求头信息
			if(headers != null && headers.size() > 0){
				try {
					for(Entry<String, String> entry : headers.entrySet()){
						httpget.setHeader(entry.getKey(), entry.getValue());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
						
			config(httpget);
			response = getHttpClient(url).execute(httpget, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, Consts.UTF_8);
			EntityUtils.consume(entity);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * GET请求URL获取内容
	 * @param url
	 * @return
	 */
	public static String delete(String url, Map<String, String> headers) {
		CloseableHttpResponse response = null;
		try {
						
			HttpDelete httpget = new HttpDelete(url);
			// post请求头信息
			if(headers != null && headers.size() > 0){
				try {
					for(Entry<String, String> entry : headers.entrySet()){
						httpget.setHeader(entry.getKey(), entry.getValue());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
						
			config(httpget);
			response = getHttpClient(url).execute(httpget, HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity, Consts.UTF_8);
			EntityUtils.consume(entity);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null){
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("Content-Type", "application/json");
		map.put("Authorization", "Basic YTE0NjJmNTkyMDU4MjMxYzkxZDU4YTA5OjQ0YmQyYjE5MGNiMzRjODU2MGJhMWQ3OA==");
		System.out.println(HttpClientUtil.delete("https://device.jpush.cn/v3/aliases/test_3833",map));
	}
}

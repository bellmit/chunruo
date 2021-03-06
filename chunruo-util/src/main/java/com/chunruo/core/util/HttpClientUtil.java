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
 * HttpClient?????????
 * @author chunruo
 */
public class HttpClientUtil {
	private static final int timeOut = 60 * 1000;
	private static CloseableHttpClient httpClient = null;
	private final static Object syncLock = new Object();

	private static void config(HttpRequestBase httpRequestBase) {
		// ??????Header???
		// httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
		// httpRequestBase
		// .setHeader("Accept",
		// "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		// httpRequestBase.setHeader("Accept-Language",
		// "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
		// httpRequestBase.setHeader("Accept-Charset",
		// "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");

		// ???????????????????????????
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(timeOut)
				.setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
		httpRequestBase.setConfig(requestConfig);
	}
	
	
	/**
	 * POST??????URL????????????
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

			// post???????????????
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
	 * ??????HttpClient??????
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
		// ????????????????????????
		cm.setMaxTotal(maxTotal);
		// ????????????????????????????????????
		cm.setDefaultMaxPerRoute(maxPerRoute);
		HttpHost httpHost = new HttpHost(hostname, port);
		// ???????????????????????????????????????
		cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

		// ??????????????????
		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {
				if (executionCount >= 5) {
					// ?????????????????????5???????????????
					return false;
				}
				if (exception instanceof NoHttpResponseException) {
					// ????????????????????????????????????????????????
					return true;
				}
				if (exception instanceof SSLHandshakeException) {
					// ????????????SSL????????????
					return false;
				}
				if (exception instanceof InterruptedIOException) {
					// ??????
					return false;
				}
				if (exception instanceof UnknownHostException) {
					// ????????????????????????
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {
					// ???????????????
					return false;
				}
				if (exception instanceof SSLException) {
					// SSL????????????
					return false;
				}

				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					// ??????????????????????????????????????????
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
	 * ??????HttpClient??????
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
	 * POST??????URL????????????
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
	 * post????????????
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
			
			// ?????????????????????????????????
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
			
			// ????????????
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
	 * POST??????URL????????????
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

			// post???????????????
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
	 * POST??????URL????????????
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

			// post????????????
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
	 * GET??????URL????????????
	 * @param url
	 * @return
	 */
	public static String get(String url) {
		return HttpClientUtil.get(url, null);
	}
	
	/**
	 * GET??????URL????????????
	 * @param url
	 * @return
	 */
	public static String get(String url, Map<String, String> params) {
		return HttpClientUtil.get(url, null, params);
	}

	/**
	 * GET??????URL????????????
	 * @param url
	 * @return
	 */
	public static String get(String url, Map<String, String> headers, Map<String, String> params) {
		CloseableHttpResponse response = null;
		try {			
			// get????????????
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
			// post???????????????
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
	 * GET??????URL????????????
	 * @param url
	 * @return
	 */
	public static String delete(String url, Map<String, String> headers) {
		CloseableHttpResponse response = null;
		try {
						
			HttpDelete httpget = new HttpDelete(url);
			// post???????????????
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

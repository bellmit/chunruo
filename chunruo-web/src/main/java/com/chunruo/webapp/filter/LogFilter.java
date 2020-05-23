package com.chunruo.webapp.filter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONObject;
import net.sf.ehcache.constructs.web.GenericResponseWrapper;
import net.sf.ehcache.constructs.web.filter.Filter;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.util.RequestUtil;

/**
 * 内容消息体加密
 * @author chunruo
 *
 */
public class LogFilter extends	Filter {
	private static Log log = LogFactory.getLog(LogFilter.class);

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)throws Throwable {

		String url = StringUtil.null2Str(request.getRequestURI());
		if(!url.endsWith(".json")) {
			chain.doFilter(request, response);
		}else {
			StringBuffer requestBuffer = new StringBuffer (RequestUtil.getReqeustParamValues(request));
			ByteArrayOutputStream compressed = new ByteArrayOutputStream();
			BufferedOutputStream bufferOut = new BufferedOutputStream(compressed);
			GenericResponseWrapper wrapper = new GenericResponseWrapper(response, bufferOut);
			chain.doFilter(request, wrapper);
			wrapper.flush();
			bufferOut.close();
			response.getOutputStream().write(compressed.toByteArray());
			requestBuffer.append(LogFilter.getResponseBody(wrapper, compressed, false));
			
			log.debug(requestBuffer.toString());
		}
		
	}
	
	/**
	 * 请求返回消息体
	 * @param wrapper
	 * @param compressed
	 * @return
	 */
	private static String getResponseBody(GenericResponseWrapper wrapper, ByteArrayOutputStream compressed, boolean isGzip){
		StringBuffer responseBuffer = new StringBuffer ();
		try{
			int statusCode = wrapper.getStatus();
			responseBuffer.append("|status=" + statusCode);
			if(StringUtil.compareObject(HttpServletResponse.SC_OK , statusCode)){
				// 请求返回消息体内容
				String resultBody = null;
				if(isGzip){
					// gzip压缩格式
					resultBody = LogFilter.uncompressToString(compressed.toByteArray(), "utf-8");
				}else{
					// 普通二进制格式
					resultBody = LogFilter.utfToString(compressed.toByteArray());
				}

				// json格式解析
				JSONObject returnJson = new JSONObject(resultBody.toString());
				if(returnJson != null 
						&& returnJson.length() > 0
						&& returnJson.has("message")){
					responseBuffer.append("[");
					responseBuffer.append("msg=" + StringUtil.null2Str(returnJson.get("message")) + ",");
					responseBuffer.append("]");
				}
			}
		}catch(Exception e){
			//log.debug(e.getMessage());
		}
		return responseBuffer.toString();
	}	

	/** 
	 * 字节数组解压缩后返回字符串    
	 */ 
	public static String uncompressToString(byte[] b, String encoding) { 
		if (b == null || b.length == 0) { 
			return null; 
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(b);
		
		try {
			GZIPInputStream gunzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];

			int n;
			while ((n = gunzip.read(buffer)) >= 0) { 
				out.write(buffer, 0, n);
			}

			return out.toString(encoding);
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
		return null;
	}
	
	/** 
	 * 字节数组解压缩后返回字符串    
	 */ 
	private static String utfToString(byte[] data) {
		String str = null;
		try {
			str = new String(data, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}   
		return str;
	}
	
	@Override
	protected void doDestroy() {
		try{
			super.destroy();
		}catch(Exception e){
		}
	}

	@Override
	protected void doInit(FilterConfig filterConfig) throws Exception {
	}
}

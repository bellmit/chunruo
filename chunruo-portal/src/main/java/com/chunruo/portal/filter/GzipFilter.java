package com.chunruo.portal.filter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONObject;

import net.sf.ehcache.constructs.web.GenericResponseWrapper;
import net.sf.ehcache.constructs.web.ResponseUtil;
import net.sf.ehcache.constructs.web.filter.Filter;

import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.RequestUtil;

/**
 * 内容消息体加密
 * @author chunruo
 *
 */
public class GzipFilter extends	Filter {
	private static Log log = LogFactory.getLog(GzipFilter.class);

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)throws Throwable {
		// 补用户会员等级信息
		UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
		if(userInfo != null && userInfo.getUserId() != null){
			response.setHeader(PortalConstants.X_LEVEL, StringUtil.null2Str(StringUtil.nullToInteger(userInfo.getLevel())));
			response.setHeader(PortalConstants.X_USER_ID, StringUtil.null2Str(StringUtil.nullToInteger(userInfo.getUserId())));
		}
		
		StringBuffer requestBuffer = new StringBuffer (RequestUtil.getReqeustParamValues(request));
		String reqeustGzipType = StringUtil.null2Str(request.getAttribute(PortalFilter.REQUST_GZIP_TYPE));
		if(StringUtil.compareObject(reqeustGzipType, PortalFilter.REQUST_GZIP_TYPE)){
			// Create a gzip stream
			final ByteArrayOutputStream compressed = new ByteArrayOutputStream();
			final GZIPOutputStream gzout = new GZIPOutputStream(compressed);

			// Handle the request
			final GenericResponseWrapper wrapper = new GenericResponseWrapper(response, gzout);
			chain.doFilter(request, wrapper);
			wrapper.flush();
			gzout.close();
			
			// 检查返回结果是否用户未登陆
			MsgModel<Integer> msgModel = GzipFilter.getResponseBody(wrapper, compressed, true);
			if(StringUtil.compareObject(msgModel.getData(), PortalConstants.CODE_NOLOGIN)) {
				response.setHeader(PortalConstants.X_TOKEN, PortalConstants.TOKEN_DEFUALT_OVERDUE);
			}
			requestBuffer.append(msgModel.getMessage());

			//return on error or redirect code, because response is already committed
			int statusCode = wrapper.getStatus();
			if (statusCode != HttpServletResponse.SC_OK) {
				return;
			}

			//Saneness checks
			byte[] compressedBytes = compressed.toByteArray();
			boolean shouldGzippedBodyBeZero = ResponseUtil.shouldGzippedBodyBeZero(compressedBytes, request);
			boolean shouldBodyBeZero = ResponseUtil.shouldBodyBeZero(request, wrapper.getStatus());
			if (shouldGzippedBodyBeZero || shouldBodyBeZero) {
				compressedBytes = new byte[0];
			}

			// Write the zipped body
			java.util.Random random = new java.util.Random();
			Integer endLength = Integer.valueOf(random.nextInt(15)) + 3;
			byte[] responseByte = new byte[compressedBytes.length + endLength];

			// 首字节表示尾长度
			byte[] headerBytes = new byte[endLength];
			headerBytes[0] = endLength.byteValue();
			for(int i = 1; i < endLength; i ++){
				headerBytes[i] = Integer.valueOf(random.nextInt(10)).byteValue();
			}
			System.arraycopy(headerBytes, 0, responseByte, 0, endLength);

			// 消息内容体
			if(compressedBytes.length > 0){
				System.arraycopy(compressedBytes, 0, responseByte, endLength, compressedBytes.length);
			}

			// 内容响应
			response.setContentLength(responseByte.length);
			response.getOutputStream().write(responseByte);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}else{
			ByteArrayOutputStream compressed = new ByteArrayOutputStream();
			BufferedOutputStream bufferOut = new BufferedOutputStream(compressed);
			GenericResponseWrapper wrapper = new GenericResponseWrapper(response, bufferOut);
			chain.doFilter(request, wrapper);
			wrapper.flush();
			bufferOut.close();
			response.getOutputStream().write(compressed.toByteArray());
			
			// 检查返回结果是否用户未登陆
			MsgModel<Integer> msgModel = GzipFilter.getResponseBody(wrapper, compressed, false);
			if(StringUtil.compareObject(msgModel.getData(), PortalConstants.CODE_NOLOGIN)) {
				response.setHeader(PortalConstants.X_TOKEN, PortalConstants.TOKEN_DEFUALT_OVERDUE);
			}
			requestBuffer.append(msgModel.getMessage());
			
		}
		log.debug(requestBuffer.toString());
	}
	
	/**
	 * 请求返回消息体
	 * @param wrapper
	 * @param compressed
	 * @return
	 */
	private static MsgModel<Integer> getResponseBody(GenericResponseWrapper wrapper, ByteArrayOutputStream compressed, boolean isGzip){
		MsgModel<Integer> msgModel = new MsgModel<Integer> ();
		StringBuffer responseBuffer = new StringBuffer ();
		int code = 0;
		try{
			int statusCode = wrapper.getStatus();
			responseBuffer.append("|status=" + statusCode);
			if(StringUtil.compareObject(HttpServletResponse.SC_OK , statusCode)){
				// 请求返回消息体内容
				String resultBody = null;
				if(isGzip){
					// gzip压缩格式
					resultBody = GzipFilter.uncompressToString(compressed.toByteArray(), "utf-8");
				}else{
					// 普通二进制格式
					resultBody = GzipFilter.utfToString(compressed.toByteArray());
				}

				// json格式解析
				JSONObject returnJson = new JSONObject(resultBody.toString());
				if(returnJson != null 
						&& returnJson.length() > 0
						&& returnJson.has(PortalConstants.CODE)){
					code = StringUtil.nullToInteger(returnJson.get(PortalConstants.CODE));
					responseBuffer.append("[");
					responseBuffer.append(PortalConstants.CODE + "=" + StringUtil.null2Str(returnJson.get(PortalConstants.CODE)) + ",");
					responseBuffer.append(PortalConstants.MSG + "=" + StringUtil.null2Str(returnJson.get(PortalConstants.MSG)) + ",");
					responseBuffer.append(PortalConstants.SYSTEMTIME + "=" + StringUtil.null2Str(returnJson.get(PortalConstants.SYSTEMTIME)));
					responseBuffer.append("]");
				}
			}
		}catch(Exception e){
			//log.debug(e.getMessage());
		}
		
		msgModel.setData(code);
		msgModel.setMessage(responseBuffer.toString());
		return msgModel;
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

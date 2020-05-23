package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.chunruo.core.Constants;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.PortalConstants;

public class RequestUtil {
	private static final Log log = LogFactory.getLog(RequestUtil.class);
	
	/**
	 * 是否微信浏览器  
	 * @param request
	 * @return
	 */
	public static boolean isWeixin(HttpServletRequest request){
		String ua = StringUtil.null2Str(request.getHeader("user-agent")).toLowerCase();
		if(ua.indexOf("micromessenger") > 0) {
			// 是否微信浏览器
			return true;  
		} 
		return false;
	}
	
	/**
	 * 获取浏览器信息
	 * @param request
	 * @return
	 */
	public static String getUserAgent(HttpServletRequest request){
		return StringUtil.null2Str(request.getHeader("User-Agent"));
	}
	
	/**
     * 获取请求中的参数值
     * 
     * @param request
     * @param paramName
     * @return
     * 		如果没有该参数，则返回空字符串
     */
    public static String getParam(HttpServletRequest request,String paramName){
    	String value = request.getParameter(paramName);
    	if(value!=null)
    		value = value.trim();
    	return value == null ? "" : value;
    }
    
    /**
     * 获取请求中的参数值
     * 
     * @param request
     * @param paramName
     * @return
     * 		如果没有该参数，则返回空字符串
     */
    public static Long getLongParam(HttpServletRequest request,String paramName){
    	String value = getParam(request, paramName);
    	Long l = null;
    	try{
    		l = Long.parseLong(value);
    	}catch(NumberFormatException e){
    		log.error(e);
    	}
    	return l;
    }
    
    /**
     * 获取请求中的参数值
     * 
     * @param request
     * @param paramName
     * @return
     * 		如果没有该参数，则返回空字符串
     */
    public static Integer getIntParam(HttpServletRequest request, String paramName){
    	String value = getParam(request, paramName);
    	Integer i = null;
    	try{
    		i = Integer.parseInt(value);
    	}catch(NumberFormatException e){
    		log.error(e);
    	}
    	return i;
    }
	
	public static String getAppVersion(HttpServletRequest request) {
		return StringUtil.nullToString(request.getHeader(PortalConstants.X_APP_VERSION));
    }
	
	public static String getTOKEN(HttpServletRequest request) {
		return StringUtil.nullToString(request.getHeader(PortalConstants.X_TOKEN));
    }
	
	public static String getUUID(HttpServletRequest request) {
		return StringUtil.nullToString(request.getHeader(PortalConstants.X_UUID));
    }
	
	public static String getShareUser(HttpServletRequest request) {
		return StringUtil.nullToString(request.getHeader(PortalConstants.X_SHARE_USER));
    }
	
	public static Integer getRequestType(HttpServletRequest request) {
		return StringUtil.nullToInteger(request.getHeader(PortalConstants.X_REQUEST_TYPE));
    }
	/**
	 * 
	 * @param request
	 * @return 客户端分辨率
	 */
	public static String getClientResolution(HttpServletRequest request) {
		return StringUtil.nullToString(request.getHeader(PortalConstants.X_RESOLUTION));
    }
	
	/**
	 * 
	 * @param request
	 * @return 客户端UA
	 */
	public static String getClientUA(HttpServletRequest request) {
		return StringUtil.nullToString(request.getHeader(PortalConstants.X_UA));
    }
	
	/**
	 * 
	 * @param request
	 * @return 店铺ID
	 */
	public static Long getStoreId(HttpServletRequest request) {
		String storeId = StringUtil.null2Str(request.getHeader(PortalConstants.X_STORE_ID));
		if(StringUtil.isNumber(storeId)){
			return StringUtil.nullToLong(storeId);
		}
		return null;
    }
	
	/**
	 * 
	 * @param request
	 * @return 客户端型号
	 */
	public static int getClientType(HttpServletRequest request) {
		return StringUtil.nullToInteger(request.getHeader(PortalConstants.X_CLIENT_TYPE));
    }
	
	/**
	 * 
	 * @param request
	 * @return 客户端平台
	 */
	public static String getPlatform(HttpServletRequest request) {
		return StringUtil.null2Str(request.getHeader(PortalConstants.X_PLATFORM));
    }
	
	/**
	 * 客户端IP地址
	 * @param request
	 * @return
	 */
	public static String getClientIp(HttpServletRequest request) {
		String clientIp = StringUtil.null2Str(request.getHeader("X-Real-IP"));
		if(StringUtil.isNull(clientIp)){
			clientIp = StringUtil.null2Str(request.getHeader("X-Forwarded-For"));
		}
		if(StringUtil.isNull(clientIp)){
			clientIp = StringUtil.null2Str(request.getRemoteAddr());
		}
		return clientIp;
	}
	
	/**
	 * 当前服务器URL地址
	 * @param request
	 * @return 请求域名+端口
	 */
	public static String getRequestURL(HttpServletRequest request) {
		// 测试指定域名地址
		String httpstr="http://"+request.getServerName();
		if(StringUtil.nullToBoolean(Constants.conf.getProperty("chunruo.https.url"))) {
			httpstr="https://"+request.getServerName();
		}
		
		String port = StringUtil.null2Str(request.getServerPort());
		List<String> portList = new ArrayList<String> ();
		portList.add("0");
		portList.add("80");
		if(!portList.contains(port)){
			httpstr += ":" + port;
		}
		return httpstr;
	}
	
	/**
	 * 请求所有参数
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getReqeustParamValues(HttpServletRequest request){
		StringBuffer sb = new StringBuffer (RequestUtil.getClientIp(request) + "=");
		try{
			sb.append(request.getRequestURI() + "[");
			sb.append("sessionId=" + StringUtil.null2Str(request.getSession().getId()) + ",");
			
			// 请求参数
			Enumeration pNames = request.getParameterNames();
			while(pNames.hasMoreElements()){
				String name = (String)pNames.nextElement();
				if(!StringUtil.isNull(name)){
					String value = request.getParameter(name);
					sb.append(name + "=" + value +",");
				}
			}
			
			// 请求头信息
			Enumeration hNames = request.getHeaderNames();
			while(hNames.hasMoreElements()){
				String name = (String)hNames.nextElement();
				if(!StringUtil.isNull(name) && StringUtil.null2Str(name).toLowerCase().startsWith("x-")){
					String value = request.getHeader(name);
					sb.append(name + "=" + value +",");
				}
			}
			
			// 用户信息
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if(userInfo != null && userInfo.getUserId() != null){
				sb.append("userId=" + userInfo.getUserId());
			}
			sb.append("]");
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
}

package com.chunruo.portal.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.tools.config.ValidScope;
import org.apache.velocity.tools.generic.SafeConfig;

import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.util.RequestUtil;

@ValidScope({"request"})
public class BaseTag extends SafeConfig{
	protected final transient Log log = LogFactory.getLog(getClass());
	protected HttpServletRequest request;
	protected HttpServletResponse response;

	public void setRequest(HttpServletRequest request){
		if (request == null){
			throw new NullPointerException("request should not be null");
		}
		this.request = request;
	}

	public void setResponse(HttpServletResponse response){
		if (response == null){
			throw new NullPointerException("response should not be null");
		}
		this.response = response;
	}
	
	/**
	 * 请求全路径
	 * @param request
	 * @return
	 */
	public String getRequestURL(HttpServletRequest request){
		String requestURL = StringUtil.null2Str(request.getRequestURI());
		if(!StringUtil.isNull(request.getParameter("a"))){
			requestURL = String.format("%s?a=%s", requestURL, StringUtil.null2Str(request.getParameter("a")));
		}
		return RequestUtil.getRequestURL(request) + requestURL;
	}
	
	/**
	 * 下单优惠信息
	 * @param priceRecommend
	 * @param priceWholesale
	 * @return
	 */
	public static String getPriceDiscount(Double priceRecommend, Double priceWholesale){
		Double priceDiscount = 0.0D;
		if(priceRecommend != null 
				&& priceWholesale != null
				&& priceWholesale.compareTo(priceRecommend) <= 0){
			priceDiscount = priceRecommend - priceWholesale;
		}
		return StringUtil.nullToDoubleFormatStr(priceDiscount);
	}
	
	/**
	 * 比较当前版本跟老版本
	 * @return
	 */
	public boolean checkVersion() {
		// 得到当前版本号
		String currentVersion = StringUtil.null2Str(RequestUtil.getAppVersion(request));
		String[] versionArr = currentVersion.split("\\.");
		String versionCode = "";
		if (versionArr != null && versionArr.length > 0) {
			for (String str : versionArr) {
				versionCode += str;
			}
		}
		// 换成数字形式判断大小
		Integer currentVersionCode = StringUtil.nullToInteger(versionCode);
		if (currentVersionCode < 180) {
			return false;
		}
		return true;
	}
}

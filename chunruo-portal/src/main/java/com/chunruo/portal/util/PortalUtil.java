package com.chunruo.portal.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONObject;
import com.chunruo.cache.portal.impl.UserInfoByIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.AESUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.TokenVo;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.PortalConstants;

public class PortalUtil {
	protected final static transient Log log = LogFactory.getLog(PortalUtil.class);


	/**
	 * 检查用户是否代理用户
	 * @param userInfo
	 * @return
	 */
	public static MsgModel<UserInfo> isAgentUserInfo(Long topUserId){
		MsgModel<UserInfo> msgModel = new MsgModel<UserInfo> ();
		try{
			UserInfoByIdCacheManager userInfoByIdCacheManager = Constants.ctx.getBean(UserInfoByIdCacheManager.class);
			UserInfo topUserInfo = userInfoByIdCacheManager.getSession(topUserId);
			return PortalUtil.isAgentUserInfo(topUserInfo);
		}catch(Exception e){
			e.printStackTrace();
		}

		msgModel.setMessage("店铺不存在错误");
		msgModel.setIsSucc(false);
		return msgModel;
	}

	/**
	 * 检查用户是否代理用户
	 * @param userInfo
	 * @return
	 */
	public static MsgModel<UserInfo> isAgentUserInfo(UserInfo userInfo){
		MsgModel<UserInfo> msgModel = new MsgModel<UserInfo> ();
		try{
			if(userInfo != null && userInfo.getUserId() != null){

				if(StringUtil.nullToBoolean(userInfo.getIsAgent())){
					if(StringUtil.compareObject(userInfo.getLevel(), UserLevel.USER_LEVEL_DEALER)){
						msgModel.setIsDistributor(true);
					}
					msgModel.setLevel(StringUtil.nullToInteger(userInfo.getLevel()));
					msgModel.setData(userInfo);
					msgModel.setIsSucc(true);
					return msgModel;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		msgModel.setMessage("店铺不存在错误");
		msgModel.setIsSucc(false);
		return msgModel;
	}

	/**
	 * userInfo转换map对象
	 * @param userInfo
	 * @return
	 */
	public static Map<String, Object> getUserInfoMap(UserInfo userInfo, final HttpServletRequest request){
		if(userInfo == null || userInfo.getUserId() == null){
			return null;
		}

		String headerImage = StringUtil.null2Str(userInfo.getHeaderImage());
		if(!StringUtil.isNull(headerImage) && (!headerImage.startsWith("http://")
				|| !headerImage.startsWith("https://"))){
			headerImage = RequestUtil.getRequestURL(request) + "/upload/" + headerImage;
		}

		Map<String, Object> userInfoMap = new HashMap<String, Object> ();
		userInfoMap.put("userId", StringUtil.nullToLong(userInfo.getUserId()));
		userInfoMap.put("nickname", StringUtil.null2Str(userInfo.getNickname()));
		userInfoMap.put("mobile", StringUtil.null2Str(userInfo.getMobile()));
		userInfoMap.put("introduce", StringUtil.null2Str(userInfo.getIntroduce()));
		userInfoMap.put("headerImage", headerImage);
		userInfoMap.put("sex", userInfo.getSex());
		userInfoMap.put("realName", StringUtil.null2Str(userInfo.getUserId()));
		userInfoMap.put("level", StringUtil.null2Str(userInfo.getLevel())); 

	
		return userInfoMap;
	}

	/**
	 * 从session缓存获取用户信息
	 * @param request
	 * @return
	 */
	public static UserInfo getCurrentUserInfo(HttpServletRequest request){
		return PortalUtil.getCurrentUserInfo(request, null);
	}
	
	/**
	 * 从session缓存获取用户信息
	 * @param request
	 * @return
	 */
	public static UserInfo getCurrentUserInfo(HttpServletRequest request, int loginType){
		UserInfo userInfo = PortalUtil.getCurrentUserInfo(request, null);
		if(userInfo != null 
				&& userInfo.getUserId() != null) {
			return userInfo;
		}
		return null;
	}
	
	/**
	 * 删除用户缓存信息
	 * @param request
	 */
	public static void removeSession(HttpServletRequest request){
		try{
			HttpSession session = request.getSession();
			session.removeAttribute(PortalConstants.SESSION_CURRENT_USER);
			session.removeAttribute(PortalConstants.SESSION_SMS_CODE);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 注册和登陆成功统一处理返回
	 * @param resultMap
	 * @param userInfo
	 * @param request
	 * @param response
	 * @return
	 */
	public static Map<String, Object> getLoginSucc(Boolean isWechatLogin, Map<String, Object> resultMap, UserInfo userInfo, HttpServletRequest request, HttpServletResponse response){
		//清空用户session
		PortalUtil.removeSession(request);
		
		try{
			// 普通用户登陆需要更新登陆次数
			UserInfoManager userInfoManager = Constants.ctx.getBean(UserInfoManager.class);
			UserInfoByIdCacheManager userInfoByIdCacheManager = Constants.ctx.getBean(UserInfoByIdCacheManager.class);
			
			// 更新用户登陆次数
			userInfo = userInfoManager.get(userInfo.getUserId());
			userInfo.setLoginCount(StringUtil.nullToInteger(userInfo.getLoginCount()) + 1);
			userInfo.setLastLoginTime(DateUtil.getCurrentDate());
			userInfo = userInfoManager.update(userInfo);
			
			try{
				// 更新缓存信息
				userInfoByIdCacheManager.updateSession(userInfo.getUserId(), userInfo);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			response.setHeader(PortalConstants.X_TOKEN, PortalUtil.createUserToken(userInfo));
			request.getSession().setAttribute(PortalConstants.SESSION_CURRENT_USER, userInfo);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		// 检查是否微信登陆
		if(StringUtil.nullToBoolean(isWechatLogin)){
			//缓存信息设置
			request.getSession().setAttribute(PortalConstants.SESSION_CURRENT_USER, userInfo);
		}
		
		// 用户信息和头像处理
		resultMap.put("userInfo", PortalUtil.getUserInfoMap(userInfo, request));
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		resultMap.put(PortalConstants.MSG, "登录成功");
		return resultMap;
	}

	/**
	 * 登陆成功保存用户信息到缓存中
	 * @param userInfo
	 * @param request
	 * @param response
	 */
	public static UserInfo saveUserInfo(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response){
		return PortalUtil.saveUserInfo(userInfo, request, response, null);
	}

	/**
	 * 登陆成功保存用户信息到缓存中
	 * @param userInfo
	 * @param request
	 * @param response
	 */
	public static UserInfo saveUserInfo(UserInfo user, HttpServletRequest request, HttpServletResponse response, String wechatNick){
		try{
			UserInfo userInfo = user.clone();
			// 普通用户登陆需要更新登陆次数
			boolean isLoginUpdate = StringUtil.nullToBoolean(request.getAttribute("isLoginUpdate"));
			if(isLoginUpdate || userInfo.getLastLoginTime() == null){
				UserInfoManager userInfoManager = Constants.ctx.getBean(UserInfoManager.class);
				UserInfoByIdCacheManager userInfoByIdCacheManager = Constants.ctx.getBean(UserInfoByIdCacheManager.class);

				// 更新用户登陆次数
				userInfo = userInfoManager.get(userInfo.getUserId());

				userInfo.setLastIp(RequestUtil.getClientIp(request));
				userInfo.setLoginCount(StringUtil.nullToInteger(userInfo.getLoginCount()) + 1);
				userInfo.setLastLoginTime(DateUtil.getCurrentDate());
				userInfo = userInfoManager.update(userInfo);
				try{
					// 更新缓存信息
					userInfoByIdCacheManager.updateSession(userInfo.getUserId(), userInfo);
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			// 设置用户登录类型
			request.getSession().setAttribute(PortalConstants.SESSION_CURRENT_USER, userInfo);
			
			// 设置X_TOKEN信息
			if(response != null) {
				userInfo.setLoginPcIp(RequestUtil.getClientIp(request));
				response.setHeader(PortalConstants.X_TOKEN, PortalUtil.createUserToken(userInfo));
			}
			return userInfo;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 删除用户缓存信息
	 * @param request
	 */
	public static void removeUserInfo(HttpServletRequest request){
		try{
			HttpSession session = request.getSession();
			session.removeAttribute(PortalConstants.SESSION_CURRENT_USER);
			// 删除绑定微信短信验证码
			request.getSession().removeAttribute(PortalConstants.SESSION_SMS_CODE);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 获取用户信息
	 * @param request
	 * @param response
	 * @return
	 */
	public static UserInfo getCurrentUserInfo(HttpServletRequest request, HttpServletResponse response){
		UserInfo userInfo = null;
		UserInfoManager userInfoManager = Constants.ctx.getBean(UserInfoManager.class);
		try {
			// 从缓存中读取token信息
			TokenVo token = PortalUtil.getToken(request);
			if(token != null 
					&& token.getUserId() != null
					&& token.getLastLoginTime() != null){
				// 直接查找数据库
				UserInfo cacheUserInfo = userInfoManager.get(token.getUserId());
				if(cacheUserInfo != null 
						&& cacheUserInfo.getUserId() != null
						&& StringUtil.nullToBoolean(cacheUserInfo.getStatus())
						&& cacheUserInfo.getLastLoginTime() != null){
					String tokenLastLoginTime = DateUtil.formatDate(DateUtil.DATE_FORMAT_HOUR, new Date(token.getLastLoginTime()));
					String cacheLastLoginTime = DateUtil.formatDate(DateUtil.DATE_FORMAT_HOUR, cacheUserInfo.getLastLoginTime());
					log.debug(String.format("token_login[userId=%s, lastTime=%s, tokenTime=%s]", cacheUserInfo.getUserId(), cacheLastLoginTime, tokenLastLoginTime));
					if(tokenLastLoginTime.compareTo(cacheLastLoginTime) >= 0){
						// 登录成功保存用户userInfo信息
						userInfo = cacheUserInfo;
						userInfo.setLoginPcIp(token.getLoginPcIp());
						PortalUtil.saveUserInfo(userInfo, request, response);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if(acceptsEncoding(request, "true")
				&& (userInfo == null || userInfo.getUserId() == null)){
			Long storeId = RequestUtil.getStoreId(request);
			userInfo = userInfoManager.get(StringUtil.nullToLong(storeId));
		}

		return userInfo;
	}
	
	protected static boolean acceptsEncoding(final HttpServletRequest request, final String name) {
		final boolean accepts = headerContains(request, "BDRequest-Encoding", name);
		return accepts;
	}

	/**
	 * 请求信息中包含关键字
	 * @param request
	 * @param header
	 * @param value
	 * @return
	 */
	public static boolean headerContains(final HttpServletRequest request, final String header, final String value) {
		@SuppressWarnings("rawtypes")
		final Enumeration accepted = request.getHeaders(header);
		while (accepted.hasMoreElements()) {
			final String headerValue = (String) accepted.nextElement();
			if (headerValue.indexOf(value) != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取请求头中的值
	 * @param request
	 * @param header
	 * @param value
	 * @return
	 */
	public static boolean getHeaderValue(final HttpServletRequest request, final String header, final String value) {
		@SuppressWarnings("rawtypes")
		final Enumeration accepted = request.getHeaders(header);
		while (accepted.hasMoreElements()) {
			final String headerValue = (String) accepted.nextElement();
			if (headerValue.indexOf(value) != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 创建用户token值
	 * @param userId
	 * @return
	 */
	public static String createUserToken(UserInfo userInfo){
		String token = null ;
		try {
			// 上次登录时间初始化
			Long lastLoginTime = null;
			if(userInfo.getLastLoginTime() == null) {
				lastLoginTime = System.currentTimeMillis();
			}else {
				lastLoginTime = userInfo.getLastLoginTime().getTime();
			}
			
			TokenVo tokenVo = new TokenVo ();
			tokenVo.setUserId(userInfo.getUserId());
			tokenVo.setLoginPcIp(StringUtil.null2Str(userInfo.getLoginPcIp()));
			tokenVo.setLevel(StringUtil.nullToInteger(userInfo.getLevel()));
			tokenVo.setLastLoginTime(lastLoginTime);
			tokenVo.setVersion(Constants.conf.getProperty("chunruo.token.version.key"));
			
			token = AESUtil.encryptString(AESUtil.transformation, AESUtil.IV, PortalConstants.DECRYPT_KEY, StringUtil.objectToJSON(tokenVo));
			log.info("创建x_token完成："+token);
		} catch (UnsupportedEncodingException e) {
			token = PortalConstants.TOKEN_DEFUALT_OVERDUE;
			log.error("创建x_token失败");
		}
		return token;
	}

	/**
	 * 字符串信息转换成token对象
	 * @param strJson
	 * @return
	 */
	public static TokenVo getToken(HttpServletRequest request) {
		try {
			String strToken = RequestUtil.getTOKEN(request);
			if (!StringUtil.isNullStr(strToken) && !StringUtil.compareObject(strToken, PortalConstants.TOKEN_DEFUALT_OVERDUE)) {
				String[] tokenArray = strToken.split(",");
				if(tokenArray != null && tokenArray.length > 0){
					TokenVo tokenVo = new TokenVo ();
					String decryptString = AESUtil.decryptString(AESUtil.transformation, AESUtil.IV, PortalConstants.DECRYPT_KEY, StringUtil.null2Str(tokenArray[0]));
					JSONObject jsonObject = new JSONObject(decryptString);
					if(jsonObject.has("userId")){
						tokenVo.setUserId(StringUtil.nullToLong(jsonObject.getString("userId")));
					}
					if(jsonObject.has("lastLoginTime")){
						tokenVo.setLastLoginTime(StringUtil.nullToLong(jsonObject.getString("lastLoginTime")));
					}
					if(jsonObject.has("level")){
						tokenVo.setLevel(StringUtil.nullToInteger(jsonObject.getString("level")));
					}
					if(jsonObject.has("version")){
						tokenVo.setVersion(StringUtil.null2Str(jsonObject.getString("version")));
					}
					if(jsonObject.has("loginPcIp")){
						tokenVo.setLoginPcIp(StringUtil.null2Str(jsonObject.getString("loginPcIp")));
					}
					return tokenVo;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param request
	 * @return 请求域名+端口+工程名
	 */
	public static String getHostPath(HttpServletRequest request) {
		return RequestUtil.getRequestURL(request) + request.getContextPath();
	}

	/**
	 * 获取用户图像完整URL
	 * @param image
	 * @return
	 */
	public static String getUserImageUrl(String image) {
		if (StringUtil.isNull(image)) {
			return Constants.conf.getProperty("user.imageurl.default");
		} else {
			return Constants.conf.getProperty("user.imageurl.suffix") + image;
		}
	}
}
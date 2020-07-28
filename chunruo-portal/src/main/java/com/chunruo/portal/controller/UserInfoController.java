package com.chunruo.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.cache.portal.impl.UserInfoByIdCacheManager;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.Constants.WechatOautType;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.MessageVo;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.interceptor.LoginInterceptor;
import com.chunruo.portal.util.PortalUtil;

/**
 * 用户中心
 * @author chunruo
 */
@Controller
@RequestMapping("/api/user/")
public class UserInfoController extends BaseController{
	
	
	private final static Integer LOGIN_TYPE_PASSWORD = 1;   //密码
	private final static Integer LOGIN_TYPE_SMSCODE = 2;    //验证码

	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private UserInfoByIdCacheManager userInfoByIdCacheManager;

	/**
	 * 用户登陆
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/login")
	public @ResponseBody Map<String, Object> clientLogin(final HttpServletRequest request, final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String mobile = StringUtil.null2Str(request.getParameter("mobile"));
		Integer loginType = StringUtil.nullToInteger(request.getParameter("loginType"));  //1:密码登录 2：验证码登录
		String password = StringUtil.null2Str(request.getParameter("password"));
		String smsCode = StringUtil.null2Str(request.getParameter("smsCode"));
		String countryCode = StringUtil.null2Str(request.getParameter("countryCode"));

		try{
			if(StringUtil.isNull(mobile)){
				//手机号码不能为空
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "手机号码不能为空");
				return resultMap;
			}else if(StringUtil.isNull(countryCode)){
				// 国家地区编码不能为空
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "国家地区编码不能为空");
				return resultMap;
			}
			
			
			List<Integer> loginTypeList = new ArrayList<Integer>();
			loginTypeList.add(LOGIN_TYPE_PASSWORD);
			loginTypeList.add(LOGIN_TYPE_SMSCODE);
			if(!loginTypeList.contains(loginType)) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "登录类型错误");
				return resultMap;
			}else if(StringUtil.compareObject(loginType,LOGIN_TYPE_PASSWORD)) {
				if(StringUtil.isNull(password)) {
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					resultMap.put(PortalConstants.MSG, "请输入密码");
					return resultMap;
				}
			}else if(StringUtil.compareObject(loginType, LOGIN_TYPE_SMSCODE)) {
				 if(StringUtil.isNull(smsCode)){
					// 登陆短信验证码不能为空
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					resultMap.put(PortalConstants.MSG, "登陆短信验证码不能为空");
					return resultMap;
				}
			}
			

			UserInfo userInfo = this.userInfoManager.getUserInfoByMobile(mobile, countryCode);
			if(userInfo == null || userInfo.getUserId() == null){
				// 登陆类型错误
				resultMap.put("oauthType", WechatOautType.WECHAT_OAUTH_TYPE_NEW);
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "用户不存在或密码错误");
				return resultMap;
			}else if(!StringUtil.nullToBoolean(userInfo.getStatus())){
				// 账号已被锁定
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "账号已被锁定,请联系客服 ");
				return resultMap;
			}

			//检查用户身份，普通用户不能登录
			MsgModel<UserInfo> smsgModel = PortalUtil.isAgentUserInfo(userInfo);
			if(!StringUtil.nullToBoolean(smsgModel.getIsSucc())){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_NOSTORE);
				resultMap.put(PortalConstants.MSG, "该账号不能登录客户端,请前往注册。若有疑问，请联系客服 ");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			
			if(StringUtil.compareObject(loginType,LOGIN_TYPE_PASSWORD)) {
				if(!StringUtil.compareObject(userInfo.getPassword(), password)) {
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_NOSTORE);
					resultMap.put(PortalConstants.MSG, "密码输入错误");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
			}else if(StringUtil.compareObject(loginType, LOGIN_TYPE_SMSCODE)) {
				// 验证码校验是否session一致
				MessageVo messageVo = this.validMSMCode(request, mobile, countryCode, smsCode, PortalConstants.CODE_TYPE_LOGIN);
				if (!messageVo.getIsSucc()) {
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					resultMap.put(PortalConstants.MSG, StringUtil.null2Str(messageVo.getMessage()));
					return resultMap;
				}
			}
			
			//清空用户session
			PortalUtil.removeUserInfo(request);

			return this.getLoginSucc(resultMap, userInfo,request, response, null);
		}catch(Exception e){
			e.printStackTrace();
			log.debug(e.getMessage());
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		resultMap.put(PortalConstants.MSG, "登陆失败");
		return resultMap;
	}

	


	/**
	 * 登陆退出
	 * @return
	 */
	@RequestMapping("/logout")
	public @ResponseBody Map<String, Object> logout(final HttpServletRequest request,final  HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		try {
			// 修改最后登录时间失效
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if(userInfo != null && userInfo.getUserId() != null){
				userInfo = this.userInfoManager.get(userInfo.getUserId());
				if(userInfo != null && userInfo.getUserId() != null){
					userInfo.setLastLoginTime(DateUtil.getCurrentDate());
					this.userInfoManager.update(userInfo);
					
					try{
						this.userInfoByIdCacheManager.removeSession(userInfo.getUserId());
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}

			response.setHeader(PortalConstants.X_TOKEN, "no");
			request.getSession().removeAttribute(PortalConstants.SESSION_CURRENT_USER);

			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "退出成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;

		}catch(Exception e) {
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "退出失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * 注册和登陆成功统一处理返回
	 * @param resultMap
	 * @param userInfo
	 * @param request
	 * @param response
	 * @return
	 */
	private Map<String, Object> getLoginSucc(Map<String, Object> resultMap, UserInfo userInfo, HttpServletRequest request, HttpServletResponse response, String wechatNick){
		// 更新用户缓存信息,返回token信息
		request.setAttribute("isLoginUpdate", true);
		UserInfo userInfoBak = PortalUtil.saveUserInfo(userInfo, request, response);		
		// 用户信息和头像处理
		Map<String, Object> userInfoMap = PortalUtil.getUserInfoMap(userInfoBak, request);

		resultMap.put("userInfo", userInfoMap);
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		resultMap.put(PortalConstants.MSG, "登录成功");
		return resultMap;
	}

	/**
	 * 用户注册
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/register")
	public @ResponseBody Map<String, Object> clientRegister(final HttpServletRequest request, final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			String mobile = StringUtil.null2Str(request.getParameter("mobile"));
			String smsCode = StringUtil.null2Str(request.getParameter("smsCode"));
			String password = StringUtil.null2Str(request.getParameter("password"));     //密码
			String repassword = StringUtil.null2Str(request.getParameter("repassword")); //确认密码

			if (StringUtil.isNull(mobile)) {
				// 手机号码不能为空
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "手机号码不能为空");
				return resultMap;
			} else if (StringUtil.isNull(smsCode)) {
				// 短信验证码不能为空
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "短信验证码不能为空");
				return resultMap;
			} else if (StringUtil.isNull(password) || StringUtil.isNull(repassword)
					|| StringUtil.compareObject(password, repassword)) {
				// 短信验证码不能为空
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "密码不能为空或者两次密码输入不匹配");
				return resultMap;
			}

			// 账号被绑定
			UserInfo userInfo = this.userInfoManager.getUserInfoByMobile(mobile, UserInfo.DEFUALT_COUNTRY_CODE);
			MsgModel<UserInfo> xmsgModel = PortalUtil.isAgentUserInfo(userInfo);
			if(StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "你的手机号已经被绑定，请您更换 手机号完成注册，如您想改绑手机号，请联系客服");
				return resultMap;
			}

			// 验证码校验是否session一致
			MessageVo messageVo = this.validMSMCode(request, mobile, UserInfo.DEFUALT_COUNTRY_CODE, smsCode, PortalConstants.CODE_TYPE_REGISTER);
			if (!messageVo.getIsSucc()) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, StringUtil.null2Str(messageVo.getMessage()));
				return resultMap;
			}
			
			// 清空用户session
			PortalUtil.removeUserInfo(request);
			
			
			userInfo.setIsAgent(true);
			userInfo.setMobile(mobile);
			userInfo.setPassword(password);
			userInfo.setLevel(UserLevel.USER_LEVEL_BUYERS);
			userInfo.setStatus(true);
			userInfo.setRegisterTime(DateUtil.getCurrentDate());
			userInfo.setUpdateTime(DateUtil.getCurrentDate());
			
			//存储用户注册信息
			userInfo = PortalUtil.saveUserInfo(userInfo, request, response);
			
			try{
				this.userInfoByIdCacheManager.removeSession(userInfo.getUserId());
			}catch(Exception e){
				e.printStackTrace();
			}

			resultMap.put("mobile", mobile);
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			resultMap.put(PortalConstants.MSG, "注册成功");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		resultMap.put(PortalConstants.MSG, "注册失败");
		return resultMap;
	}

	
	@LoginInterceptor(value = LoginInterceptor.LOGIN, contType = LoginInterceptor.CONT_JOSN_TYPE)
	@RequestMapping("/bindShareUser")
	public @ResponseBody Map<String, Object> bindShareUser(final HttpServletRequest request,final  HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Long shareUserId = StringUtil.nullToLong(request.getParameter("userId"));
		try {

			System.out.println("bindShareUser:"+shareUserId);
			// 修改最后登录时间失效
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if(userInfo == null || userInfo.getUserId() == null){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_NOLOGIN);
				resultMap.put(PortalConstants.MSG, "用户未登录");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			if(StringUtil.compareObject(userInfo.getUserId(), shareUserId)) {
                    resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "不能绑定自己");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
			}
			
			if(!StringUtil.compareObject(userInfo.getShareUserId(), 0)) {
				UserInfo shareUserInfo = this.userInfoManager.get(StringUtil.nullToLong(userInfo.getShareUserId()));
				if(shareUserInfo != null 
						&& shareUserInfo.getUserId() != null) {
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "用户已绑定分享关系");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
			}
			
			UserInfo shareUserInfo = this.userInfoManager.get(shareUserId);
			if(shareUserInfo == null || shareUserInfo.getUserId() == null) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "分享人信息未找到");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			userInfo.setShareUserId(StringUtil.nullToLong(shareUserInfo.getUserId()));
			userInfo.setUpdateTime(DateUtil.getCurrentDate());
			this.userInfoManager.save(userInfo);

			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "分享关系绑定成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;

		}catch(Exception e) {
			e.printStackTrace();
		}

        System.out.println("ERROR bindShareUser:"+shareUserId);
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "绑定失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
}

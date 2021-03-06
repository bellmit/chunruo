package com.chunruo.portal.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.cache.portal.impl.UserInfoByIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserSociety;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserSocietyManager;
import com.chunruo.core.util.AESUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.HttpClientUtil;
import com.chunruo.core.util.SHA1Util;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.WxSendUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.interceptor.LoginInterceptor;
import com.chunruo.portal.util.DesUtil;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.WeiXinPayUtil;


@Controller
@RequestMapping("/api/miniProgram/")
public class WeiXinAppletController extends BaseController{
	private final static String WECHAT_SESSION_KEY = "wechatSessionKey";
	@Autowired
	private UserSocietyManager userSocietyManager;
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private UserInfoByIdCacheManager userInfoByIdCacheManager;
	
	
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/getQrCode")
	public @ResponseBody Map<String, Object> getQrCode(final HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		try{		
			WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(Constants.MINI_PROGRAM_WECHAT_CONFIG_ID);
			String token = WxSendUtil.getWeiXinToken(weChatAppConfig.getAppId(), weChatAppConfig.getAppSecret());
			if(!StringUtil.isNull(token)){
				UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
				Map<String, String> headers = new HashMap<String, String> ();
				headers.put("Content-Type", "application/json;charset=utf-8");

				Map<String, Object> params = new HashMap<String, Object> ();
				params.put("scene", userInfo.getUserId());
				params.put("width", 100);
				params.put("page", "pages/openMember/openMember");
				String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + token;
				byte[] result = HttpClientUtil.postInputStream(url, headers, params);

				// ???????????????Base64??????
				String base64 = new String(Base64.encodeBase64(result));
				if(base64 != null && base64.length() > 1000){
					resultMap.put("qrCode", String.format("data:image/jpeg;base64,%s", base64));
					resultMap.put(PortalConstants.MSG, "????????????");
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.MSG, "????????????");
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
	
	/**
	 * ??????URL????????????
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/checkSiginature")
	public @ResponseBody String checkSiginature(final HttpServletRequest request) {
		String signature = StringUtil.null2Str(request.getParameter("signature"));
		String timestamp = StringUtil.null2Str(request.getParameter("timestamp"));
		String nonce = StringUtil.null2Str(request.getParameter("nonce"));
		String echostr = StringUtil.null2Str(request.getParameter("echostr"));
		
		try {
			String token = StringUtil.null2Str(Constants.conf.getProperty("miniProgram.url.token"));
			String localSignature = SHA1Util.getSHA1(token, timestamp, nonce);
			log.debug(String.format("validate---[localSignature=%s, signature=%s]", localSignature, signature));
			if(StringUtil.compareObject(localSignature, signature)) {
				log.debug("validate=result->" + echostr);
				return echostr;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
        return null;
	}

	@RequestMapping(value="/getToken")
	public @ResponseBody Map<String, String>  getToken(final HttpServletRequest request, HttpServletResponse response) {
		String url = StringUtil.null2Str(request.getParameter("url"));
		Map<String, String> signMap = new HashMap<String, String> ();
		try{
			Long configId = Constants.MINI_PROGRAM_WECHAT_CONFIG_ID;
			url = url.replaceAll("%26", "&");
			WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(configId);
			String jsApiTicket = WeiXinPayUtil.getJSApiTicket(weChatAppConfig);
			signMap = WeiXinPayUtil.sign(jsApiTicket, url);
			signMap.put("appId", weChatAppConfig.getAppId());
			signMap.put("code", "1");
		}catch(Exception e){
			e.printStackTrace();
		}
		return signMap;
	}

	/**
	 * ???????????????code????????????
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/getWechatCode")
	public @ResponseBody Map<String, Object> getWechatCode(final HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String wechatCode = StringUtil.null2Str(request.getParameter("code"));
		try{
			//??????????????????
			PortalUtil.removeUserInfo(request);

			// ???????????????
			Long configId = Constants.MINI_PROGRAM_WECHAT_CONFIG_ID;
			MsgModel<String> msgModel = WeiXinPayUtil.validateWechatCode(wechatCode, configId);
			if(StringUtil.nullToBoolean(msgModel.getIsSucc())) {
				log.debug("sessionId[=====1]"+request.getSession().getId());
				request.getSession().setAttribute(WeiXinAppletController.WECHAT_SESSION_KEY, msgModel.getData());
				resultMap.put("openId", StringUtil.null2Str(msgModel.getTransactionId()));
				resultMap.put("codeKey", DesUtil.encrypt(msgModel.getData(), WeiXinPayUtil.DES_ENCRYPT_CRYPT_KEY));
				resultMap.put(PortalConstants.MSG, "????????????");
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.MSG, "????????????");
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * ?????????????????????
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/getWeiXinUserInfo")
	public @ResponseBody Map<String, Object> getWeiXinUserInfo(final HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String encryptedData = StringUtil.null2Str(request.getParameter("encryptedData"));
		String codeKey = StringUtil.null2Str(request.getParameter("codeKey"));
		String iv = StringUtil.null2Str(request.getParameter("iv"));
		String userData = StringUtil.null2Str(request.getParameter("userData"));
		String openId = StringUtil.null2Str(request.getParameter("openId"));


		log.debug(String.format("Wechat Login[encryptedData=%s, iv=%s, codeKey=%s]", encryptedData, iv, codeKey));
		try{
//			//????????????wechatCode??????
//			String sessionKey = StringUtil.nullToString(request.getSession().getAttribute(WeiXinAppletController.WECHAT_SESSION_KEY));
//			if(StringUtil.isNull(sessionKey)){
//				sessionKey = DesUtil.decrypt(codeKey, WeiXinPayUtil.DES_ENCRYPT_CRYPT_KEY);
//			}
//
//			log.debug("sessionId[=====2]"+request.getSession().getId());
//			System.out.println("encryptedData="+encryptedData);
//			System.out.println("sessionKey="+sessionKey);
//			System.out.println("iv="+iv);
//			//????????????????????????
//			String weChatUserInfo = AESUtil.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(sessionKey), Base64.decodeBase64(iv));
//			log.debug(String.format("Wechat Login[sessionKey=%s, weChatUserInfo=%s]", sessionKey, weChatUserInfo));

			System.out.println("==========="+userData);
			// ??????????????????
			Map<String, String> weChatUserMap = StringUtil.jsonToHashMap(userData);
			if(weChatUserMap == null 
					|| weChatUserMap.size() <= 0
					|| StringUtil.isNull("openId")){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "??????????????????");
				return resultMap;
			}

			//????????????session
			PortalUtil.removeUserInfo(request);

			Long configId = Constants.MINI_PROGRAM_WECHAT_CONFIG_ID;

			weChatUserMap.put("openId", openId);
//			String openId = StringUtil.null2Str(weChatUserMap.get("openId"));
			UserSociety userSociety = this.userSocietyManager.getUserSocietyByOpenIdAndConfigId(configId, openId);
			if(userSociety == null || userSociety.getUserSocietyId() == null){
				// ????????????????????????????????????????????????
				userSociety = this.getUserSocietyByMap(configId, weChatUserMap);
				userSociety.setCreateTime(DateUtil.getCurrentDate());
				userSociety.setUpdateTime(userSociety.getCreateTime());
				userSociety = this.userSocietyManager.saveUserSociety(userSociety);
			}

			//??????????????????????????????
			boolean isBindMobile = true;
			UserInfo userInfo = userSociety.getUserInfo();
			if(StringUtil.isValidateMobile(StringUtil.null2Str(userInfo.getMobile()))){
				isBindMobile = true;
			}

			// ?????????????????????
			userInfo = PortalUtil.saveUserInfo(userInfo,request, response);
			request.getSession().setAttribute(PortalConstants.SESSION_CURRENT_OPEN_ID, openId);

			resultMap.put("userInfo", userInfo);
			resultMap.put("isBindMobile", StringUtil.booleanToInt(isBindMobile));
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			resultMap.put(PortalConstants.MSG, "????????????");
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.MSG, "????????????");
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}


	/**
	 * ?????????????????????
	 * @param request
	 * @param response
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/bindWeixinMobile")
	public @ResponseBody Map<String, Object>  bindWeixinMobile(final HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String encryptedData = StringUtil.null2Str(request.getParameter("encryptedData"));
		String codeKey = StringUtil.null2Str(request.getParameter("codeKey"));
		String iv = StringUtil.null2Str(request.getParameter("iv"));
		log.debug(String.format("Wechat Login[encryptedData=%s, iv=%s, codeKey=%s]", encryptedData, iv, codeKey));
		try{
			UserInfo userInfo = this.userInfoManager.get(PortalUtil.getCurrentUserInfo(request).getUserId());
			if (userInfo == null || userInfo.getUserId() == null) {
				resultMap.put(PortalConstants.MSG, "???????????????");
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			//????????????wechatCode??????
			String sessionKey = StringUtil.nullToString(request.getSession().getAttribute(WeiXinAppletController.WECHAT_SESSION_KEY));
			if(StringUtil.isNull(sessionKey)){
				sessionKey = DesUtil.decrypt(codeKey, WeiXinPayUtil.DES_ENCRYPT_CRYPT_KEY);
			}

			//????????????????????????
			String weChatUserInfo = AESUtil.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(sessionKey), Base64.decodeBase64(iv));
			log.debug(String.format("Wechat Login[sessionKey=%s, weChatUserInfo=%s]", sessionKey, weChatUserInfo));

			// ??????????????????
			Map<String, String> mobileInfoMap = StringUtil.jsonToHashMap(weChatUserInfo);
			if(mobileInfoMap == null || mobileInfoMap.size() <= 0){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "?????????????????????");
				return resultMap;
			}

			//?????????????????????????????????
			String mobile = mobileInfoMap.get("purePhoneNumber");
			if (!StringUtil.isValidateMobile(mobile)) {
				resultMap.put(PortalConstants.MSG, "??????????????????");
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			// ???????????????????????????????????????
			UserInfo user = this.userInfoManager.getUserInfoByMobile(mobile, UserInfo.DEFUALT_COUNTRY_CODE);
			if (user != null 
					&& user.getUserId() != null
					&& !StringUtil.compareObject(userInfo.getUserId(), user.getUserId())) {
				resultMap.put(PortalConstants.MSG, "???????????????????????????");
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			userInfo.setMobile(mobile);
			userInfo.setUpdateTime(DateUtil.getCurrentDate());
			userInfo = this.userInfoManager.save(userInfo);

			try{
				//??????????????????
				this.userInfoByIdCacheManager.updateSession(userInfo.getUserId(), userInfo);
			}catch(Exception e){
				e.printStackTrace();
			}

			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			resultMap.put(PortalConstants.MSG, "????????????");
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.MSG, "?????????????????????");
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}

	/**
	 * ???????????????
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping("/bindMobile")
	public @ResponseBody Map<String, Object> modifyMobile(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String mobile = StringUtil.nullToString(request.getParameter("mobile"));
		try {

			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if(!StringUtil.isValidateMobile(mobile)) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "????????????????????????");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			//????????????
			UserInfo userInfoBak = this.userInfoManager.getUserInfoByMobile(mobile, UserInfo.DEFUALT_COUNTRY_CODE);
			if(userInfoBak != null && userInfoBak.getUserId() != null) {
				if(!StringUtil.compareObject(userInfo.getUserId(), userInfoBak.getUserId())) {
					//???????????????????????????????????????????????????????????????
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "????????????????????????????????????");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
			}else {
				userInfo.setMobile(mobile);
				userInfo.setUpdateTime(DateUtil.getCurrentDate());
				this.userInfoManager.save(userInfo);
			}
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "????????????");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;

		}catch(Exception e) {
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "????????????");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
	
	
	/**
	 * ??????
	 * @return
	 */
	@RequestMapping("/logout")
	public @ResponseBody Map<String, Object> logout(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		try {
			// ??????????????????????????????
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
			resultMap.put(PortalConstants.MSG, "????????????");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;

		}catch(Exception e) {
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "????????????");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
	public static void main(String[] args) {
		String json = "{\"openid\":1}";
		Map<String, String> weChatUserMap = StringUtil.jsonToHashMap(json);
		System.out.println(weChatUserMap.get("openid"));

	}
}

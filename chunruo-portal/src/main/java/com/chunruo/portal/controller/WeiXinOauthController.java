package com.chunruo.portal.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.core.Constants;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.UserSociety;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.service.UserSocietyManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.SHA1Util;
import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.RequestUtil;
import com.chunruo.portal.util.WeiXinPayUtil;

import net.sf.json.JSONObject;

/**
 * 微信相关授权
 * @author chunruo
 *
 */
@Controller
@RequestMapping("/api/weixin/")
public class WeiXinOauthController extends BaseController{
	@Autowired
	private UserSocietyManager userSocietyManager;
	@Autowired
	private UserInfoManager userInfoManager;
	
	/**
	 * 微信URL接入验证
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/validate")
	public @ResponseBody String validate(final HttpServletRequest request) {
		String signature = StringUtil.null2Str(request.getParameter("signature"));
		String timestamp = StringUtil.null2Str(request.getParameter("timestamp"));
		String nonce = StringUtil.null2Str(request.getParameter("nonce"));
		String echostr = StringUtil.null2Str(request.getParameter("echostr"));
		
		try {
			String token = StringUtil.null2Str(Constants.conf.getProperty("weixin.url.token"));
			//校验签名
			String localSignature = SHA1Util.getSHA1(token, timestamp, nonce);
			log.debug(String.format("validate---[localSignature=%s, signature=%s]", localSignature, signature));
			if(StringUtil.compareObject(localSignature, signature)) {
			    //若签名相等，原样返回echostr
				log.debug("validate=result->" + echostr);
				return echostr;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
        //接入失败
        return null;
	}
	
	@RequestMapping(value="/getToken")
	public @ResponseBody Map<String, String>  getToken(final HttpServletRequest request, HttpServletResponse response) {
		String url = StringUtil.null2Str(request.getParameter("url"));
		Map<String, String> signMap = new HashMap<String, String> ();
		try{
			url = url.replaceAll("%26", "&");
			WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(Constants.PUBLIC_ACCOUNT_WECHAT_CONFIG_ID);
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
	 * 微信页面授权回调接口
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/accessToken")
	public void accessToken(final HttpServletRequest request, HttpServletResponse response) {
		String code = StringUtil.null2Str(request.getParameter("code"));
		String typeCode = StringUtil.null2Str(request.getParameter("typeCode"));
		String state = StringUtil.null2Str(request.getParameter("state"));
		try{
			log.debug(String.format("authorize weChat[%s]", RequestUtil.getReqeustParamValues(request)));
			WeChatAppConfig weChatAppConfig = Constants.WECHAT_CONFIG_ID_MAP.get(StringUtil.nullToLong(typeCode));
			if(weChatAppConfig != null && weChatAppConfig.getConfigId() != null){
				Map<String, String> objectMap = WeiXinPayUtil.requestAccessToken(weChatAppConfig.getAppId(), weChatAppConfig.getAppSecret(), code);
				if(objectMap != null 
						&& objectMap.size() > 0
						&& objectMap.containsKey("openid")
						&& objectMap.containsKey("access_token")){
					String openId = StringUtil.null2Str(objectMap.get("openid"));
					String accessToken = StringUtil.null2Str(objectMap.get("access_token"));
					
					// 检查微信账号是否已绑定账号
					UserSociety userSociety = this.userSocietyManager.getUserSocietyByOpenIdAndConfigId(weChatAppConfig.getConfigId(), openId);
					if(userSociety != null && userSociety.getUserSocietyId() != null){
						// 已绑定账号
						UserInfo userInfo = userSociety.getUserInfo();
						if(userInfo != null && userInfo.getUserId() != null){
							/**********************
							 * 补上新的unionId
							 * **********************/
							// 只针对旧UnionId不为空补新UnionId
							if(!StringUtil.isNull(userSociety.getOldUnionId())){
								// 任何一个unionId为空
								if(StringUtil.isNull(userSociety.getUnionId()) || StringUtil.isNull(userInfo.getUnionId())){
									Map<String, String> userInfoMap = WeiXinPayUtil.requestWeChatUserInfo(openId, accessToken);
									if(userInfoMap != null 
											&& userInfoMap.size() > 0
											&& !StringUtil.isNull(userInfoMap.get("unionid"))){
										try{
											String unionid = StringUtil.null2Str(userInfoMap.get("unionid"));
											// 检查授权的新unionid是否为空
											userSociety.setUnionId(unionid);
											userSociety.setUpdateTime(DateUtil.getCurrentDate());
											this.userSocietyManager.save(userSociety);

											// 更新数据库
											userInfo.setUnionId(unionid);
											this.userInfoManager.updateUserInfo(unionid, userInfo.getUserId());
										}catch(Exception e){
											e.printStackTrace();
										}
									}
								}
							}
							log.debug(String.format("weChat--------1111[%s]", userSociety.getUserInfo()));
							this.setWeiXinOauthSessionInfo(request, response, weChatAppConfig, openId, userSociety.getUserInfo());
						}
					}else{
						// 拉取用户信息(需scope为 snsapi_userinfo)
						Map<String, String> userInfoMap = WeiXinPayUtil.requestWeChatUserInfo(openId, accessToken);
						if(userInfoMap != null && userInfoMap.size() > 0){
							if(StringUtil.isNull(userInfoMap.get("unionid"))){
								userInfoMap.put("unionid", userInfoMap.get("openid"));
							}
							
							// 微信头像本地下载
							String headImageURL = BaseController.getSocietyIconImage(StringUtil.null2Str(userInfoMap.get("headimgurl")));
							
							userSociety = new UserSociety();
							userSociety.setAppConfigId(weChatAppConfig.getConfigId());						// 对应微信APP配置Id
							userSociety.setUnionId(StringUtil.null2Str(userInfoMap.get("unionid")));		// 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
							userSociety.setOpenId(StringUtil.null2Str(userInfoMap.get("openid")));			// 用户的唯一标识
							userSociety.setNickname(StringUtil.null2Str(userInfoMap.get("nickname")));		// 昵称
							userSociety.setSex(StringUtil.null2Str(userInfoMap.get("sex")));				// 性别
							userSociety.setProvince(StringUtil.null2Str(userInfoMap.get("province")));		// 省份
							userSociety.setCity(StringUtil.null2Str(userInfoMap.get("city")));				// 城市
							userSociety.setCountry(StringUtil.null2Str(userInfoMap.get("country")));		// 国家，如中国为CN
							userSociety.setHeadImgUrl(headImageURL);										// 用户头像地址
							userSociety.setCreateTime(DateUtil.getCurrentDate());							// 创建时间
							userSociety.setUpdateTime(userSociety.getCreateTime());							// 更新时间
							userSociety = this.userSocietyManager.saveUserSociety(userSociety);
							if(userSociety != null 
									&& userSociety.getUserInfo() != null
									&& userSociety.getUserInfo().getUserId() != null){
								log.debug(String.format("weChat--------2222[%s]", userSociety.getUserInfo()));
								this.setWeiXinOauthSessionInfo(request, response, weChatAppConfig, openId, userSociety.getUserInfo());
							}
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				log.debug(String.format("urlCurrent weChat[%s]", state));
				response.sendRedirect(state);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取店铺Id
	 * @param userId
	 * @return
	 */
	private void setWeiXinOauthSessionInfo(HttpServletRequest request, HttpServletResponse response, WeChatAppConfig weChatAppConfig, String openId, UserInfo userInfo){
		List<Long> wechatConfigIdList = new ArrayList<Long>();
		wechatConfigIdList.add(Constants.PUBLIC_ACCOUNT_WECHAT_CONFIG_ID);
		wechatConfigIdList.add(Constants.PUBLIC_INVITE_ACCOUNT_CONFIG_ID);
		
		try {
			// 设置未微信登录
			PortalUtil.saveUserInfo(userInfo, request, response);
			request.getSession().setAttribute(PortalConstants.SESSION_CURRENT_OPEN_ID, StringUtil.null2Str(openId));
			if(weChatAppConfig != null 
					&& weChatAppConfig.getConfigId() != null
					&& wechatConfigIdList.contains(weChatAppConfig.getConfigId())){
				// 检查是否微信授权登陆
				request.getSession().setAttribute(PortalConstants.SESSION_WECHAT_CONFIG_ID, weChatAppConfig.getConfigId());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取h5神策用户distinctid
	 * @param request
	 * @return
	 */
	public static String getDistinctid(HttpServletRequest request) {
		try {
			Cookie[] cookies = request.getCookies();
			String distinctid = "";
			if (cookies != null && cookies.length > 0) {
				for (Cookie cookie : cookies) {
					if (StringUtil.null2Str(cookie.getName()).compareTo("sensorsdata2015jssdkcross") == 0) {
						distinctid = StringUtil.null2Str(cookie.getValue());
						break;
					}
				}
			}
			if(!StringUtil.isNull(distinctid)) {
				String jsonStr = URLDecoder.decode(distinctid, "UTF-8");
				JSONObject jsonObject = JSONObject.fromObject(jsonStr);
				return jsonObject.getString("distinct_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

package com.chunruo.portal;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.chunruo.core.Constants;
import com.chunruo.core.model.UserSociety;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.MessageVo;
import com.chunruo.core.util.vo.SmsCodeVo;
import com.chunruo.portal.controller.SmsController;

public class BaseController {
	protected final transient Log log = LogFactory.getLog(getClass());
	//短信验证码有效时间
  	protected static final int SMSCODE_VALIDTIME = 10;
	
	@Autowired
    private  MessageSource messageSource;
	
	/**
	 * 获取ModelAndView对象
	 * @param request
	 * @return
	 */
	public ModelAndView getModelAndView(HttpServletRequest request){
		return this.getModelAndView(request, null, null);
	}
	
	/**
	 * 获取ModelAndView对象
	 * @param request
	 * @param targetURL
	 * @return
	 */
	public ModelAndView getModelAndView(HttpServletRequest request, String targetURL){
		return this.getModelAndView(request, targetURL, null);
	}
	
	/**
	 * 获取ModelAndView对象
	 * @param request
	 * @param objectMap
	 * @return
	 */
	public ModelAndView getModelAndView(HttpServletRequest request, Map<String, Object> objectMap){
		return this.getModelAndView(request, null, objectMap);
	}
	
	/**
	 * 获取ModelAndView对象
	 * @param request
	 * @param targetURL
	 * @param objectMap
	 * @return
	 */
	public ModelAndView getModelAndView(HttpServletRequest request, String targetURL, Map<String, Object> objectMap){
		String requestURL = StringUtil.null2Str(request.getRequestURI());
		if(targetURL == null){
			targetURL = requestURL.replace(".html", "");
		}
		
		Model model = new ExtendedModelMap();
		try{
			model.addAttribute("redirectURL", request.getSession().getAttribute(PortalConstants.ORDER_REDIRECT_URI));
			if(objectMap != null && objectMap.size() > 0){
				for(Entry<String, Object> entry : objectMap.entrySet()){
					model.addAttribute(entry.getKey(), entry.getValue());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return new ModelAndView(targetURL, model.asMap());
	}
	
    /**
     * Convenience method for getting a i18n key's value.  Calling
     * getMessageSourceAccessor() is used because the RequestContext variable
     * is not set in unit tests b/c there's no DispatchServlet Request.
     *
     * @param msgKey
     * @param locale the current locale
     * @return
     */
	public String getText(String msgKey) {
		try{
			return messageSource.getMessage(msgKey, null, Locale.CHINA);
		}catch(Exception e){
			return msgKey;
		}
	}

    public String getText(String msgKey, Object[] objs) {
        return getText(msgKey, objs, Locale.CHINA);
    }
    
    public String getText(String key, String arg1){
    	List<Object> args = new ArrayList<Object> ();
    	args.add(arg1);
    	return getText(key, args.toArray(new Object[args.size()]));
    }

    /**
     * Convenient method for getting a i18n key's value with a single
     * string argument.
     *
     * @param msgKey
     * @param arg
     * @param locale the current locale
     * @return
     */
    public String getText(String msgKey, String arg, Locale locale) {
        return getText(msgKey, new Object[] { arg }, locale);
    }

    /**
     * Convenience method for getting a i18n key's value with arguments.
     *
     * @param msgKey
     * @param args
     * @param locale the current locale
     * @return
     */
    public String getText(String msgKey, Object[] args, Locale locale) {
    	try{
    		return messageSource.getMessage(msgKey, args, locale);
    	}catch(Exception e){
    		return msgKey;
    	}
    }
    
    /**
     * 请求消息文本返回
     * @param response
     * @param msg
     */
    public void writeTextResponse(final HttpServletResponse response, String msg) {
    	response.setHeader("Cache-Control","no-cache");
    	response.setContentType("text/xml;charset=UTF-8");
		try {
			if (msg != null) {
				response.getWriter().write(msg);
				response.getWriter().flush();
			}
		} catch (Exception e) {
			log.error(e.getCause());
			e.printStackTrace();
		}
	}
    
    /**
	 * 短信验证码校验
	 * @param codeVo
	 * @param mobile
	 * @param veriCode
	 * @return
	 */
	public MessageVo validMSMCode(final HttpServletRequest request, String mobile, String countryCode, String strSmsCode, int codeType){
		MessageVo message = new MessageVo ();
		log.debug(String.format("init[mobile=%s,strSmsCode=%s,codeType=%s]", mobile, strSmsCode, codeType));
		
		// 固定手机号码
		if(StringUtil.compareObject(mobile, "") && StringUtil.compareObject("", strSmsCode)){
			message.setIsSucc(true);
			message.setMessage("固定短信验证成功");
			return message;
		}
		
		SmsCodeVo smsCodeVo = (SmsCodeVo) request.getSession().getAttribute(PortalConstants.SESSION_SMS_CODE);
		if(smsCodeVo == null || smsCodeVo.getCodeType() == null){
			log.debug(String.format("session[mobile=%s,strSmsCode=%s,codeType=%s]null", mobile, strSmsCode, codeType));
			smsCodeVo = SmsController.SMS_MOBILE_CODE_MAP.get(mobile);
			if(smsCodeVo == null || smsCodeVo.getCodeType() == null){
				log.debug(String.format("map[mobile=%s,strSmsCode=%s,codeType=%s]null", mobile, strSmsCode, codeType));
				// 短信验证码错误
				message.setIsSucc(false);
				message.setMessage(this.getText("短信验证码错误"));
				return message;
			}
			
			// 从手机号码中获取短信信息放入缓存中
			request.getSession().setAttribute(PortalConstants.SESSION_SMS_CODE, smsCodeVo);
			log.debug(String.format("map[mobile=%s,strSmsCode=%s,codeType=%s]%s", mobile, strSmsCode, codeType, StringUtil.objectToJSON(smsCodeVo)));
		}else{
			SmsCodeVo tmpCodeVo = SmsController.SMS_MOBILE_CODE_MAP.get(mobile);
			if(tmpCodeVo != null 
					&& StringUtil.compareObject(tmpCodeVo.getMobile(), mobile)
					&& StringUtil.compareObject(tmpCodeVo.getCodeType(), codeType)
					&& StringUtil.compareObject(tmpCodeVo.getSmsCode(), strSmsCode)){
				
				// 从手机号码中获取短信信息放入缓存中
				smsCodeVo = tmpCodeVo;
				request.getSession().setAttribute(PortalConstants.SESSION_SMS_CODE, smsCodeVo);
				log.debug(String.format("map2[mobile=%s,strSmsCode=%s,codeType=%s]%s", mobile, strSmsCode, codeType, StringUtil.objectToJSON(smsCodeVo)));
			}
		}
		
		// 打印短信校验值
		log.debug(String.format("valid[mobile=%s,strSmsCode=%s,codeType=%s]%s", mobile, strSmsCode, codeType, StringUtil.objectToJSON(smsCodeVo)));
		
		// 验证码校验是否session一致
		if(smsCodeVo == null 
				|| smsCodeVo.getMobile() == null 
				|| smsCodeVo.getSmsCode() == null 
				|| smsCodeVo.getCreateTime() == null){
			message.setIsSucc(false);
			message.setMessage(this.getText("短信验证码错误"));
			return message;
		}else if(!mobile.equals(StringUtil.null2Str(smsCodeVo.getMobile()))){
			message.setIsSucc(false);
			message.setMessage(this.getText("手机号码错误"));
			return message;
		}else if(!countryCode.equals(StringUtil.null2Str(smsCodeVo.getCountryCode()))){
			message.setIsSucc(false);
			message.setMessage(this.getText("国家和区域错误"));
			return message;
		}else if(!StringUtil.compareObject(smsCodeVo.getSmsCode(), strSmsCode)){
			message.setIsSucc(false);
			message.setMessage(this.getText("短信验证码错误"));
			return message;
		}else if(!StringUtil.compareObject(smsCodeVo.getCodeType(), codeType)){
			message.setIsSucc(false);
			message.setMessage(this.getText("短信类型错误"));
			return message;
		}else if(!StringUtil.checkSMSCodeValidTime(SMSCODE_VALIDTIME, smsCodeVo.getCreateTime())){
			message.setIsSucc(false);
			message.setMessage(this.getText("短信验证码已过期"));
			return message;
		}
		return message;
	}
	
	/**
	 * 授权信息转换成对象
	 * @param appConfigId
	 * @param oauthInfoMap
	 * @return
	 */
	public UserSociety getUserSocietyByMap(Long appConfigId, Map<String, String> oauthInfoMap){
		//微信授权全新用户注册
		UserSociety userSociety = new UserSociety();
		userSociety.setAppConfigId(appConfigId);										
		
		// 对应微信APP配置Id
		List<Long> configIdList = new ArrayList<Long> ();
		configIdList.add(Constants.MINI_PROGRAM_WECHAT_CONFIG_ID);
//		configIdList.add(Constants_PROGRAM_WECHAT_CONFIG_ID);
		if(configIdList.contains(appConfigId)) {
			//小程序
			if(!StringUtil.isNull(oauthInfoMap.get("unionId"))) {
				userSociety.setUnionId(StringUtil.null2Str(oauthInfoMap.get("unionId")));
			}
			userSociety.setOpenId(StringUtil.null2Str(oauthInfoMap.get("openId")));
			userSociety.setNickname(StringUtil.null2Str(oauthInfoMap.get("nickName")));
			userSociety.setSex(StringUtil.null2Str(oauthInfoMap.get("gender")));				// 性别
			userSociety.setHeadImgUrl(oauthInfoMap.get("avatarUrl"));						// 用户头像地址
		}else {
			if(!StringUtil.isNull(oauthInfoMap.get("unionid"))) {
				userSociety.setUnionId(StringUtil.null2Str(oauthInfoMap.get("unionid")));	// 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
			}
			userSociety.setOpenId(StringUtil.null2Str(oauthInfoMap.get("openid")));
			userSociety.setNickname(StringUtil.null2Str(oauthInfoMap.get("nickname")));		// 昵称
			userSociety.setSex(StringUtil.null2Str(oauthInfoMap.get("sex")));				// 性别
			userSociety.setHeadImgUrl(oauthInfoMap.get("headimgurl"));						// 用户头像地址
		}
		
		
		userSociety.setProvince(StringUtil.null2Str(oauthInfoMap.get("province")));		// 省份
		userSociety.setCity(StringUtil.null2Str(oauthInfoMap.get("city")));				// 城市
		userSociety.setCountry(StringUtil.null2Str(oauthInfoMap.get("country")));		// 国家，如中国为CN
		return userSociety;
	}
	
	/**
	 * 获取社交平台头像地址
	 * @param iconURL
	 * @return
	 */
	public static String getSocietyIconImage(String iconURL){
		if(!StringUtil.isNull(iconURL)){
			try{
				String fileExt = ".jpg";
				String filePath = CoreUtil.dateToPath("images/weixinImage", fileExt);
				
				// 第三方平台头像下载
				URL u = new URL(iconURL);
				HttpURLConnection conn =  (HttpURLConnection)u.openConnection();
				conn.setConnectTimeout(6000);
				
				String fullFilePath = Constants.EXTERNAL_IMAGE_PATH + "/upload/" + filePath;
				FileUtil.checkFileExists(fullFilePath);
				boolean result = FileUploadUtil.copyFile(conn.getInputStream(), fullFilePath);
				if(result && (new File(fullFilePath)).exists()){
					return filePath;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}
}

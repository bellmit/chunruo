package com.chunruo.portal.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.cache.portal.impl.CountryListCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.model.Country;
import com.chunruo.core.model.PaymentVerifyRecord;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.PaymentVerifyRecordManager;
import com.chunruo.core.service.UserInfoManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.AliSendMsgUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.MessageVo;
import com.chunruo.core.util.vo.SmsCodeVo;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.interceptor.LoginInterceptor;
import com.chunruo.portal.util.PortalUtil;

/**
 * 用户注册登录
 * @author chunruo
 *
 */
@Controller
@RequestMapping("/api/user/")
public class SmsController extends BaseController {
	public static List<Integer> SMS_CODETYPE_LIST = new ArrayList<Integer> ();
	public static Map<String, SmsCodeVo> SMS_MOBILE_CODE_MAP = new HashMap<String, SmsCodeVo> ();
	@Autowired
	private UserInfoManager userInfoManager;
	@Autowired
	private CountryListCacheManager countryListCacheManager;
	@Autowired
	private PaymentVerifyRecordManager paymentVerifyRecordManager;
	
	static {
		SMS_CODETYPE_LIST.add(PortalConstants.CODE_TYPE_REGISTER);			// 注册短信
		SMS_CODETYPE_LIST.add(PortalConstants.CODE_TYPE_LOGIN);				// 短信登陆
		SMS_CODETYPE_LIST.add(PortalConstants.CODE_TYPE_BIND_MOBILE);		// 修改手机号码
		SMS_CODETYPE_LIST.add(PortalConstants.CODE_TYPE_FORGET);            // 修改安全密碼
		SMS_CODETYPE_LIST.add(PortalConstants.CODE_TYPE_WECHAT_MODIFY);     // 更换微信短信
		SMS_CODETYPE_LIST.add(PortalConstants.CODE_TYPE_ORDER_PAY);         // 下单短信
	}
			
	/**
	 * 发送短信验证码
	 * @return
	 */
	@RequestMapping(value="/sendCode")
	public @ResponseBody Map<String, Object> sendCode(final HttpServletRequest request, final HttpServletResponse response){
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		String mobile = StringUtil.nullToString(request.getParameter("mobile"));
		Integer codeType = StringUtil.nullToInteger(request.getParameter("codeType"));
		String countryCode = StringUtil.nullToString(request.getParameter("countryCode"));
		String clientIp = StringUtil.null2Str(request.getRemoteAddr());
		log.info("sendVerificationCode[mobile=" + mobile + ", countryCode=" + countryCode + ", clientIp=" + clientIp + "]");
		
		try{
			// 固定手机号码
			if(StringUtil.compareObject(mobile, "")){
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
				resultMap.put(PortalConstants.MSG, "固定短信已发送成功");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// 国际电话编码
			List<String> countryCodeList = new ArrayList<String> (); 
			List<Country> countryList = countryListCacheManager.getSession();
			if(countryList != null && countryList.size() > 0){
				for(Country country : countryList){
					countryCodeList.add(StringUtil.null2Str(country.getTelCode()));
				}
			}
			
			// 发送短信完成清除缓存
			List<Integer> cleanCodeTypeList = new ArrayList<Integer> ();
			cleanCodeTypeList.add(PortalConstants.CODE_TYPE_LOGIN);				// 短信登陆
			
			if(!SMS_CODETYPE_LIST.contains(codeType)){
				//发送短信验证码类型错误
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "发送短信验证码类型错误");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if(StringUtil.isNullStr(countryCode)){
				// 国家地区不能为空
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "国家地区不能为空");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}else if(!countryCodeList.contains(countryCode)){
				// 国家地区不能为空
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "国家和地区不支持");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// 检查手机号码是否有效
			String realMobile = null;
			boolean isMobilePrefixForward = false;
			if(StringUtil.compareObject(UserInfo.DEFUALT_COUNTRY_CODE, countryCode)){
				//安全密码验证码
				List<Integer> smsModifyBindTypeList = new ArrayList<Integer> ();
				smsModifyBindTypeList.add(PortalConstants.CODE_TYPE_FORGET);              // 找回、设置密码短信
				smsModifyBindTypeList.add(PortalConstants.CODE_TYPE_WECHAT_MODIFY);       // 更换微信短信
				smsModifyBindTypeList.add(PortalConstants.CODE_TYPE_ORDER_PAY);           // 下单短信
	
				// 使用用户已存在的手机号码发送短信
				if(smsModifyBindTypeList.contains(codeType) && StringUtil.isNull(mobile)) {
					// 通过缓存信息查询mobile
					UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
					if (userInfo == null || userInfo.getUserId() == null) {
						// 用户未登陆
						resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
						resultMap.put(PortalConstants.MSG, "用户未登陆");
						resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
						return resultMap;
					}
					
					if (!StringUtil.isNull(userInfo.getMobile())){
						mobile = StringUtil.null2Str(userInfo.getMobile());
						countryCode = StringUtil.null2Str(userInfo.getCountryCode());
					}
				}
				
				if(!StringUtil.isValidateMobile(mobile)){
					if(!StringUtil.compareObject(PortalConstants.CODE_TYPE_LOGIN, codeType)){
						// 通过前端传mobile值验证
						resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
						resultMap.put(PortalConstants.MSG, "手机号码无效");
						resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
						return resultMap;
					}
				}
			}
			
			// 修改手机号码
			if(StringUtil.compareObject(PortalConstants.CODE_TYPE_BIND_MOBILE, codeType)){
				// 通过缓存信息查询mobile
				UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
				if (userInfo == null || userInfo.getUserId() == null) {
					// 用户未登陆
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "用户未登陆");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
				
				// 检查用户是否已是代理商用户
				if(StringUtil.nullToBoolean(userInfo.getIsAgent())){
					// 已是代理商用户
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "您已经是纯若微店店主了哦");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
				
				// 检查手机号码是否已绑定其他账号
				UserInfo dbUserInfo = this.userInfoManager.getUserInfoByMobile(mobile, countryCode);
				if(dbUserInfo != null && !StringUtil.compareObject(dbUserInfo.getMobile(), mobile)){
					// 手机号码已存在错误
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
					resultMap.put(PortalConstants.MSG, "手机号已被绑定其他账号,如有疑问请联系客服400-063-9939");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
			}else{
				UserInfo userInfo = this.userInfoManager.getUserInfoByMobile(mobile, countryCode);
				if(StringUtil.compareObject(PortalConstants.CODE_TYPE_LOGIN, codeType)){
					// 短信登陆
					if(userInfo == null || userInfo.getUserId() == null){
						// 手机号码不存在错误
						resultMap.put(PortalConstants.MOBILE_CODE,PortalConstants.MOBILE_ERROR);
						resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
						resultMap.put(PortalConstants.MSG, "手机号码不存在错误");
						resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
						return resultMap;
					}else if(!StringUtil.nullToBoolean(userInfo.getStatus())){
						// 账号已被锁定
						resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
						resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
						resultMap.put(PortalConstants.MSG, "账号已被锁定,请联系客服400-063-9939");
						return resultMap;
					}
				}else if(StringUtil.compareObject(PortalConstants.CODE_TYPE_REGISTER, codeType)){
					List<Integer> userLevelList = new ArrayList<Integer> ();
					userLevelList.add(UserLevel.USER_LEVEL_BUYERS);//店长
					userLevelList.add(UserLevel.USER_LEVEL_DEALER);//经销商
					userLevelList.add(UserLevel.USER_LEVEL_AGENT);//总代
					userLevelList.add(UserLevel.USER_LEVEL_V2);//V2
					userLevelList.add(UserLevel.USER_LEVEL_V3);//V3
					
					// 注册短信
					if(userInfo != null 
							&& userInfo.getUserId() != null
							&& StringUtil.nullToBoolean(userInfo.getIsAgent())
							&& userLevelList.contains(StringUtil.nullToInteger(userInfo.getLevel()))){
						// 手机号码已存在错误
						resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
						resultMap.put(PortalConstants.MSG, "手机号已被绑定其他账号,如有疑问请联系客服400-063-9939");
						resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
						return resultMap;
					}
				}
			}
			
			// 是否使用测试模式
			boolean isTestModelSendSMSCode = StringUtil.nullToBoolean(Constants.conf.getProperty("SMS_TEST_MODEL"));
			if(isTestModelSendSMSCode && !StringUtil.compareObject("13095520537", mobile)){
				String smsCode = StringUtil.null2Str(Constants.conf.getProperty("SMS_TEST_CODE"));
				log.info("sendCode_Test[mobile=" + mobile + ", smsCode=" + smsCode + "]");
				SmsCodeVo codeVo = new SmsCodeVo();
				codeVo.setMobile(mobile);
				codeVo.setSmsCode(smsCode);
				codeVo.setCodeType(codeType);
				codeVo.setCountryCode(countryCode);
				codeVo.setCreateTime(new Date());
				
				// 清除用户登录缓存信息
				if(cleanCodeTypeList.contains(codeType)){
					PortalUtil.removeUserInfo(request);
				}
						
				// 手机短信验证码本次缓存
				SmsController.SMS_MOBILE_CODE_MAP.put(mobile, codeVo);
				
				System.out.println(request.getSession().getId() + "---1");
				request.getSession().setAttribute(PortalConstants.SESSION_SMS_CODE, codeVo);
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
				resultMap.put(PortalConstants.MSG, "短信发送成功");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}

			//生成验证码（四位随机数字）
			int intCount = (new Random()).nextInt(999999);
			if(intCount < 100000) intCount += 100000;  
			String smsCode = StringUtil.null2Str(intCount);
			log.info("sendCode[mobile=" + mobile + ", veriCode=" + smsCode + "]");

			//短信验证码保存到session中
			SmsCodeVo codeVo = new SmsCodeVo();
			codeVo.setMobile(mobile);
			codeVo.setSmsCode(smsCode);
			codeVo.setCodeType(codeType);
			codeVo.setCountryCode(countryCode);
			codeVo.setCreateTime(DateUtil.getCurrentDate());
			
			// 启动手机号码的短信转发
			if(isMobilePrefixForward){
				mobile = realMobile;
			}
			
			// 检查短信类型发送30秒内不能重复发送
			if(SmsController.SMS_MOBILE_CODE_MAP.containsKey(mobile)){
				Date currentDate = new Date(System.currentTimeMillis() - 1000*30);
				SmsCodeVo tmpCodeVo = SmsController.SMS_MOBILE_CODE_MAP.get(mobile);
				if(tmpCodeVo != null 
						&& StringUtil.compareObject(tmpCodeVo.getCodeType(), codeType)
						&& tmpCodeVo.getCreateTime() != null
						&& tmpCodeVo.getCreateTime().compareTo(currentDate) > 0){
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
					resultMap.put(PortalConstants.MSG, "短信已发送成功");
					resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
					return resultMap;
				}
			}
			
			// 调用第三方接口发送短信
			String tplParam = String.format("{\"code\":\"%s\"}", smsCode);
			String templateId = StringUtil.null2Str(Constants.conf.getProperty("ali.send.msg.templateId"));
			if(!AliSendMsgUtil.sendMessage(mobile, templateId, tplParam)) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.MSG, "短信发送失败");
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				return resultMap;
			}
			
			// 打印短信校验值
			log.debug(String.format("send[mobile=%s,codeType=%s]%s", mobile, codeType, StringUtil.objectToJSON(codeVo)));
			
			// 清除用户登录缓存信息
			if(cleanCodeTypeList.contains(codeType)){
				PortalUtil.removeUserInfo(request);
			}
			
			// 手机短信验证码本次缓存
			SmsController.SMS_MOBILE_CODE_MAP.put(mobile, codeVo);
			request.getSession().setAttribute(PortalConstants.SESSION_SMS_CODE, codeVo);
			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.MSG, "短信发送成功");
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.MSG, "短信发送失败");
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
	
	
	/**
	 * 检查验证码
	 * @param request
	 * @return
	 */
	@LoginInterceptor(value=LoginInterceptor.LOGIN)
	@RequestMapping(value="/checkSmsCode")
	public @ResponseBody Map<String, Object> checkSmsCode(final HttpServletRequest request,final HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String smsCode = StringUtil.null2Str(request.getParameter("smsCode"));
		Integer codeType = StringUtil.nullToInteger(request.getParameter("codeType"));
		
		try {
			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			UserInfo userInfoBak = this.userInfoManager.get(userInfo.getUserId());
			if (userInfoBak == null || userInfoBak.getUserId() == null) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "用户不存在");
				return resultMap;
			}
			
			String mobile = StringUtil.null2Str(userInfoBak.getMobile());
			if(StringUtil.isNull(mobile)){
				//注册手机号码不能为空
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "手机号码不能为空");
				return resultMap;
			}else if(StringUtil.isNull(smsCode)){
				// 登陆短信验证码不能为空
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, "短信验证码不能为空");
				return resultMap;
			}
			
			// 验证码校验是否session一致
			MessageVo messageVo = this.validMSMCode(request, mobile, UserInfo.DEFUALT_COUNTRY_CODE, smsCode, codeType);
			if (!messageVo.getIsSucc()) {
				resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
				resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
				resultMap.put(PortalConstants.MSG, StringUtil.null2Str(messageVo.getMessage()));
				return resultMap;
			}
			
			// 设置短信验证成功下个流程验证
			if(StringUtil.compareObject(codeType, PortalConstants.CODE_TYPE_FORGET)) {
				// 修改安全密碼
				request.getSession().setAttribute(PortalConstants.SESSION_MODFIY_FORGET_SMS, true);
			}else if(StringUtil.compareObject(codeType, PortalConstants.CODE_TYPE_WECHAT_MODIFY)) {
				// 绑定微信
				request.getSession().setAttribute(PortalConstants.SESSION_MODFIY_WECHAT_SMS, true);
			}else if(StringUtil.compareObject(codeType, PortalConstants.CODE_TYPE_ORDER_PAY)) {
				// 将支付使用余额短信认证记录保存
				this.paymentVerifyRecordManager.savePaymentVerifyRecord(userInfo.getUserId(), PaymentVerifyRecord.SMS_PAYMENT_VERIFY);
				log.info(String.format("保存验证码redis，账号：%s,验证码：%s", mobile, smsCode));
			}

			resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
			resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
			resultMap.put(PortalConstants.MSG, "短信验证码认证成功");
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_ERROR);
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		resultMap.put(PortalConstants.MSG, "请求失败");
		return resultMap;
	} 
	
}

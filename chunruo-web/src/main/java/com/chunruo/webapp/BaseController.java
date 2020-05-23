package com.chunruo.webapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.Constants;
import com.chunruo.core.util.AliSendMsgUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.User;
import com.chunruo.security.service.UserManager;
import com.chunruo.webapp.filter.SmsCodeFilter;
import com.chunruo.webapp.vo.LoginCodeVo;

@Controller
public class BaseController {
	protected final transient Log log = LogFactory.getLog(getClass());
	public final static String SESSION_CURRENT_USER = "current_user";
	public final static String VERIFY_CODE ="verifyCode";
	@Autowired
	private  UserManager userManager;
	@Autowired
	private  MessageSource messageSource;

	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	@RequestMapping("/loginValid")
	public String loginValid() {
		return "loginValid";
	}

	@RequestMapping("/main")
	public String index(HttpServletRequest request, HttpServletResponse response) {
		// 匿名用户直接跳转登陆页面
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof AnonymousAuthenticationToken) {
			//设置为离线状态
	        new SecurityContextLogoutHandler().logout(request, response, auth);
			return "login";
		}
		return "main";
	}
	
	@RequestMapping("/widget/**")
	public String good(final HttpServletRequest request) {
		String uri = StringUtil.null2Str(request.getRequestURI());
		return uri.substring(1).replace(".html", "");
	}

	@RequestMapping("/scripts/**")
	public String scripts(final HttpServletRequest request) {
		String uri = StringUtil.null2Str(request.getRequestURI());
		return uri.substring(1).replace(".js", "");
	}

	/**
	 * 生成登录验证码
	 * @return
	 */
	@RequestMapping(value="/verifyCode")
	public @ResponseBody Map<String, Object> sendSmsCode(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String username = StringUtil.null2Str(request.getParameter("username"));
		try {
			// 检查登录用户名参数是否为空
			if(StringUtil.isNull(username)) {
				resultMap.put("success", false);
				resultMap.put("message", "登录用户不能为空");
				return resultMap;
			}

			// 检查登录用户是否存在
			User user = this.userManager.getUserByName(username);
			if(user == null || user.getUserId() == null) {
				resultMap.put("success", false);
				resultMap.put("message", "登录用户不存在错误");
				return resultMap;
			}

			// 检查手机号码是否有有效
			if(!StringUtil.isValidateMobile(user.getMobile())){
				resultMap.put("success", false);
				resultMap.put("message", "账户未绑定手机号码,联系管理员");
				return resultMap;
			}

			// 是否使用测试模式
			String mobile = StringUtil.null2Str(user.getMobile());
			boolean isTestModelSendSMSCode = StringUtil.nullToBoolean(Constants.conf.getProperty("IS_TEST_ENV"));
			if(isTestModelSendSMSCode){
				String smsCode = StringUtil.null2Str(Constants.conf.getProperty("SMS_TEST_CODE"));
				log.info("sendCode[mobile=" + mobile + ", username=" + username + ", validateCode=" + smsCode + "]");

				request.getSession().setAttribute(SmsCodeFilter.SMS_CODE, LoginCodeVo.setLoginCode(username, mobile, smsCode));
				resultMap.put("success", true);
				resultMap.put("message", "短信发送成功");
				return resultMap;
			}

			//生成验证码（四位随机数字）
			int intCount = (new Random()).nextInt(999999);
			if(intCount < 100000) intCount += 100000; 
			String smsCode = StringUtil.null2Str(intCount);
			log.info("sendCode[mobile=" + mobile + ", username=" + username + ", validateCode=" + smsCode + "]");

			String tplParam = String.format("{\"code\":\"%s\"}", smsCode);
			String templateId = StringUtil.null2Str(Constants.conf.getProperty("alisms.access.login.templateId"));
			if(AliSendMsgUtil.sendMessage(mobile, templateId, tplParam)) {
				// 更新短信验证码
				request.getSession().setAttribute(SmsCodeFilter.SMS_CODE, LoginCodeVo.setLoginCode(username, mobile, smsCode));
				log.info("登录验证码:"+smsCode);
				resultMap.put("success", true);
				resultMap.put("message", "短信发送成功");
				return resultMap;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("success", false);
		resultMap.put("message", "短信验证码发送错误");
		return resultMap;
	}

	public User getCurrentUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		User currentUser = null;
		if (session.getAttribute(BaseController.SESSION_CURRENT_USER) != null) {
			currentUser = (User) session.getAttribute(BaseController.SESSION_CURRENT_USER);
		} else {
			currentUser = this.userManager.getUserByName(request.getRemoteUser());
			session.setAttribute(BaseController.SESSION_CURRENT_USER, currentUser);
		}
		return currentUser;
	}

	public Long getUserId(HttpServletRequest request){
		return this.getCurrentUser(request).getUserId();
	}

	public String getUserName(HttpServletRequest request){
		return this.getCurrentUser(request).getUsername();
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
	 * 客户端IP地址
	 * @param request
	 * @return
	 */
	public String getClientIp(HttpServletRequest request) {
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
	 *  当前服务器URL地址
	 * @param request
	 * @return
	 */
	public String getRequestURL(HttpServletRequest request){
		String httpstr = "http://" + request.getServerName();
		int port = request.getServerPort();
		if(port != 0 && port != 80){
			httpstr += ":" + port;
		}
		return StringUtil.null2Str(httpstr + request.getContextPath());
	}
}

package com.chunruo.webapp.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.chunruo.core.Constants;
import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.User;
import com.chunruo.security.service.UserManager;
import com.chunruo.webapp.vo.LoginCodeVo;

/**
 * 检验验证码
 * @author Administrator
 *
 */
@Component("smsCodeFilter")
public class SmsCodeFilter extends OncePerRequestFilter{
	public static final String SMS_CODE= "code";
	private AuthenticationFailureHandler authenticationFailureHandler;

	/**
	 * 验证码校验失败处理器
	 * @param authenticationFailureHandler
	 */
	public SmsCodeFilter(AuthenticationFailureHandler authenticationFailureHandler){
		this.authenticationFailureHandler = authenticationFailureHandler;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String uri = StringUtil.null2Str(request.getRequestURI());
		String method = StringUtil.null2Str(request.getMethod());
		if(uri.contains("/j_security_check") && StringUtil.compareObject("post", StringUtil.null2Str(method).toLowerCase())) {
			try {
				//登录检查
				String username = StringUtil.null2Str(request.getParameter("username"));
				String code = StringUtil.null2Str(request.getParameter(SmsCodeFilter.SMS_CODE));
				LoginCodeVo loginCodeVo = (LoginCodeVo) request.getSession().getAttribute(SmsCodeFilter.SMS_CODE);
				if(StringUtil.isNull(username)){
					throw new VerifyCodeErrorException("用户名不能为空");
				}else if(StringUtil.isNull(code)){
					throw new VerifyCodeErrorException("验证码不能为空");
				}else if(loginCodeVo == null || !StringUtil.compareObject(username, loginCodeVo.getUsername())){
					throw new VerifyCodeErrorException("验证码不存在");
				}

				// 检查登录用户是否存在
				UserManager userManager = Constants.ctx.getBean(UserManager.class);
				User user = userManager.getUserByName(username);
				if(user == null || user.getUserId() == null) {
					throw new VerifyCodeErrorException("用户验证码错误");
				}else if(!StringUtil.compareObject(user.getMobile(), loginCodeVo.getMobile())){
					throw new VerifyCodeErrorException("手机验证码错误");
				}else if(!StringUtil.compareObject(code, loginCodeVo.getSmsCode())){
					throw new VerifyCodeErrorException("验证码错误");
				}
			}catch(VerifyCodeErrorException e) {
				authenticationFailureHandler.onAuthenticationFailure(request, response, e);
				return;
			}
		}
		filterChain.doFilter(request, response); 
	}


	public class VerifyCodeErrorException extends AuthenticationException {
		private static final long serialVersionUID = -4893115939456905055L;
		
		public VerifyCodeErrorException(String msg) {
			super(msg);
		}

		public VerifyCodeErrorException(String msg, Throwable t) {
			super(msg, t);
		}
	}
}

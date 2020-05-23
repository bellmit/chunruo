package com.chunruo.webapp.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import com.chunruo.core.Constants;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.filter.SmsCodeFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	private MyUserDetailsService myUserDetailsService;
	@Autowired
	private DataSource dataSource;
	@Autowired
	private MyAccessDeniedHandler myAccessDeniedHandler;
	@Autowired
	private AuthenticationFailureHandler myAuthenctiationFailureHandler;
	@Autowired
	private AuthenticationSuccessHandler myAuthenctiationSuccessHandler;

	/**
	 * 授权需要放行的URL
	 */
	private static final String[] SECURITYCONFIG_AUTH_WHITELIST = {
		"/xedit/**",
		"/jquery/**",
		"/images/**",
		"/ximages/**",
		"/styles/**",
		"/widget/**", 
		"/upload/**",
		"/verifyCode", 
		"/login", 
		"/main.html",
		"/logout"
	};

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Autowired
	public void setMyUserDetailsService(MyUserDetailsService myUserDetailsService) {
		this.myUserDetailsService = myUserDetailsService;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 短信验证码验证
		SmsCodeFilter smsCodeFilter = new SmsCodeFilter(myAuthenctiationFailureHandler);
		// 基于Spring的企业应用系统提供声明式的安全访问控制解决方案的安全框架
		http.headers().frameOptions().disable();
		http.authorizeRequests()
		.antMatchers(SECURITYCONFIG_AUTH_WHITELIST).permitAll() //这些请求所有人都可以访问
		.anyRequest().authenticated()   //所有请求都需要认证
		.and()
		.authorizeRequests()
		.anyRequest()
		.access("@rbacAuthorityService.hasPermission(request,authentication)")
		.and()
		.formLogin()
		.loginPage("/login")
		.loginProcessingUrl("/j_security_check")  // 指定form表单提交地址
		.failureUrl("/login?error")
		.defaultSuccessUrl("/main")
		.failureHandler(myAuthenctiationFailureHandler)
		.successHandler(myAuthenctiationSuccessHandler)
		.and()
		.rememberMe()
		.tokenRepository(jdbcTokenRepositoryImpl())
		.tokenValiditySeconds(60 * 60 * 24 * 30)      //有效期一个月
		.userDetailsService(myUserDetailsService)   
		.and()
		.addFilterBefore(smsCodeFilter, UsernamePasswordAuthenticationFilter.class)
		.exceptionHandling().authenticationEntryPoint(myAccessDeniedHandler)
		.accessDeniedHandler(myAccessDeniedHandler)
		.and().csrf().disable()
		.sessionManagement()
		.invalidSessionUrl("/login?error")
		.maximumSessions(1)
		.maxSessionsPreventsLogin(false)
		.expiredSessionStrategy(new CustomExpiredSessionStrategy("test", myUserDetailsService))
		.sessionRegistry(sessionRegistry());
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
	}

	/**
	 * 密码加密方式
	 * @return
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	/**
	 * 获取记住我数据库信息
	 * @return
	 */
	@Bean
	public JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl() {
		JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl = new JdbcTokenRepositoryImpl();
		jdbcTokenRepositoryImpl.setDataSource(dataSource);
		return jdbcTokenRepositoryImpl;
	}

	/**
	 * 已经另一台机器登录，您被迫下线
	 * @author chunruo
	 */
	public class CustomExpiredSessionStrategy extends AbstractRememberMeServices implements SessionInformationExpiredStrategy{
	
		protected CustomExpiredSessionStrategy(String key, UserDetailsService userDetailsService) {
			super(key, userDetailsService);
		}

		@Override
		public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
			Map<String, Object> map = new HashMap<>(16);
			map.put("success", false);
			map.put("isLogin", true);
			map.put("message", String.format("已经另一台机器登录，您被迫下线[%]", DateUtil.formatDate(DateUtil.DATE_TIME_MS_PATTERN, event.getSessionInformation().getLastRequest())));

			try{
				//获得注销用户的信息
		        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
		        if (auth != null){
		            //设置为离线状态
		           new SecurityContextLogoutHandler().logout(event.getRequest(), event.getResponse(), auth);
		        }
		        
				// 清除记住我缓存记录
				String[] cookieTokens = null;
				String rememberMeCookie = this.extractRememberMeCookie(event.getRequest());
				if (!StringUtil.isNull(rememberMeCookie) 
						&& (cookieTokens = this.decodeCookie(rememberMeCookie)) != null
						&& cookieTokens.length > 0) {
					PersistentTokenRepository tokenRepository = Constants.ctx.getBean(PersistentTokenRepository.class);
					if(tokenRepository != null){
						PersistentRememberMeToken token = tokenRepository.getTokenForSeries(StringUtil.null2Str(cookieTokens[0]));
						if(token != null && !StringUtil.isNull(token.getUsername())){
							tokenRepository.removeUserTokens(token.getUsername());
							this.cancelCookie(event.getRequest(), event.getResponse());
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}

			event.getResponse().setStatus(HttpStatus.FORBIDDEN.value());
			event.getResponse().setContentType("application/json;charset=UTF-8");
			event.getResponse().getWriter().write(StringUtil.objToJson(map));
		}

		@Override
		protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
				Authentication successfulAuthentication) {
		}

		@Override
		protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
				HttpServletResponse response) throws RememberMeAuthenticationException, UsernameNotFoundException {
			return null;
		}
	}
}

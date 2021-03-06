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
	 * ?????????????????????URL
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
		// ?????????????????????
		SmsCodeFilter smsCodeFilter = new SmsCodeFilter(myAuthenctiationFailureHandler);
		// ??????Spring????????????????????????????????????????????????????????????????????????????????????
		http.headers().frameOptions().disable();
		http.authorizeRequests()
		.antMatchers(SECURITYCONFIG_AUTH_WHITELIST).permitAll() //????????????????????????????????????
		.anyRequest().authenticated()   //???????????????????????????
		.and()
		.authorizeRequests()
		.anyRequest()
		.access("@rbacAuthorityService.hasPermission(request,authentication)")
		.and()
		.formLogin()
		.loginPage("/login")
		.loginProcessingUrl("/j_security_check")  // ??????form??????????????????
		.failureUrl("/login?error")
		.defaultSuccessUrl("/main")
		.failureHandler(myAuthenctiationFailureHandler)
		.successHandler(myAuthenctiationSuccessHandler)
		.and()
		.rememberMe()
		.tokenRepository(jdbcTokenRepositoryImpl())
		.tokenValiditySeconds(60 * 60 * 24 * 30)      //??????????????????
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
	 * ??????????????????
	 * @return
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	/**
	 * ??????????????????????????????
	 * @return
	 */
	@Bean
	public JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl() {
		JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl = new JdbcTokenRepositoryImpl();
		jdbcTokenRepositoryImpl.setDataSource(dataSource);
		return jdbcTokenRepositoryImpl;
	}

	/**
	 * ?????????????????????????????????????????????
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
			map.put("message", String.format("?????????????????????????????????????????????[%]", DateUtil.formatDate(DateUtil.DATE_TIME_MS_PATTERN, event.getSessionInformation().getLastRequest())));

			try{
				//???????????????????????????
		        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
		        if (auth != null){
		            //?????????????????????
		           new SecurityContextLogoutHandler().logout(event.getRequest(), event.getResponse(), auth);
		        }
		        
				// ???????????????????????????
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

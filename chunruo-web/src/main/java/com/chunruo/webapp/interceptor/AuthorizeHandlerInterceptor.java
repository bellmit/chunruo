package com.chunruo.webapp.interceptor;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.chunruo.core.Constants;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.Resource;
import com.chunruo.security.model.User;
import com.chunruo.security.service.ResourceManager;
/**
 * 访问权限拦截
 * @author chunruo
 */
public class AuthorizeHandlerInterceptor extends HandlerInterceptorAdapter {
	protected final transient Log log = LogFactory.getLog(getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		// 已正确配置权限
		return true;
//		String requestURL = StringUtil.null2Str(request.getRequestURI());
//		// 特殊请求拦截访问权限
//		AuthorizeInterceptor interceptorRef = ((HandlerMethod) handler).getMethodAnnotation(AuthorizeInterceptor.class);
//		if(interceptorRef != null 
//				&& !StringUtil.isNull(interceptorRef.value())
//				&& StringUtil.null2Str(interceptorRef.value()).contains("=")) {
//			String[] params = StringUtil.null2Str(interceptorRef.value()).split("=");
//			if (params.length == 2) {
//				String key = StringUtil.null2Str(params[0]);
//				String value = StringUtil.null2Str(params[1]).toLowerCase();
//				Map<String, String[]> paramMap = request.getParameterMap();
//				if(paramMap != null 
//						&& paramMap.size() > 0
//						&& paramMap.containsKey(key)
//						&& StringUtil.compareObject(value, StringUtil.null2Str(paramMap.get(key)[0]).toLowerCase())){
//					requestURL = String.format("%s?%s", requestURL, interceptorRef.value());
//					// 参数请求与拦截访问参数一致(单个参数)
//					Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//					if(authentication != null){
//						Object principal = authentication.getPrincipal();
//						if (principal instanceof UserDetails) {
//							// 检查用户是否配合权限
//							User user = (User) principal;
//							if(StringUtil.nullToBoolean(user.getIsAdmin())) {
//							    return true;
//							}
//							if(user.getAuthorities() != null && user.getAuthorities().size() > 0){
//								for(GrantedAuthority authority : user.getAuthorities()){
//									if(StringUtil.compareObject(authority.getAuthority(), StringUtil.null2Str(requestURL).toLowerCase())){
//										// 已正确配置权限
//										return true;
//									}
//								}
//							}
//
//							// 自动补缺少的URL请求地址
//							ResourceManager resourceManager = Constants.ctx.getBean(ResourceManager.class);
//							if(!resourceManager.isExistLinkPath(requestURL, null)){
//								Resource resource = new Resource ();
//								resource.setIsEnable(false);
//								resource.setLinkPath(requestURL);
//								resource.setName("自动补权限地址" + DateUtil.formatDate(DateUtil.DATE_TIME_MS_PATTERN, DateUtil.getCurrentDate()));
//								resource.setCreateTime(DateUtil.getCurrentDate());
//								resource.setUpdateTime(resource.getCreateTime());
//								resourceManager.save(resource);
//							}
//
//							Map<String, Object> dataMap = new HashMap<String, Object> ();
//							dataMap.put("success", false); 
//							dataMap.put("message", "你无权限访问");
//
//							response.setStatus(HttpStatus.FORBIDDEN.value());
//							response.setContentType("application/json;charset=UTF-8");
//							response.getWriter().write(StringUtil.objToJson(dataMap));
//							return false;
//						}
//					}
//				}
//			}
//		}
//		return true;
	}
}

package com.chunruo.webapp.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.chunruo.core.Constants;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.Resource;
import com.chunruo.security.model.User;
import com.chunruo.security.service.ResourceManager;
import javax.servlet.http.HttpServletRequest;

@Component("rbacAuthorityService")
public class RbacAuthorityService {

	/**
	 * RBAC需要放行的URL
	 */
	private static final String[] AUTH_WHITELIST = {
		"/favicon.ico",
		"/main.html",
		"/ext/**",
		"/common/**",
		"/classic/**",
		"/packages/**",
		"/depository/**",
		"/upload/images/**",
		"/scripts",
		"/InitModel",
		"/.js",
		"/**/**.jpg",
		"/**/**.png",
		"/**/**.jpeg",
		"/null",
		"/userSys/myUser.json",
		"/userSys/updateMyUser.json",
		"/category/**.jpg",
		"/ApplyAgent.js"
	};
	
	/**
	 * 检查访问权限是否有效
	 * @param requestURL
	 * @return
	 */
	public static boolean isExistRbacAuthority(HttpServletRequest request,String requestURL){
		try{
			
			return true;
//			//获得用户的信息
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			if (auth != null 
//					&& auth.getPrincipal() != null
//					&& auth.getPrincipal() instanceof User){
//				// 检查用户是否配合权限
//				User user = (User) auth.getPrincipal();
//				if(StringUtil.nullToBoolean(user.getIsAdmin())) {
//					return true;
//				}
//				if(user.getAuthorities() != null && user.getAuthorities().size() > 0){
//					for(GrantedAuthority authority : user.getAuthorities()){
//						if(StringUtil.compareObject(authority.getAuthority(), StringUtil.null2Str(requestURL).toLowerCase())
//								|| StringUtil.compareObject(request.getRequestURI(), authority.getAuthority())){
//							// 已正确配置权限
//							return true;
//						}
//					}
//				}
//			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 正则表达式匹配
	 * @param lookupPath
	 * @return
	 */
	public boolean noMatches(String[] authWhiteList, String lookupPath) {
		PathMatcher matcher = new AntPathMatcher(); 
		if (authWhiteList != null && authWhiteList.length > 0) {
			for (String pattern : authWhiteList) {
				if (matcher.match(pattern, lookupPath)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 检查url授权
	 * @param request
	 * @param authentication
	 * @return
	 */
	public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
		return true;
//		// 静态文件过滤
//		String requestURL = StringUtil.null2Str(request.getRequestURI());
//		if(this.noMatches(RbacAuthorityService.AUTH_WHITELIST, requestURL)){
//			return true;
//		}
//
//		Object principal = authentication.getPrincipal();
//		if (principal instanceof UserDetails) {
//			if(this.noMatches(new String[]{"/scripts/**/*.js"}, requestURL)){
//				return true;
//			}
//			
//			// 检查用户是否配合权限
//			User user = (User) principal;
//			if(user.getAuthorities() != null && user.getAuthorities().size() > 0){
//				for(GrantedAuthority authority : user.getAuthorities()){
//					if(StringUtil.nullToBoolean(user.getIsAdmin()) 
//							|| StringUtil.compareObject(authority.getAuthority(), StringUtil.null2Str(requestURL).toLowerCase())){
//						// 已正确配置权限
//						return true;
//					}
//				}
//			}
//
//			// 自动补缺少的URL请求地址
//			ResourceManager resourceManager = Constants.ctx.getBean(ResourceManager.class);
//			if(!resourceManager.isExistLinkPath(requestURL, null)){
//				Resource resource = new Resource ();
//				resource.setIsEnable(false);
//				resource.setLinkPath(requestURL);
//				resource.setName("自动补权限地址" + DateUtil.formatDate(DateUtil.DATE_TIME_MS_PATTERN, DateUtil.getCurrentDate()));
//				resource.setCreateTime(DateUtil.getCurrentDate());
//				resource.setUpdateTime(resource.getCreateTime());
//				resourceManager.save(resource);
//			}
//		}else{
//			//获得注销用户的信息
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			if (auth != null){
//				//设置为离线状态
//				new SecurityContextLogoutHandler().logout(request, null, auth);
//			}
//		}
//		return false;
	}
}

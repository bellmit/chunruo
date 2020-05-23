package com.chunruo.webapp.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.chunruo.core.util.StringUtil;
import com.chunruo.security.model.User;

/**
 * 权限模块控制
 * @author chunruo
 *
 */
public class HaveAuthorizeTag extends TagSupport {
	private static final long serialVersionUID = 7471128090872333243L;
	private String access;

	@Override
	public int doStartTag() throws JspException {
		Set<String> authorityList = new HashSet<String> ();
		SecurityContext ctx = SecurityContextHolder.getContext();
		if (ctx.getAuthentication() != null) {
			Authentication auth = ctx.getAuthentication();
			if(skipAOP(auth)) return SKIP_BODY;
			
			if (auth != null 
					&& auth.getPrincipal() != null
					&& auth.getPrincipal() instanceof User){
				// 检查用户是否配合权限
				User user = (User) auth.getPrincipal();
				if(StringUtil.nullToBoolean(user.getIsAdmin())) {
					return EVAL_BODY_INCLUDE;
				}
			}
			Collection<? extends GrantedAuthority> grantedAuthority = auth.getAuthorities();
			for (GrantedAuthority authority : grantedAuthority) {
				authorityList.add(StringUtil.null2Str(authority.getAuthority()).toLowerCase());
			}
		}
		
//		boolean isHavePerm = false;
//		String[] accesss = StringUtil.null2Str(this.access).split(",");
//		if(accesss != null && accesss.length > 0){
//			isHavePerm = true;
//			for(int i = 0; i < accesss.length; i ++){
//				String accesssURL = StringUtil.null2Str(accesss[i]).toLowerCase();
//				if(!authorityList.contains(accesssURL)){
//					isHavePerm = false;
//					break;
//				}
//			}
//		}
		
		return EVAL_BODY_INCLUDE;
//		if(isHavePerm){
//			return EVAL_BODY_INCLUDE;
//		}else{
//			return SKIP_BODY;
//		}
	}

	private boolean skipAOP(Authentication auth) {
		if (auth.getPrincipal() instanceof UserDetails
				|| auth.getDetails() instanceof UserDetails) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public void release() {
		super.release();
		this.access = null;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}
	
}

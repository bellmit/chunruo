package com.chunruo.portal.interceptor;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.PortalUtil;

public class LoginHandlerInterceptor extends HandlerInterceptorAdapter {
	protected final transient Log log = LogFactory.getLog(getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 方法配置申明需要登录
		if(handler instanceof HandlerMethod) {
			LoginInterceptor interceptorRef = ((HandlerMethod) handler).getMethodAnnotation(LoginInterceptor.class);
			if(interceptorRef != null && StringUtil.compareObject(interceptorRef.value(), LoginInterceptor.LOGIN)) {
				UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
				if (userInfo != null 
						&& userInfo.getUserId() != null
						&& StringUtil.nullToBoolean(userInfo.getStatus())
						&& StringUtil.nullToLong(userInfo.getUserId()).compareTo(0L) > 0) {
					// 登陆成功
					return true;
				}else {
					//客户端未登录提示用户信息
					Map<String, Object> resultMap = new HashMap<String, Object> ();
					resultMap.put(PortalConstants.CODE, PortalConstants.CODE_NOLOGIN);
					resultMap.put(PortalConstants.MSG, "未登陆");
					resultMap.put(PortalConstants.SYSTEMTIME, StringUtil.null2Str(DateUtil.getCurrentTime()));
					response.setContentType("application/json;charset=UTF-8");
					response.setHeader(PortalConstants.X_TOKEN, PortalConstants.TOKEN_DEFUALT_OVERDUE);
					OutputStream out = response.getOutputStream();
					PrintWriter pw = new PrintWriter(new OutputStreamWriter(out,"utf-8"));
					pw.println(StringUtil.strMapToJSON(resultMap));
					pw.flush();
					pw.close();
					return false;
				}	
			}
		}
		return true;
	}
}

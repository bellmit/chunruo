package com.chunruo.webapp.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.chunruo.core.util.StringUtil;

/**
 * 访问无权限
 * @author chunruo
 *
 */
@Component("myAccessDeniedHandler")
public class MyAccessDeniedHandler implements AuthenticationEntryPoint, AccessDeniedHandler{

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		Map<String, Object> dataMap = new HashMap<String, Object> ();
		dataMap.put("success", false); 
		dataMap.put("message", "你无权限访问");
		
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(StringUtil.objToJson(dataMap));
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		Map<String, Object> dataMap = new HashMap<String, Object> ();
		dataMap.put("success", false); 
		dataMap.put("message", "你无权限访问");
		
		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(StringUtil.objToJson(dataMap));
	}
}
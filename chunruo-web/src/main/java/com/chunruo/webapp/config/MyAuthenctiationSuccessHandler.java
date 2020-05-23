package com.chunruo.webapp.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.chunruo.core.util.StringUtil;

@Component("myAuthenctiationSuccessHandler")
public class MyAuthenctiationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		try {
			response.setHeader("Content-Type", "application/json;charset=UTF-8");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("success", "true");
			dataMap.put("data", "/main.html");

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("model", dataMap);
			response.getWriter().write(StringUtil.strMapToJSON(resultMap));
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

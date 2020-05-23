package com.chunruo.webapp.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import com.chunruo.core.util.StringUtil;

@Component("myAuthenctiationFailureHandler")
public class MyAuthenctiationFailureHandler extends SimpleUrlAuthenticationFailureHandler{

	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		try{
		response.setHeader("Content-Type", "application/json;charset=UTF-8");
		Map<String, Object> dataMap = new HashMap<String, Object> ();
		dataMap.put("success", false); 
		dataMap.put("message", StringUtil.null2Str(exception.getMessage()));

		Map<String, Object> resultMap = new HashMap<String, Object> ();
		resultMap.put("model", dataMap);
		request.getSession().setAttribute("model", dataMap);
		response.getWriter().write(StringUtil.strMapToJSON(resultMap));
	    response.getWriter().flush();
		}catch(Exception e){
		e.printStackTrace();
	}
}

}

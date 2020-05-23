package com.chunruo.portal;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.util.RequestUtil;

@Controller
public class PortalBaseController extends BaseController{
	public final static String VERIFY_CODE ="verifyCode";
	
	@RequestMapping("/**.html")
	public String jstlView(final HttpServletRequest request, final HttpServletResponse response) {
		String uri = StringUtil.null2Str(request.getRequestURI());
		StringBuffer urlBuffer = new StringBuffer(uri.replace(".html", ""));
		if(!StringUtil.isNull(request.getParameter("a"))){
			urlBuffer.append("/" + StringUtil.null2Str(request.getParameter("a")));
		}

		// 如果请求参数包含storeId参数
		if(request.getParameterMap().containsKey("storeId")){
			response.setHeader("Location", uri.replace(".html", ""));
		}
		return urlBuffer.toString();
	}

	/**
	 * 客户端请求json格式
	 */
	@RequestMapping("/clt/index.json")
	public ModelAndView cltJstlView(final HttpServletRequest request, final HttpServletResponse response) {
		StringBuffer urlBuffer = new StringBuffer("/clt/");
		if(!StringUtil.isNull(request.getParameter("a"))){
			String urlName = StringUtil.null2Str(request.getParameter("a"));
			urlBuffer.append(urlName);
		}
		
		
		// 公共请求参数
		Map<String, Object> resultMap = new HashMap<String, Object> ();
        resultMap.put("requestURL", RequestUtil.getRequestURL(request));
        resultMap.put("systemTime", System.currentTimeMillis());

		response.setContentType("application/json");
		return this.getModelAndView(request, urlBuffer.toString(), resultMap);
	}
	
}

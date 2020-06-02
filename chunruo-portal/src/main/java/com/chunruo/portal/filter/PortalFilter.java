package com.chunruo.portal.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import com.chunruo.core.util.StringUtil;

public class PortalFilter extends OncePerRequestFilter {
	private static Log log = LogFactory.getLog(PortalFilter.class);
	private final List<String> excludePatterns = new ArrayList<String>();

	public PortalFilter(){
		this.excludePathPatterns("/clt/weixin/**.msp");
		this.excludePathPatterns("/clt/miniProgram/**.msp");
		this.excludePathPatterns("/clt/order/wxpayNotify.msp");
	}
	

	public PortalFilter excludePathPatterns(String... patterns) {
		this.excludePatterns.addAll(Arrays.asList(patterns));
		return this;
	}

	public boolean noMatches(String lookupPath) {
		PathMatcher matcher = new AntPathMatcher(); 
		if (this.excludePatterns != null) {
			for (String pattern : this.excludePatterns) {
				if (matcher.match(pattern, lookupPath)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try{
			String requestURL = StringUtil.null2Str(request.getRequestURI());;
			String contextPath = request.getContextPath();
			if (requestURL.startsWith(contextPath)) {
				requestURL = requestURL.substring(contextPath.length());
			}


			// .msp业务接口处理
			if(requestURL.endsWith(".msp")){
				String baseRequestURL = requestURL.substring(0, requestURL.lastIndexOf(".msp"));
				requestURL = StringUtil.null2Str(baseRequestURL).replace("/clt/", "/api/");
			}

			request.getRequestDispatcher(requestURL).forward(request, response);
			return;
		}catch(Exception e){
			log.debug(e.getMessage());
		}

		chain.doFilter(request, response);
	}
}

package com.chunruo.portal.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
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

import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.RequestUtil;

/**
 * 客户端请求加密
 * @author chunruo
 *
 */
public class PortalFilter extends OncePerRequestFilter {
	public final static String REQUST_GZIP_TYPE = "REQEUST_GZIP_TYPE";	//客户端请求加密类型
	private static Log log = LogFactory.getLog(PortalFilter.class);
	private final List<String> excludePatterns = new ArrayList<String>();

	public PortalFilter(){
		// 设置请求URL不走加密方式
		this.excludePathPatterns("/clt/weixin/**.msp");
		this.excludePathPatterns("/clt/enterprise/**.msp");
		this.excludePathPatterns("/clt/miniProgram/**.msp");
		this.excludePathPatterns("/clt/pay/appJumpPay.msp");
		this.excludePathPatterns("/clt/order/wxpayNotify.msp");
		this.excludePathPatterns("/clt/order/alipayNotify.msp");
		this.excludePathPatterns("/clt/order/huifuNotify.msp");
		this.excludePathPatterns("/clt/order/huifuResultNotify.msp");
		this.excludePathPatterns("/clt/order/huifuCustomNotify.msp");
		this.excludePathPatterns("/clt/order/huifuRefundNotify.msp");
		this.excludePathPatterns("/clt/auth/wxpayNotify.msp");
		this.excludePathPatterns("/clt/auth/alipayNotify.msp");
		this.excludePathPatterns("/clt/userRecharge/wxpayNotify.msp");
		this.excludePathPatterns("/clt/userRecharge/alipayNotify.msp");
		this.excludePathPatterns("/clt/orderStack/download.msp");
		this.excludePathPatterns("/clt/friendPayment/wxpayNotify.msp");
		this.excludePathPatterns("/clt/friendPayment/alipayNotify.msp");
		this.excludePathPatterns("/clt/idAuthen/queryListByIdCardNo.msp");
		this.excludePathPatterns("/clt/idAuthen/requestAuthen.msp");
		this.excludePathPatterns("/clt/easypay/**.msp");
		this.excludePathPatterns("/clt/testapp/**.msp");
	}
	


	/**
	 * Add URL patterns to which the registered interceptor should not apply to.
	 */
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

	protected boolean acceptsGzipEncoding(HttpServletRequest request) {
		return acceptsEncoding(request, "gzip");
	}

	protected boolean acceptsEncoding(final HttpServletRequest request, final String name) {
		final boolean accepts = headerContains(request, "BDAccept-Encoding", name);
		return accepts;
	}

	/**
	 * 头参数是否包含
	 * @param request
	 * @param header
	 * @param value
	 * @return
	 */
	private boolean headerContains(final HttpServletRequest request, final String header, final String value) {
		@SuppressWarnings("rawtypes")
		final Enumeration accepted = request.getHeaders(header);
		while (accepted.hasMoreElements()) {
			final String headerValue = (String) accepted.nextElement();
			if (headerValue.indexOf(value) != -1) {
				return true;
			}
		}
		return false;
	}
}

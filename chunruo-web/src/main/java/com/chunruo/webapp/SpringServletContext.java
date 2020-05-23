package com.chunruo.webapp;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.alipay.config.AlipayConfig;
import com.chunruo.core.Constants;
import com.chunruo.core.util.CoreInitUtil;
import com.chunruo.security.SecurityConstants;

@Component
public class SpringServletContext implements ServletContextAware{
	public static ServletContext context;

	@Override
	public void setServletContext(ServletContext servletContext) {
		SpringServletContext.context = servletContext;
		try{
			Constants.SERVER_REAL_PATH = context.getRealPath("/");
			Constants.EXTERNAL_IMAGE_PATH = Constants.conf.getProperty("jkd.external.image.path");
			Constants.DEPOSITORY_PATH = Constants.EXTERNAL_IMAGE_PATH + "/" + Constants.DEPOSITORY;
			
		
			CoreInitUtil.initAreaConstantsList();
			CoreInitUtil.initProductIntroConstantsList();
			CoreInitUtil.initProductCountryConstantsList();
			CoreInitUtil.initProductCategoryConstantsList();
			CoreInitUtil.initPurchaseDoubtConstantsList();
			CoreInitUtil.initWeChatAppConfigConstantsList();
			SecurityConstants.initMenuMap();
			SpringServletContext.initAlipayConfig();
			
    		context.setAttribute(Constants.MENU_TREE_MAPS, SecurityConstants.MENU_TREE_NODE.getChildrenNode());
    		context.setAttribute(Constants.PRODUCT_INTRO_MAPS, Constants.PRODUCT_INTRO_MAP);
    		context.setAttribute(Constants.PRODUCT_COUNTRY_MAPS, Constants.PRODUCT_COUNTRY_MAP);
    		context.setAttribute(Constants.PRODUCT_CATEGORY_LISTS, Constants.PRODUCT_CATEGORY_TREE_LIST);
    		context.setAttribute(Constants.PRODUCT_WAREHOUSE_MAPS, Constants.PRODUCT_WAREHOUSE_MAP);
    		context.setAttribute(Constants.HANDER_WAREHOUSE_MAPS, Constants.HANDER_WAREHOUSE_MAP);
    		context.setAttribute(Constants.EXPRESS_CODE_MAPS, Constants.EXPRESS_CODE_MAP);
    		context.setAttribute(Constants.NOFree_POSTAGE_TEMPLATE_MAPS, Constants.NOFree_POSTAGE_TEMPLATE_MAP);
    		context.setAttribute(Constants.BANK_MAPS, Constants.BANK_MAP);
    		context.setAttribute(Constants.PURCHASE_DOUBT_MAPS, Constants.PURCHASE_DOUBT_MAP);
    		context.setAttribute(Constants.PRODUCT_BRAND_MAPS, Constants.PRODUCT_BRAND_MAP);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
	
	/**
	 * 支付宝配置初始化
	 */
	public static void initAlipayConfig(){
		// 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
		AlipayConfig.partner = Constants.conf.getProperty("alipay.partner");
		// 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
		AlipayConfig.seller_id = AlipayConfig.partner;
		// MD5密钥，安全检验码，由数字和字母组成的32位字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
		AlipayConfig.key = Constants.conf.getProperty("alipay.key");
		// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
		AlipayConfig.notify_url = Constants.conf.getProperty("alipay.notify_url");
		// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
		AlipayConfig.return_url = Constants.conf.getProperty("alipay.return_url");
		// 签名方式
		AlipayConfig.sign_type = "MD5";
		// 调试用，创建TXT日志文件夹路径，见AlipayCore.java类中的logResult(String sWord)打印方法。
		AlipayConfig.log_path = "C:\\";	
		// 字符编码格式 目前支持 gbk 或 utf-8
		AlipayConfig.input_charset = "utf-8";
		// 支付类型 ，无需修改
		AlipayConfig.payment_type = "1";
		// 调用的接口名，无需修改
		AlipayConfig.service = Constants.conf.getProperty("alipay.service");
		// 防钓鱼时间戳  若要使用请调用类文件submit中的query_timestamp函数
		AlipayConfig.anti_phishing_key = "";
		// 客户端的IP地址 非局域网的外网IP地址，如：221.0.0.1
		AlipayConfig.exter_invoke_ip = "";
	}
}

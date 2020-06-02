package com.chunruo.webapp;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
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
			Constants.EXTERNAL_IMAGE_PATH = Constants.conf.getProperty("chunruo.default.image.path");
			Constants.DEPOSITORY_PATH = Constants.EXTERNAL_IMAGE_PATH + "/" + Constants.DEPOSITORY;
			
		
			CoreInitUtil.initAreaConstantsList();
			CoreInitUtil.initProductCountryConstantsList();
			CoreInitUtil.initProductCategoryConstantsList();
			CoreInitUtil.initWeChatAppConfigConstantsList();
			SecurityConstants.initMenuMap();
			
    		context.setAttribute(Constants.MENU_TREE_MAPS, SecurityConstants.MENU_TREE_NODE.getChildrenNode());
    		context.setAttribute(Constants.PRODUCT_CATEGORY_LISTS, Constants.PRODUCT_CATEGORY_TREE_LIST);
    		context.setAttribute(Constants.PRODUCT_WAREHOUSE_MAPS, Constants.PRODUCT_WAREHOUSE_MAP);
    		context.setAttribute(Constants.HANDER_WAREHOUSE_MAPS, Constants.HANDER_WAREHOUSE_MAP);
    		context.setAttribute(Constants.NOFree_POSTAGE_TEMPLATE_MAPS, Constants.NOFree_POSTAGE_TEMPLATE_MAP);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
	
}

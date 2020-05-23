package com.chunruo.core.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.chunruo.core.Constants;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.util.StringUtil;

public class PortalUtil {
	protected final static transient Log log = LogFactory.getLog(PortalUtil.class);
	public static ResourceBundle resourceBundle = null;
	public static final String BUNDLE_KEY = "ApplicationResources";
	
	public static String getText(String key) {
		return getText(key, null);
	}
	
	public static String getText(String key, Object[] args) {
		if (PortalUtil.resourceBundle == null)
			PortalUtil.resourceBundle = ResourceBundle.getBundle(PortalUtil.BUNDLE_KEY, Locale.CHINA);
		if (PortalUtil.resourceBundle != null) {
			try {
				String result = PortalUtil.resourceBundle.getString(key);
				if (result != null)
					return MessageFormat.format(result, args);
				else
					return result;
			} catch (Exception e) {
				return key;
			}
		}
		return "";
	}	
	
	/**
	 * 检查仓库是否存在
	 * @param wareHouseId
	 * @return
	 */
	public static MsgModel<ProductWarehouse> checkProductWarehouse(Long wareHouseId){
		MsgModel<ProductWarehouse> msgModel = new MsgModel<ProductWarehouse> ();
		try{
			if(Constants.PRODUCT_WAREHOUSE_MAP.containsKey(StringUtil.nullToLong(wareHouseId))){
				msgModel.setIsSucc(true);
				msgModel.setData(Constants.PRODUCT_WAREHOUSE_MAP.get(StringUtil.nullToLong(wareHouseId)));
				return msgModel;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		msgModel.setMessage("仓库信息不存在错误");
		return msgModel;
	}
}

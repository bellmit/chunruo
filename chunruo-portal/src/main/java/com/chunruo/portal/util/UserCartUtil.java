package com.chunruo.portal.util;

import java.util.Map;
import java.util.Map.Entry;

import com.chunruo.cache.portal.impl.UserCartListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.UserCart;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;

public class UserCartUtil {
	
	/**
	 * 获取当前用户、店铺下的商品数量
	 * @param userId
	 * @param storeId
	 * @return
	 */
	public static Integer getUserCartProductNumbers(UserInfo userInfo){
		UserCartListByUserIdCacheManager userCartListByUserIdCacheManager = Constants.ctx.getBean(UserCartListByUserIdCacheManager.class);
		Map<String, UserCart> userCartMap = userCartListByUserIdCacheManager.getSession(userInfo.getUserId());
		if(userCartMap == null || userCartMap.size() == 0){
			return 0;
		}
		
		Integer cartProductNumbers = 0;
		for(Entry<String, UserCart> entry : userCartMap.entrySet()){
			UserCart cart = entry.getValue();
			Long productId = StringUtil.nullToLong(cart.getProductId());
			Long productSpecId = StringUtil.nullToLong(cart.getProductSpecId());
			
			// 检查商品是否有效
			MsgModel<Product> msgModel = ProductUtil.getProductByUserLevel(productId, productSpecId, userInfo, false);
			if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
				cartProductNumbers++;
			}
		}
		return cartProductNumbers;
	}
}

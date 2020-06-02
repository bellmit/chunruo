package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chunruo.cache.portal.impl.UserCartListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.UserCart;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.ProductUtil;
import com.chunruo.portal.vo.TagModel;

/**
 * 购物车列表
 * 按店铺storeId关键字
 * @author 
 *
 */
public class CartListTag extends BaseTag {

	public TagModel<List<UserCart>> getData(){
		TagModel<List<UserCart>> tagModel = new TagModel<List<UserCart>> ();
		Map<String,Object> dataMap = new HashMap<String, Object>();
		try{
			final UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if(userInfo == null || userInfo.getUserId() == null){
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登陆");
				return tagModel;
			}

			final UserCartListByUserIdCacheManager userCartListByUserIdCacheManager = Constants.ctx.getBean(UserCartListByUserIdCacheManager.class);

			final Map<String, UserCart> cartIdMap = userCartListByUserIdCacheManager.getSession(userInfo.getUserId());
			if(cartIdMap != null && cartIdMap.size() > 0){
				//  排序
				List<Map.Entry<String, UserCart>> mappingList = new ArrayList<Map.Entry<String, UserCart>> (cartIdMap.entrySet());
				Collections.sort(mappingList, new Comparator<Map.Entry<String, UserCart>>(){
					public int compare(Map.Entry<String, UserCart> obj1, Map.Entry<String, UserCart> obj2){
						UserCart userCart1 = obj1.getValue();
						UserCart userCart2 = obj2.getValue();
						if(userCart1 == null || userCart1.getUpdateTime() == null){
							return -1;
						}else if(userCart2 == null || userCart2.getUpdateTime() == null){
							return -1;
						}
						return (userCart1.getUpdateTime().getTime() < userCart2.getUpdateTime().getTime()) ? 1 : -1;
					}
				}); 

				List<UserCart> userCartList = new ArrayList<UserCart> ();
				for(Map.Entry<String, UserCart> entry : mappingList){
					UserCart userCart = entry.getValue();

					// 检查商品是否有效
					MsgModel<Product> msgModel = ProductUtil.getProductByUserLevel(userCart.getProductId(), userCart.getProductSpecId(), userInfo, false);
					if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
						Product product = msgModel.getData();

						if(!StringUtil.nullToBoolean(product.getIsFreeTax()) && StringUtil.compareObject(product.getProductType(), GoodsType.GOODS_TYPE_CROSS)) {
							Double taxAmount = StringUtil.nullToDoubleFormat(product.getPaymentPrice() * Product.TAXRATE  ); 
							userCart.setTax(taxAmount);
						}
						userCart.setPaymentPrice(product.getPaymentPrice());
						userCart.setProductTags(product.getProductTags());
						userCart.setProductName(product.getName());
						userCart.setProductType(product.getProductType());
						userCart.setImagePath(product.getImage());
						userCart.setProductId(product.getProductId());
						userCart.setIsSoldout(product.getIsPaymentSoldout());
						userCart.setStockNumber(product.getPaymentStockNumber());
						userCart.setIsTaskProduct(StringUtil.nullToBoolean(product.getIsTaskProduct()));
						userCart.setTaskProductTag(StringUtil.null2Str(product.getTaskProductTag()));
						userCart.setIsSeckillProduct(StringUtil.nullToBoolean(product.getIsSeckillProduct()));
						userCartList.add(userCart);
					}
				}
				tagModel.setData(userCartList);
			}
			dataMap.put("level", StringUtil.nullToInteger(userInfo.getLevel()));
			dataMap.put("storeName", StringUtil.nullToString(userInfo.getStoreName()));
			tagModel.setDataMap(dataMap);
		}catch(Exception e){
			e.printStackTrace();
		}

		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}
}

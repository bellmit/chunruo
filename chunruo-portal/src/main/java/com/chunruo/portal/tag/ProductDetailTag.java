package com.chunruo.portal.tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.ProductUtil;
import com.chunruo.portal.util.UserCartUtil;
import com.chunruo.portal.vo.TagModel;

/**
 * 商品详情 根据供货商商品ID查询
 * @author chunruo
 */
public class ProductDetailTag extends BaseTag {

	public TagModel<Product> getData(Object productId_1) {
		Long productId = StringUtil.nullToLong(productId_1);
		TagModel<Product> tagModel = new TagModel<Product>();
		try {

			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if (userInfo == null || userInfo.getUserId() == null) {
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登陆");
				return tagModel;
			}

			MsgModel<Product> msgModel = ProductUtil.getProductByUserLevel(productId, userInfo, false);
			if (!StringUtil.nullToBoolean(msgModel.getIsSucc())) {
				tagModel.setCode(PortalConstants.CODE_ERROR);
				tagModel.setMsg(msgModel.getMessage());
				return tagModel;
			}

			Product product = msgModel.getData();
			Map<String, Object> objectMap = new HashMap<String,Object>();

			Integer cartProductNumbers = UserCartUtil.getUserCartProductNumbers(userInfo);
			product.setPaymentCartNumbers(cartProductNumbers);

			List<Map<String, Object>> imageMapList = ProductUtil.getProductImageList(product);
			if(!CollectionUtils.isEmpty(imageMapList)){
				String minImagePath = StringUtil.null2Str(imageMapList.get(0).get("imageURL"));
				tagModel.setMinImagePath(ImageRateTag.getData(minImagePath, "300", "upload/"));
			}
			tagModel.setMapList(imageMapList);

			tagModel.setDataMap(objectMap);
			if (StringUtil.nullToBoolean(product.getIsPaymentSoldout())) {
				product.setPaymentStockNumber(0);
			}
			tagModel.setData(product);
		} catch (Exception e) {
			e.printStackTrace();
		}

		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}

	
	public static String getCategoryName(List<Long> categoryIdList) {
		try {
			if(categoryIdList != null && !categoryIdList.isEmpty()) {
				StringBuilder categoryName = new StringBuilder();
				for(Long categoryId : categoryIdList) {
					ProductCategory productCategory = Constants.PRODUCT_CATEGORY_MAP.get(categoryId);
					if(productCategory != null && productCategory.getCategoryId() != null) {
						categoryName.append(productCategory.getName());
						categoryName.append(",");
					}
				}
				String fName = categoryName.toString().substring(0, categoryName.toString().lastIndexOf(","));
				return fName;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
} 
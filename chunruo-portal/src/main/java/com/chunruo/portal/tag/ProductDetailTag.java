package com.chunruo.portal.tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;

import com.chunruo.cache.portal.impl.CountryListCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Country;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductBrand;
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

			// 供应商商品信息
			MsgModel<Product> msgModel = ProductUtil.getProductByUserLevel(productId, userInfo, false);
			if (!StringUtil.nullToBoolean(msgModel.getIsSucc())) {
				tagModel.setCode(PortalConstants.CODE_ERROR);
				tagModel.setMsg(msgModel.getMessage());
				return tagModel;
			}

			// 商品详情
			Product product = msgModel.getData();
			Map<String, Object> objectMap = new HashMap<String,Object>();

			// 用户购物车数量
			Integer cartProductNumbers = UserCartUtil.getUserCartProductNumbers(userInfo);
			product.setPaymentCartNumbers(cartProductNumbers);

		// 商品banner图片列表
			List<Map<String, Object>> imageMapList = ProductUtil.getProductImageList(product);
			if(!CollectionUtils.isEmpty(imageMapList)){
				String minImagePath = StringUtil.null2Str(imageMapList.get(0).get("imageURL"));
				tagModel.setMinImagePath(ImageRateTag.getData(minImagePath, "300", "upload/"));
			}
			tagModel.setMapList(imageMapList);

			// 国家名称
			ProductBrand brand= ProductUtil.getProductBrandByBrandId(product.getBrandId());
			if(brand != null && brand.getBrandId() != null) {
				String countryName = StringUtil.null2Str(ProductDetailTag.getCountryName(StringUtil.nullToLong(brand.getCountryId())));
				brand.setCountryName(countryName);
			}
			product.setBarrageVoList(ProductUtil.getProductInfoList(product.getProductType(),brand));

			//用户等级
			objectMap.put("level", StringUtil.nullToString(userInfo.getLevel()));

			//分类名称
			product.setCategoryFidName(StringUtil.null2Str(getCategoryName(product.getCategoryFidList())));
			product.setCategoryIdName(StringUtil.null2Str(getCategoryName(product.getCategoryIdList())));
			

			tagModel.setDataMap(objectMap);
			// 在上架商品中，供货商取消分销后，商品显示已售罄
			if (StringUtil.nullToBoolean(product.getIsPaymentSoldout())) {
				// 判断商品状态不是上架状态,购物车统一任务是售罄状态
				product.setPaymentStockNumber(0);
			}
			tagModel.setData(product);
		} catch (Exception e) {
			e.printStackTrace();
		}

		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}

	/**
	 * 商品生产地址
	 * @param countryListCacheManager
	 * @param countryId
	 * @return
	 */
	public static String getCountryName(Long countryId) {
		String countryName = null;
		try{
			if (countryId != null) {
				CountryListCacheManager countryListCacheManager = Constants.ctx.getBean(CountryListCacheManager.class);
				Country country = countryListCacheManager.getSession(countryId);
				if(country != null && country.getCountryId() != null){
					countryName = StringUtil.null2Str(country.getCountryName());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return StringUtil.null2Str(countryName);
	}



	
	/**
	 * 获取商品分类名称
	 * @param categoryIdList
	 * @return
	 */
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
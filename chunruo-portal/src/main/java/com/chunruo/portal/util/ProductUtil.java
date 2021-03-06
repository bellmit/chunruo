package com.chunruo.portal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.chunruo.cache.portal.impl.OrderListByUserIdCacheManager;
import com.chunruo.cache.portal.impl.ProductByIdCacheManager;
import com.chunruo.cache.portal.impl.ProductCategoryAllListCacheManager;
import com.chunruo.cache.portal.impl.ProductImageListByIdCacheManger;
import com.chunruo.cache.portal.impl.TagModelListCacheManager;
import com.chunruo.cache.portal.impl.UserCouponListByUserIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.model.Coupon;
import com.chunruo.core.model.Keywords;
import com.chunruo.core.model.Order;
import com.chunruo.core.model.OrderItems;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductBrand;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.ProductGroup;
import com.chunruo.core.model.ProductImage;
import com.chunruo.core.model.ProductQuestion;
import com.chunruo.core.model.ProductSeckill;
import com.chunruo.core.model.ProductShareRecord;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.model.RechargeTemplate;
import com.chunruo.core.model.TagModel;
import com.chunruo.core.model.UserCoupon;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.service.KeywordsManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.IKUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.vo.BarrageVo;
import com.chunruo.portal.vo.ProductVerifyVo;

public class ProductUtil {
	protected final static transient Log log = LogFactory.getLog(ProductUtil.class);

	/**
	 * ??????????????????????????????
	 * @param priceWholesale
	 * @param priceRecommend
	 * @param userInfo
	 * @return
	 */
	public static MsgModel<Double> getPaymentPriceByUserInfo(Product product,Double priceWholesale, Double priceRecommend, UserInfo userInfo,Boolean isFromProductGroup){
		MsgModel<Double> msgModel = new MsgModel<Double> ();
		try{
			// ??????????????????
			List<Integer> userLevelList = new ArrayList<Integer> ();
			userLevelList.add(UserLevel.USER_LEVEL_COMMON);	//????????????
			userLevelList.add(UserLevel.USER_LEVEL_BUYERS);	//VIP??????
			userLevelList.add(UserLevel.USER_LEVEL_DEALER);	//?????????
			userLevelList.add(UserLevel.USER_LEVEL_AGENT);	//????????????
			userLevelList.add(UserLevel.USER_LEVEL_V2);	    //v2
			userLevelList.add(UserLevel.USER_LEVEL_V3);	    //v3

			// ??????????????????
			List<Integer> userLevelWholesale = new ArrayList<Integer> ();
			userLevelWholesale.add(UserLevel.USER_LEVEL_DEALER);	// ?????????
			userLevelWholesale.add(UserLevel.USER_LEVEL_AGENT);		// ????????????
			userLevelWholesale.add(UserLevel.USER_LEVEL_V2);		// v2
			userLevelWholesale.add(UserLevel.USER_LEVEL_V3);		// v3

			// ??????????????????
			Integer userLevel = UserLevel.USER_LEVEL_COMMON;
			if(StringUtil.nullToBoolean(userInfo.getIsAgent()) && userLevelList.contains(StringUtil.nullToInteger(userInfo.getLevel()))){
				userLevel = StringUtil.nullToInteger(userInfo.getLevel());
			}

			boolean isShareProduct = false;  //??????????????????
			//??????????????????
			ProductShareRecord productShareRecord = userInfo.getProductShareRecord();
			if(productShareRecord != null 
					&& productShareRecord.getRecordId() != null
					&& StringUtil.nullToBoolean(userInfo.getIsShareUser())) {
				isShareProduct = true;
			}

			// ?????????????????????????????????????????????
			Double paymentPrice = StringUtil.nullToDoubleFormat(priceRecommend);
			if(StringUtil.nullToBoolean(isShareProduct)
					&& StringUtil.nullToBoolean(isFromProductGroup)
					&& !StringUtil.nullToBoolean(product.getIsFreeTax())
					&& Constants.PRODUCT_TYPE_CROSS_LIST.contains(product.getProductType())) {
				//????????????????????????
				MsgModel<Double> taxModel = ProductUtil.getProductTax(paymentPrice, product.getProductType(), product.getIsFreeTax());
				if(StringUtil.nullToBoolean(taxModel.getIsSucc())) {
					paymentPrice = DoubleUtil.add(paymentPrice, StringUtil.nullToDouble(taxModel.getData()));
				}
			}else if(userLevelWholesale.contains(userLevel) && !StringUtil.nullToBoolean(isShareProduct)){
				// ????????????????????????????????????????????????
				paymentPrice = StringUtil.nullToDoubleFormat(priceWholesale);
			}

			msgModel.setIsSucc(true);
			msgModel.setData(paymentPrice);
			return msgModel;
		}catch(Exception e){
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("????????????????????????");
		return msgModel;
	}

	/**
	 * ??????????????????????????????
	 * @param productSeckill
	 * @return
	 */
	public static MsgModel<Integer> checkSeckillSeason(ProductSeckill productSeckill){
		MsgModel<Integer> msgModel = new MsgModel<Integer> ();
		try{
			// ??????????????????
			if(productSeckill != null 
					&& productSeckill.getSeckillId() != null
					&& DateUtil.isEffectiveTime(DateUtil.DATE_HOUR, productSeckill.getStartTime())
					&& DateUtil.isEffectiveTime(DateUtil.DATE_HOUR, productSeckill.getEndTime())){
				// ??????23:50:00????????????
				String strLastDate = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, DateUtil.getDateAfterByDay(DateUtil.getCurrentDate(), 1));
				Date lastDate = DateUtil.parseDate(DateUtil.DATE_FORMAT_HOUR, String.format("%s 23:50:00", strLastDate));

				// ????????????????????????????????????????????????????????????
				Date seckillStartDate = DateUtil.parseDate(DateUtil.DATE_FORMAT_HOUR, productSeckill.getStartTime());
				Date seckillEndDate = DateUtil.parseDate(DateUtil.DATE_FORMAT_HOUR, productSeckill.getEndTime());
				if(seckillStartDate != null && seckillEndDate != null){
					Long seckillStartTime = seckillStartDate.getTime();
					Long seckillEndTime = seckillEndDate.getTime();
					Long currentTimeMillis = System.currentTimeMillis();
					if(currentTimeMillis <= seckillEndTime && seckillStartTime <= lastDate.getTime()){
						//?????????????????????????????????
						Integer seckillType = ProductSeckill.SECKILL_TYPE_READY;
						if(seckillStartTime < currentTimeMillis){
							//?????????????????????
							seckillType = ProductSeckill.SECKILL_TYPE_START;
						}

						msgModel.setData(seckillType);
						msgModel.setIsSucc(true);
						return msgModel;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		return msgModel;
	}


	/**
	 * ??????????????????
	 * @param product
	 * @return
	 */
	public static MsgModel<Integer> checkGroupProduct(Product product){
		MsgModel<Integer> msgModel = new MsgModel<Integer> ();
		try{
			// ??????????????????????????????????????????
			Map<Long, List<ProductGroup>> productGroupListMap = product.getProductGroupListMap();
			if(productGroupListMap == null || productGroupListMap.size() <= 0){
				msgModel.setIsSucc(false);
				msgModel.setMessage(String.format("\"%s\"?????????????????????????????????", product.getName()));
				return msgModel;
			}

			// ???????????????
			List<Integer> paymentStockNumberList = new ArrayList<Integer>();
			// ????????????id
			Set<Long> wareHouseIdSet = new HashSet<Long>();

			// ????????????????????????
			for(Entry<Long, List<ProductGroup>> entry : productGroupListMap.entrySet()){
				Long productId = entry.getKey();
				List<ProductGroup> productGroupList = entry.getValue();
				if(productGroupList == null || productGroupList.size() <= 0){
					// ?????????????????????(??????????????????)
					product.setIsSoldout(false);
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"??????????????????", product.getName()));
					return msgModel;
				}

				// ??????????????????????????????
				MsgModel<Product> xmsgModel = ProductUtil.getProductByProductId(productId, true);
				if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
					// ?????????????????????(??????????????????)
					product.setIsSoldout(false);
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"??????????????????", product.getName()));
					return msgModel;
				}

				// ?????????????????????????????????
				Product xproduct = xmsgModel.getData();
				if(StringUtil.nullToBoolean(xproduct.getIsSeckillProduct())){
					// ?????????????????????????????????????????????
					product.setIsSoldout(false);
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"????????????????????????", xproduct.getName()));
					return msgModel;
				}

				wareHouseIdSet.add(xproduct.getWareHouseId());
				boolean isPaymentSoldout = true;
				if(StringUtil.nullToBoolean(xproduct.getIsSpceProduct())){
					// ????????????
					Map<Long, ProductSpec> productSpecMap = new HashMap<Long, ProductSpec> ();
					for(ProductSpec productSpec : xproduct.getProductSpecList()){
						productSpecMap.put(productSpec.getProductSpecId(), productSpec);
					}

					for(ProductGroup productGroup : productGroupList){
						boolean isConfigureError = false;
						Double priceCost = StringUtil.nullToDoubleFormat(productGroup.getGroupPriceCost());
						Double priceWholesale = StringUtil.nullToDoubleFormat(productGroup.getGroupPriceWholesale());
						Double priceRecommend = StringUtil.nullToDoubleFormat(productGroup.getGroupPriceRecommend());
						if(priceCost == null  
								|| priceWholesale == null 
								|| priceRecommend == null){
							// ??????????????????????????????
							isConfigureError = true;
						}else if(priceCost.compareTo(new Double(0.001)) <= 0){
							// ??????????????????????????????0.001
							isConfigureError = true;
						}else if(priceWholesale.compareTo(priceCost) < 0){
							// ??????????????????????????????????????????
							isConfigureError = true;
						}else if(priceRecommend.compareTo(priceWholesale) <= 0){
							// ????????????????????????????????????????????????
							isConfigureError = true;
						}

						// ??????????????????????????????
						if(isConfigureError){
							String message = String.format("\"%s\"?????????????????????", StringUtil.null2Str(xproduct.getName()));
							msgModel.setIsSucc(false);
							msgModel.setMessage(message);
							return msgModel;
						}

						// ????????????0??????
						productGroup.setPaymentStockNumber(0);
						productGroup.setIsPaymentSoldout(true);

						// ??????????????????????????????
						if(productSpecMap.containsKey(productGroup.getProductSpecId())){
							ProductSpec productSpec = productSpecMap.get(productGroup.getProductSpecId());
							Integer paymentStockNumber = productSpec.getPaymentStockNumber() / productGroup.getSaleTimes();
							productGroup.setPaymentStockNumber(paymentStockNumber);
						}

						// ???????????????????????????0?????????????????????
						if(StringUtil.nullToInteger(productGroup.getPaymentStockNumber()) > 0){
							isPaymentSoldout = false;
							productGroup.setIsPaymentSoldout(false);
							paymentStockNumberList.add(productGroup.getPaymentStockNumber());
						}
					}
				}else{
					// ????????????
					for(ProductGroup productGroup : productGroupList){
						boolean isConfigureError = false;
						Double priceCost = StringUtil.nullToDoubleFormat(productGroup.getGroupPriceCost());
						Double priceWholesale = StringUtil.nullToDoubleFormat(productGroup.getGroupPriceWholesale());
						Double priceRecommend = StringUtil.nullToDoubleFormat(productGroup.getGroupPriceRecommend());
						if(priceCost == null  
								|| priceWholesale == null 
								|| priceRecommend == null){
							// ??????????????????????????????
							isConfigureError = true;
						}else if(priceCost.compareTo(new Double(0.001)) <= 0){
							// ??????????????????????????????0.001
							isConfigureError = true;
						}else if(priceWholesale.compareTo(priceCost) < 0){
							// ??????????????????????????????????????????
							isConfigureError = true;
						}else if(priceRecommend.compareTo(priceWholesale) <= 0){
							// ????????????????????????????????????????????????
							isConfigureError = true;
						}

						// ??????????????????????????????
						if(isConfigureError){
							String message = String.format("\"%s\"?????????????????????", StringUtil.null2Str(xproduct.getName()));
							msgModel.setIsSucc(false);
							msgModel.setMessage(message);
							return msgModel;
						}

						// ????????????0??????
						productGroup.setPaymentStockNumber(0);
						productGroup.setIsPaymentSoldout(true);

						Integer paymentStockNumber = xproduct.getPaymentStockNumber() / productGroup.getSaleTimes();
						productGroup.setPaymentStockNumber(paymentStockNumber);

						// ???????????????????????????0?????????????????????
						if(StringUtil.nullToInteger(productGroup.getPaymentStockNumber()) > 0){
							isPaymentSoldout = false;
							productGroup.setIsPaymentSoldout(false);
							paymentStockNumberList.add(productGroup.getPaymentStockNumber());
						}
					}
				}

				// ????????????????????????????????????????????????
				if (!StringUtil.compareObject(wareHouseIdSet.size(), 1)) {
					isPaymentSoldout = true;

					// ??????????????????????????????
					product.setIsSoldout(false);
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"??????????????????????????????", product.getName()));
					return msgModel;
				}

				// ????????????????????????????????????
				if(StringUtil.nullToBoolean(isPaymentSoldout)){
					product.setIsSoldout(false);
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"???????????????????????????", product.getName()));
					return msgModel;
				}
			}

			// ?????????????????????????????????????????????
			Collections.sort(paymentStockNumberList);
			product.setPaymentStockNumber(paymentStockNumberList.get(0));
			msgModel.setIsSucc(true);
			return msgModel;
		}catch(Exception e){
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage(String.format("\"%s\"??????????????????", product.getName()));
		return msgModel;
	}


	/**
	 * ???????????????????????????
	 * @param productId
	 * @return
	 */
	public static boolean isExistBuyProduct(Long productId, Long userId){
		try{
			//???????????????????????????
			OrderListByUserIdCacheManager orderListByUserIdCacheManager = Constants.ctx.getBean(OrderListByUserIdCacheManager.class);
			List<Order> orderList = orderListByUserIdCacheManager.getSession(userId);
			if (orderList != null && orderList.size() > 0){
				// ??????????????????????????????
				for(Order order : orderList){
					if(order != null
							&& StringUtil.nullToBoolean(order.getIsPaymentSucc())
							&& order.getOrderItemsList() != null
							&& order.getOrderItemsList().size() > 0){
						for(OrderItems orderItems : order.getOrderItemsList()){
							if(StringUtil.compareObject(productId, orderItems.getProductId())){
								return true;
							}
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ????????????
	 * ????????????
	 * @param productType
	 * @param countryName
	 * @return
	 */
	public static List<BarrageVo> getProductInfoList(Integer productType,ProductBrand brand) {
		List<BarrageVo> barrageVoList = new ArrayList<BarrageVo>();
		//????????????(1:??????;2:??????;3:??????)
		if(StringUtil.compareObject(GoodsType.GOODS_TYPE_CROSS, productType)){
			barrageVoList.add(new BarrageVo(StringUtil.null2Str(Constants.conf.getProperty("product.type.cross.default.image")),"????????????"));
			barrageVoList.add(new BarrageVo(StringUtil.null2Str(Constants.conf.getProperty("product.type.cross.plane.image")),"???????????????"));
		}else if(StringUtil.compareObject(GoodsType.GOODS_TYPE_DIRECT, productType)
				|| StringUtil.compareObject(GoodsType.GOODS_TYPE_DIRECT_GO, productType)){
			barrageVoList.add(new BarrageVo(StringUtil.null2Str(Constants.conf.getProperty("product.type.cross.default.image")),"????????????"));
			barrageVoList.add(new BarrageVo(StringUtil.null2Str(Constants.conf.getProperty("product.type.cross.plane.image")),"????????????"));
		}

		if(brand != null && brand.getBrandId() != null) {
			if(!StringUtil.isNull(StringUtil.null2Str(brand.getCountryName()))) {
				barrageVoList.add(new BarrageVo(StringUtil.null2Str(brand.getCountryImage()),StringUtil.null2Str(brand.getCountryName())));
			}else {
				barrageVoList.add(new BarrageVo(StringUtil.null2Str(brand.getCountryImage()),"????????????"));
			}
		}

		return barrageVoList;
	}

	/**
	 * ??????????????????????????????
	 * @param warehouseId
	 * @return
	 */
	public static Integer getProductType(Long warehouseId){
		try{
			ProductWarehouse productWarehouse = ProductUtil.getProductWarehouse(warehouseId);
			if(productWarehouse != null && productWarehouse.getWarehouseId() != null){
				return StringUtil.nullToInteger(productWarehouse.getProductType());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ??????????????????????????????
	 * @param warehouseId
	 * @return
	 */
	public static ProductWarehouse getProductWarehouse(Long warehouseId){
		try{
			if(warehouseId != null
					&& Constants.PRODUCT_WAREHOUSE_MAP != null
					&& Constants.PRODUCT_WAREHOUSE_MAP.size() > 0
					&& Constants.PRODUCT_WAREHOUSE_MAP.containsKey(warehouseId)){
				return Constants.PRODUCT_WAREHOUSE_MAP.get(warehouseId);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ????????????????????????????????????
	 * @param warehouseId
	 * @return
	 */
	public static Long getProductBrand(Long brandId){
		try{
			ProductBrand productBrand = ProductUtil.getProductBrandByBrandId(brandId);
			if(productBrand != null && productBrand.getBrandId() != null) {
				return StringUtil.nullToLong(productBrand.getCountryId());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ????????????????????????????????????
	 * @param warehouseId
	 * @return
	 */
	public static ProductBrand getProductBrandByBrandId(Long brandId){
		try{
			if(brandId != null
					&& Constants.PRODUCT_BRAND_MAP != null
					&& Constants.PRODUCT_BRAND_MAP.size() > 0
					&& Constants.PRODUCT_BRAND_MAP.containsKey(brandId)){
				return Constants.PRODUCT_BRAND_MAP.get(brandId);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ??????????????????????????????
	 * @param wholesale
	 * @param productSpecId
	 * @return
	 */
	public static MsgModel<ProductSpec> checkExistProductSpecByProductSpecId(Product product, Long productSpecId){
		MsgModel<ProductSpec> msgModel = new MsgModel<ProductSpec> ();
		try{
			if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
				Map<Long, ProductSpec> productSpceMap = new HashMap<Long, ProductSpec> ();
				if(product.getProductSpecList() != null && product.getProductSpecList().size() > 0){
					for(ProductSpec productSpec : product.getProductSpecList()){
						productSpceMap.put(productSpec.getProductSpecId(), productSpec);
					}
				}

				// ????????????????????????????????????
				if(productSpceMap.containsKey(productSpecId)){
					msgModel.setIsSucc(true);
					msgModel.setData(productSpceMap.get(productSpecId));
					return msgModel;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage(String.format("\"%s\"?????????????????????????????????", product.getName()));
		return msgModel;
	}

	/**
	 * ?????????????????????????????????
	 * ?????????????????????
	 * @param productId
	 * @param productSpecId
	 * @param userInfo
	 * @param isMustSell
	 * @return
	 */
	public static MsgModel<Product> getProductByUserLevel(Long productId, UserInfo userInfo, Boolean isMustSell){
		MsgModel<Product> msgModel = new MsgModel<Product>();
		try {
			return ProductUtil.getProductByUserLevel(productId, null, userInfo, isMustSell, false);
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("????????????????????????");
		return msgModel;
	}

	/**
	 * ?????????????????????????????????
	 * ????????????????????????
	 * @param productId
	 * @param productSpecId
	 * @param userInfo
	 * @param isMustSell
	 * @return
	 */
	public static MsgModel<Product> getProductByUserLevel(Long productId, Long productSpecId, UserInfo userInfo, Boolean isMustSell){
		MsgModel<Product> msgModel = new MsgModel<Product>();
		try {
			return ProductUtil.getProductByUserLevel(productId, productSpecId, userInfo, isMustSell, true);
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("??????????????????????????????");
		return msgModel; 
	}


	/**
	 * ????????????????????????
	 * @param productId
	 * @param productSpecId
	 * @param userInfo
	 * @param isMustSell
	 * @param isCheckSpceProduct
	 * @return
	 */
	public static MsgModel<Product> getProductByUserLevel(Long productId, Long productSpecId, UserInfo userInfo, Boolean isMustSell, Boolean isCheckSpceProduct){
		MsgModel<Product> msgModel = new MsgModel<Product>();

		try {
			//????????????
			MsgModel<Product> xmsgModel = ProductUtil.getProductByUserLevel(productId, productSpecId, userInfo, isMustSell, isCheckSpceProduct, false);
			if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())) {
				msgModel.setIsSucc(false);
				msgModel.setMessage(xmsgModel.getMessage());
				return msgModel;
			}
			return xmsgModel;
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage("???????????????");
		return msgModel;
	}

	/**
	 * ?????????????????????????????????
	 * @param productId
	 * @param productSpecId
	 * @param userInfo
	 * @param isMustSell
	 * @param isCheckSpceProduct
	 * @return
	 */
	public static MsgModel<Product> getProductByUserLevel(Long productId, Long productSpecId, UserInfo userInfo, Boolean isMustSell, Boolean isCheckSpceProduct,Boolean isShareProduct){
		MsgModel<Product> msgModel = new MsgModel<Product> ();
		try{
			MsgModel<Product> xsgModel = ProductUtil.getProductByProductId(productId, isMustSell);
			if(!StringUtil.nullToBoolean(xsgModel.getIsSucc())){
				msgModel.setIsSucc(false);
				msgModel.setMessage(xsgModel.getMessage());
				return msgModel;
			}

			Product product = xsgModel.getData();

			List<Integer> userLevelList = new ArrayList<Integer> ();
			userLevelList.add(UserLevel.USER_LEVEL_BUYERS);	
			userLevelList.add(UserLevel.USER_LEVEL_DEALER);	
			
			List<Integer> userLevelWholesale = new ArrayList<Integer> ();
			userLevelWholesale.add(UserLevel.USER_LEVEL_DEALER);	

			Integer userLevel = UserLevel.USER_LEVEL_COMMON;
			if(userLevelList.contains(StringUtil.nullToInteger(userInfo.getLevel()))){
				userLevel = StringUtil.nullToInteger(userInfo.getLevel());
			}

			userInfo.setPaymentUserLevel(userLevel);

			String imagePath = StringUtil.null2Str(product.getImage());
			Double weigth = StringUtil.nullToDouble(product.getWeigth());
			Long paymentTemplateId = StringUtil.nullToLong(product.getTemplateId());
			Integer paymentStockNumber = StringUtil.nullToInteger(product.getPaymentStockNumber());
			Boolean isPaymentSoldout = StringUtil.nullToBoolean(product.getIsPaymentSoldout());
			Boolean isPaymentSeckillLimit = StringUtil.nullToBoolean(product.getIsSeckillLimit());
			Integer paymentSeckillBuyNum = StringUtil.nullToInteger(product.getSeckillLimitNumber());
			Double priceCost = StringUtil.nullToDoubleFormat(product.getPriceCost());
			Double priceRecommend = StringUtil.nullToDoubleFormat(product.getPriceRecommend());
			Double priceWholesale = StringUtil.nullToDoubleFormat(product.getPriceRecommend());
			Double seckillPrice = StringUtil.nullToDoubleFormat(product.getSeckillPrice());
			Double seckillProfit = StringUtil.nullToDoubleFormat(product.getSeckillProfit());
			Double v2Price = StringUtil.nullToDoubleFormat(product.getV2Price());
			Double v3Price = StringUtil.nullToDoubleFormat(product.getV3Price());
			Double realSellPrice = StringUtil.nullToDoubleFormat(product.getRealSellPrice());

			if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
				if(product.getProductSpecList() != null && product.getProductSpecList().size() > 0){
					for(ProductSpec productSpec : product.getProductSpecList()){
						Double paymentPrice = StringUtil.nullToDoubleFormat(productSpec.getPriceRecommend());
						if(!StringUtil.nullToBoolean(isShareProduct)) {
							if(StringUtil.nullToBoolean(product.getIsSeckillProduct()) && !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())){
								paymentPrice = StringUtil.nullToDoubleFormat(productSpec.getSeckillPrice()); 
							}else if(userLevelWholesale.contains(userLevel)){
								paymentPrice = StringUtil.nullToDoubleFormat(productSpec.getRealSellPrice());
							}
						}
						productSpec.setPaymentPrice(paymentPrice);
					}
				}

				if(isCheckSpceProduct){
					MsgModel<ProductSpec> xmsgModel = ProductUtil.checkExistProductSpecByProductSpecId(product, productSpecId);
					if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
						msgModel.setIsSucc(false);
						msgModel.setMessage(xmsgModel.getMessage());
						return msgModel;
					}

					ProductSpec productSpec = xmsgModel.getData();
					if(isMustSell && StringUtil.nullToBoolean(productSpec.getIsPaymentSoldout())){
						msgModel.setIsSucc(false);
						msgModel.setMessage(String.format("\"%s,%s\"???????????????", product.getName(), StringUtil.null2Str(productSpec.getProductTags())));
						return msgModel;
					}
					weigth = StringUtil.nullToDouble(productSpec.getWeigth());
					imagePath = StringUtil.null2Str(productSpec.getSpecImagePath());
					paymentStockNumber = StringUtil.nullToInteger(productSpec.getPaymentStockNumber());
					isPaymentSoldout = StringUtil.nullToBoolean(productSpec.getIsPaymentSoldout());
					isPaymentSeckillLimit = StringUtil.nullToBoolean(productSpec.getIsSeckillLimit());
					paymentSeckillBuyNum = StringUtil.nullToInteger(productSpec.getSeckillLimitNumber());
					priceCost = StringUtil.nullToDoubleFormat(productSpec.getPriceCost());
					priceRecommend = StringUtil.nullToDoubleFormat(productSpec.getPriceRecommend());
					priceWholesale = StringUtil.nullToDoubleFormat(productSpec.getPriceWholesale());
					seckillPrice = StringUtil.nullToDoubleFormat(productSpec.getSeckillPrice());
					seckillProfit = StringUtil.nullToDoubleFormat(productSpec.getSeckillProfit());
					v2Price = StringUtil.nullToDoubleFormat(productSpec.getV2Price());
					v3Price = StringUtil.nullToDoubleFormat(productSpec.getV3Price());
					realSellPrice = StringUtil.nullToDoubleFormat(productSpec.getRealSellPrice());
					product.setCurrentProductSpec(productSpec);
					product.setProductTags(StringUtil.null2Str(productSpec.getProductTags()));
				}
			}

			Double paymentPrice = StringUtil.nullToDoubleFormat(priceRecommend);
			product.setPaymentPriceRegion(StringUtil.null2Str(product.getSellPriceRegion()));   
			product.setPaymentGroupPriceRegion(StringUtil.null2Str(product.getGroupPriceRecommend()));
			product.setPaymentTaxRegion(StringUtil.null2Str(product.getGroupTaxRecommend()));
			if(!StringUtil.nullToBoolean(isShareProduct)) {
				product.setPaymentPriceRegion(StringUtil.null2Str(product.getGroupPriceRecommend()));
				//???????????????
				if(userLevelWholesale.contains(userLevel)){
					// ????????????????????????
					paymentPrice = StringUtil.nullToDoubleFormat(priceCost);
					product.setPaymentPriceRegion(StringUtil.null2Str(product.getGroupPriceWholesale()));
					product.setPaymentGroupPriceRegion(StringUtil.null2Str(product.getGroupPriceWholesale()));
					product.setPaymentTaxRegion(StringUtil.null2Str(product.getGroupTaxWholesale()));
				}
			}

			product.setImage(imagePath);
			product.setPriceCost(priceCost);
			product.setPriceRecommend(priceRecommend);
			product.setV2Price(v2Price);
			product.setV3Price(v3Price);
			product.setRealSellPrice(realSellPrice);
			product.setPaymentPrice(paymentPrice);
			product.setPaymentOriginalPrice(product.getPaymentPrice());
			product.setSeckillPrice(seckillPrice);
			product.setSeckillProfit(seckillProfit);
			product.setPaymentWeigth(weigth);
			product.setPaymentStockNumber(paymentStockNumber);
			product.setPaymentTemplateId(paymentTemplateId);
			product.setIsPaymentSoldout(isPaymentSoldout);
			product.setIsPaymentSeckillLimit(isPaymentSeckillLimit);
			product.setPaymentSeckillBuyNum(paymentSeckillBuyNum);
			product.setTax(0.0D);

			msgModel.setIsSucc(true);
			msgModel.setData(product);
			return msgModel;

		}catch(Exception e){
			e.printStackTrace();
		}

		// ???????????????????????????
		msgModel.setIsSucc(false);
		msgModel.setMessage("???????????????????????????");
		return msgModel;
	}

	/**
	 * ???????????????????????????????????????
	 * @param productId
	 * @return
	 */
	public static Product getProduct(Long productId){
		try{
			ProductByIdCacheManager productByIdCacheManager = Constants.ctx.getBean(ProductByIdCacheManager.class);
			Product product = productByIdCacheManager.getSession(productId);
			if(product != null){
				// ??????????????????
				return product.clone();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ????????????????????????
	 * ????????????????????????????????????
	 * @param productId
	 * @param isMustSell(????????????????????????)
	 * @return
	 */
	public static MsgModel<Product> getProductByProductId(Long productId, Boolean isMustSell){
		MsgModel<Product> msgModel = new MsgModel<Product> ();
		try{
			ProductManager productManager = Constants.ctx.getBean(ProductManager.class);
			ProductByIdCacheManager productByIdCacheManager = Constants.ctx.getBean(ProductByIdCacheManager.class);

			Product product = ProductUtil.getProduct(productId);
			if(product != null && product.getProductId() != null){
				boolean isPaymentSoldout = false;

				// ????????????????????????
				MsgModel<Integer> xsgModel = ProductUtil.checkProductConfigure(product);
				if(!StringUtil.nullToBoolean(xsgModel.getIsSucc())){
					// ?????????
					isPaymentSoldout = true;
					if(isMustSell
							&& !StringUtil.nullToBoolean(product.getIsSoldout())
							&& !StringUtil.nullToBoolean(product.getIsSeckillProduct())){
						// ???????????????????????????????????????
						productManager.updateProductSoldoutStatus(productId, true);
						try{
							productByIdCacheManager.removeSession(productId);
						}catch(Exception e){
							e.printStackTrace();
						}

						// ?????????????????????
						String message = String.format("\"%s\"???????????????", StringUtil.null2Str(product.getName()));
						msgModel.setIsSucc(false);
						msgModel.setMessage(message);
						return msgModel;
					}

					// ????????????????????????????????????(????????????)
					if(!StringUtil.nullToBoolean(isMustSell)
							&& StringUtil.nullToBoolean(product.getIsSeckillProduct()) 
							&& StringUtil.nullToBoolean(product.getIsSeckillReadStatus())){
						product.setIsSeckillProduct(false);
						product.setIsSeckillReadStatus(false);
					}
				}

				// ????????????????????????
				if(StringUtil.nullToBoolean(product.getIsSoldout())
						|| !StringUtil.nullToBoolean(product.getStatus())
						|| StringUtil.nullToBoolean(product.getIsDelete())){
					// ?????????
					isPaymentSoldout = true;
				}

				// ???????????????????????????
				if(isPaymentSoldout){
					product.setStockNumber(0);
					product.setPaymentStockNumber(0);

					if(isMustSell){
						// ?????????????????????
						String message = String.format("\"%s\"???????????????", StringUtil.null2Str(product.getName()));
						msgModel.setIsSucc(false);
						msgModel.setMessage(message);
						return msgModel;
					}
				}

				product.setIsPaymentSoldout(isPaymentSoldout);
				msgModel.setIsSucc(true);
				msgModel.setData(product);
				return msgModel;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		// ???????????????????????????
		msgModel.setIsSucc(false);
		msgModel.setMessage("???????????????????????????");
		return msgModel;
	}

	/**
	 * ??????????????????????????????????????????
	 * @param wholesale
	 * @param priceCost
	 * @param priceWholesale
	 * @param priceRecommend
	 * @return
	 */
	public static MsgModel<Integer> checkProductConfigure(Product product){
		MsgModel<Integer> msgModel = new MsgModel<Integer> ();
		try{
			if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
				if(product.getProductSpecList() == null || product.getProductSpecList().size() <= 0){
					String message = String.format("\"%s\"????????????????????????", StringUtil.null2Str(product.getName()));
					msgModel.setIsSucc(false);
					msgModel.setMessage(message);
					return msgModel;
				}

				//??????????????????
				boolean isPaymentSoldout = true;
				if(product.getProductSpecList() != null && product.getProductSpecList().size() > 0){
					int paymentStockNumber = 0;
					int salesNumber = 0;
					int paymentSeckillTotalStock = 0;
					List<ProductSpec> productSpecList = new ArrayList<ProductSpec> ();
					Set<Double> priceWholesaleSet = new HashSet<Double>();
					for(ProductSpec productSpec : product.getProductSpecList()){
						// ????????????????????????????????????
						ProductVerifyVo productVerify = new ProductVerifyVo ();
						productVerify.setProductSpecId(productSpec.getProductSpecId());
						productVerify.setPriceCost(productSpec.getPriceCost());
						productVerify.setPriceWholesale(productSpec.getPriceWholesale());
						productVerify.setPriceRecommend(productSpec.getPriceRecommend());
						productVerify.setV2Price(productSpec.getV2Price());
						productVerify.setV3Price(productSpec.getV3Price());
						productVerify.setStockNumber(StringUtil.nullToInteger(productSpec.getStockNumber()));
						productVerify.setSeckillPrice(productSpec.getSeckillPrice());
						productVerify.setSeckillProfit(productSpec.getSeckillProfit());
						productVerify.setSeckillTotalStock(productSpec.getSeckillTotalStock());
						productVerify.setSeckillSalesNumber(productSpec.getSeckillSalesNumber());
						productVerify.setSeckillLockNumber(productSpec.getSeckillLockNumber());

						//??????????????????????????????????????????
						MsgModel<Integer> cmsgModel = ProductUtil.checkSingleProductConfigure(product, productVerify);
						if(!StringUtil.nullToBoolean(product.getIsSoldout()) && StringUtil.nullToBoolean(cmsgModel.getIsSucc())){
							isPaymentSoldout = false;

							// ????????????
							int surplusStock = StringUtil.nullToInteger(cmsgModel.getData());
							paymentStockNumber += surplusStock;
							productSpec.setPaymentStockNumber(surplusStock);
							productSpec.setIsPaymentSoldout(false);
						}else{
							// ??????????????????
							productSpec.setPaymentStockNumber(0);
							productSpec.setIsPaymentSoldout(true);
						}

						//???????????????
						salesNumber += StringUtil.nullToInteger(productSpec.getSalesNumber());

						// ?????????????????????
						if(StringUtil.nullToInteger(productSpec.getSeckillTotalStock()) > 0){
							paymentSeckillTotalStock += productSpec.getSeckillTotalStock();
						}

						// ?????????????????????
						if(StringUtil.nullToBoolean(product.getIsTeamPackage())){
							productSpec.setPriceRecommend(productSpec.getPriceWholesale());
						}

						//??????????????????????????????
						Integer profit = StringUtil.nullToDoubleFormat(productSpec.getPriceRecommend() - productSpec.getPriceWholesale()).intValue();
						// ??????????????????
						if(StringUtil.nullToBoolean(product.getIsSeckillProduct())
								&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())){
							if(StringUtil.nullToInteger(productSpec.getSeckillLimitNumber()) > 0) {
								// ????????????????????????????????????
								productSpec.setIsSeckillLimit(true);
							}
							
							//??????????????????????????????
							profit = StringUtil.nullToDoubleFormat(productSpec.getPriceRecommend() - productSpec.getSeckillPrice()).intValue();
							priceWholesaleSet.add(productSpec.getSeckillPrice());
						}else {
							priceWholesaleSet.add(productSpec.getPriceWholesale());
						}
						productSpec.setRealSellPrice(productSpec.getPriceWholesale());
						productSpec.setProductProfit(profit);
						productSpecList.add(productSpec);
					}

					// ?????????????????????
					product.setSalesNumber(salesNumber);
					product.setPaymentStockNumber(paymentStockNumber);
					product.setPaymentSeckillTotalStock(paymentSeckillTotalStock);

					// ?????????????????????????????????
					if(productSpecList != null && productSpecList.size() > 0){
						Collections.sort(productSpecList, new Comparator<ProductSpec>(){
							@Override
							public int compare(ProductSpec o1, ProductSpec o2) {
								if(StringUtil.nullToInteger(priceWholesaleSet.size()) == 1) {
									//???????????????????????????????????????(?????????????????????????????????)
									Double object1 = StringUtil.nullToDouble(o1.getProductProfit());
									Double object2 = StringUtil.nullToDouble(o2.getProductProfit());
									return object1.compareTo(object2);
								}else if(StringUtil.nullToBoolean(product.getIsSeckillProduct())
										&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())) {
									//?????????????????????????????????
									Double object1 = StringUtil.nullToDouble(o1.getSeckillPrice());
									Double object2 = StringUtil.nullToDouble(o2.getSeckillPrice());
									return object1.compareTo(object2);								
								}else {
									//?????????????????????????????????
									Double object1 = StringUtil.nullToDouble(o1.getPriceWholesale());
									Double object2 = StringUtil.nullToDouble(o2.getPriceWholesale());
									return object1.compareTo(object2);
								}
							}
						});

						//??????????????????????????????
						ProductSpec productSpec = productSpecList.get(0);
						product.setProductSpecId(StringUtil.nullToLong(productSpec.getProductSpecId()));
						product.setPriceRecommend(productSpec.getPriceRecommend());
						product.setPriceCost(productSpec.getPriceCost());
						product.setPaymentPrice(productSpec.getPriceRecommend());
						product.setRealSellPrice(productSpec.getRealSellPrice());
						product.setSeckillPrice(productSpec.getSeckillPrice());
						product.setSeckillProfit(productSpec.getSeckillProfit());
						product.setSeckillTotalStock(productSpec.getSeckillTotalStock());
						product.setSeckillSalesNumber(productSpec.getSeckillSalesNumber());
						product.setSeckillLockNumber(productSpec.getSeckillLockNumber());
						product.setProductProfit(productSpec.getProductProfit());
						product.setRecommendProfit(productSpec.getProductProfit());
					}
				}

				// ???????????????????????????
				if(isPaymentSoldout){
					String message = String.format("\"%s\"???????????????", StringUtil.null2Str(product.getName()));
					msgModel.setIsSucc(false);
					msgModel.setMessage(message);
					return msgModel;
				}else{
					msgModel.setIsSucc(true);
					return msgModel;
				}
			}else{
				// ????????????????????????????????????
				ProductVerifyVo productVerify = new ProductVerifyVo ();
				productVerify.setPriceCost(product.getPriceCost());
				productVerify.setPriceRecommend(product.getPriceRecommend());
				productVerify.setV2Price(product.getV2Price());
				productVerify.setV3Price(product.getV3Price());
				productVerify.setStockNumber(StringUtil.nullToInteger(product.getStockNumber()));
				productVerify.setSeckillPrice(product.getSeckillPrice());
				productVerify.setSeckillMinSellPrice(product.getSeckillMinSellPrice());
				productVerify.setSeckillProfit(product.getSeckillProfit());
				productVerify.setSeckillTotalStock(product.getSeckillTotalStock());
				productVerify.setSeckillSalesNumber(product.getSeckillSalesNumber());
				productVerify.setSeckillLockNumber(product.getSeckillLockNumber());
				product.setPaymentPrice(productVerify.getPriceRecommend());
				//??????????????????????????????
				MsgModel<Integer> cmsgModel = ProductUtil.checkSingleProductConfigure(product, productVerify);
				if(!StringUtil.nullToBoolean(product.getIsSoldout()) && StringUtil.nullToBoolean(cmsgModel.getIsSucc())){
					// ?????????????????????
					int paymentSeckillTotalStock = 0;
					if(StringUtil.nullToInteger(product.getSeckillTotalStock()) > 0){
						paymentSeckillTotalStock += product.getSeckillTotalStock();
					}
					product.setPaymentSeckillTotalStock(paymentSeckillTotalStock);

					// ??????????????????????????????
					product.setPaymentStockNumber(StringUtil.nullToInteger(cmsgModel.getData()));
				}
				return cmsgModel;
			}
		}catch(Exception e){
			log.info("??????????????????id================"+product.getProductId());
		}

		String message = String.format("\"%s\"????????????????????????", StringUtil.null2Str(product.getName()));
		msgModel.setIsSucc(false);
		msgModel.setMessage(message);
		return msgModel;
	}

	/**
	 * ??????????????????????????????????????????
	 * @param wholesale
	 * @param priceCost
	 * @param priceWholesale
	 * @param priceRecommend
	 * @return
	 */
	public static MsgModel<Integer> checkSingleProductConfigure(Product product, ProductVerifyVo productVerify){
		MsgModel<Integer> msgModel = new MsgModel<Integer> ();
		try{
			boolean isConfigureError = false;
			StringBuffer errorBuffer = new StringBuffer ();
			int stockNumber = StringUtil.nullToInteger(productVerify.getStockNumber());
			Double priceCost = StringUtil.nullToDoubleFormat(productVerify.getPriceCost());
			Double priceRecommend = StringUtil.nullToDoubleFormat(productVerify.getPriceRecommend());
			if(priceCost == null  
					|| priceRecommend == null){
				// ??????????????????????????????
				isConfigureError = true;
				errorBuffer.append("??????????????????????????????");
			}else if(priceCost.compareTo(new Double(0.001)) <= 0){
				// ??????????????????????????????0.001
				isConfigureError = true;
				errorBuffer.append("??????????????????????????????0.001");
			}else if(priceRecommend.compareTo(priceCost) < 0) {
				// ??????????????????????????????
				isConfigureError = true;
				errorBuffer.append("??????????????????????????????");
			}

			// ??????????????????????????????
			if(isConfigureError || StringUtil.nullToInteger(stockNumber) <= 0){
				String message = String.format("\"%s\"???????????????", StringUtil.null2Str(product.getName()));
				msgModel.setIsSucc(false);
				msgModel.setMessage(message);
				return msgModel;
			}

			msgModel.setData(stockNumber);
			msgModel.setIsSucc(true);
			return msgModel;
		}catch(Exception e){
			e.printStackTrace();
		}

		String message = String.format("\"%s\"????????????????????????", StringUtil.null2Str(product.getName()));
		msgModel.setIsSucc(false);
		msgModel.setMessage(message);
		return msgModel;
	}


	/**
	 * ????????????????????????????????????????????????
	 * @param name
	 * @param keyword
	 * @param isVague
	 * @return
	 */
	public static MsgModel<Long> getKeywordFuzzyMatchByName(List<String> keywordList, String name){
		MsgModel<Long> msgModel = new MsgModel<Long> ();
		try{
			// ???????????????,?????????????????????
//			List<String> keywordList = IKUtil.getKeywordList(keyword);
			if(keywordList != null && keywordList.size() > 0){
				for(String strKey : keywordList){
					//?????????????????????
					strKey = StringUtil.null2Str(strKey).replaceAll("\\s+", "").toUpperCase();
					name = StringUtil.null2Str(name).replaceAll("\\s+", "").toUpperCase();
					if(StringUtil.null2Str(name).contains(strKey)){
						msgModel.setIsSucc(true);
						return msgModel;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		return msgModel;
	}

	/**
	 * ????????????????????????????????????
	 * @param orderItemsList
	 * @return
	 */
	public static MsgModel<Integer> checkOrderItems(List<OrderItems> orderItemsList){
		MsgModel<Integer> msgModel = new MsgModel<Integer> ();
		if(orderItemsList == null || orderItemsList.size() <= 0){
			msgModel.setIsSucc(false);
			msgModel.setMessage("????????????????????????");
			return msgModel;
		}

		try{
			// ?????????????????????????????????????????????
			Map<Long, OrderItems> productIdOrderItemsMap = new HashMap<Long, OrderItems> ();
			for(OrderItems orderItems : orderItemsList){
				productIdOrderItemsMap.put(orderItems.getProductId(), orderItems);
			}

			// ??????????????????????????????????????????
			ProductManager productManager = Constants.ctx.getBean(ProductManager.class);
			List<Product> productList = productManager.getProductListByProudctIdList(StringUtil.longSetToList(productIdOrderItemsMap.keySet()));
			if(productList == null || productList.size() <= 0){
				msgModel.setIsSucc(false);
				msgModel.setMessage("????????????,???????????????");
				return msgModel;
			}

			// list????????????Map??????
			Map<Long, Product> productMap = new HashMap<Long, Product> ();
			for(Product product : productList){
				productMap.put(product.getProductId(), product);
			}

			// ????????????????????????????????????????????????
			for(Entry<Long, OrderItems> entry : productIdOrderItemsMap.entrySet()){
				Long productId = entry.getKey();
				OrderItems orderItems = entry.getValue();
				log.debug(String.format("[productId=%s, orderItems=%s]", productId, StringUtil.objectToJSON(orderItems)));

				// ???????????????????????????
				if(!StringUtil.compareObject(StringUtil.nullToLong(productId), StringUtil.nullToLong(orderItems.getProductId()))){
					//?????????????????????????????????
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"???????????????", StringUtil.null2Str(orderItems.getProductName())));
					return msgModel;
				}else if(!productMap.containsKey(productId)){
					//?????????????????????????????????
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"???????????????", StringUtil.null2Str(orderItems.getProductName())));
					return msgModel;
				}

				Product dbProudct = productMap.get(entry.getKey());
				log.debug(String.format("[productId=%s, dbProudct=%s]", productId, StringUtil.objectToJSON(dbProudct)));

				// ??????????????????????????????
				MsgModel<Integer> xmsgModel = ProductUtil.checkProductConfigure(dbProudct);
				if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
					msgModel.setIsSucc(false);
					msgModel.setMessage(xmsgModel.getMessage());
					return msgModel;
				}

				// ???????????????????????????
				int stockNumber = StringUtil.nullToInteger(dbProudct.getStockNumber());
				int seckillTotalStock = StringUtil.nullToInteger(dbProudct.getSeckillTotalStock());
				int seckillSalesNumber = StringUtil.nullToInteger(dbProudct.getSeckillSalesNumber());
				if(StringUtil.nullToBoolean(orderItems.getIsSpceProduct())){
					stockNumber = 0;
					seckillTotalStock = 0;
					seckillSalesNumber = 0;
					MsgModel<ProductSpec> pmsgModel = ProductUtil.checkExistProductSpecByProductSpecId(dbProudct, orderItems.getProductSpecId());
					if(StringUtil.nullToBoolean(pmsgModel.getIsSucc())){
						// ??????????????????????????????
						stockNumber = StringUtil.nullToInteger(pmsgModel.getData().getStockNumber());
						seckillTotalStock = StringUtil.nullToInteger(pmsgModel.getData().getSeckillTotalStock());
						seckillSalesNumber = StringUtil.nullToInteger(pmsgModel.getData().getSeckillSalesNumber());
					}
				}

				// ????????????????????????
				if(StringUtil.nullToBoolean(orderItems.getIsSeckillProduct())){
					// ????????????
					seckillSalesNumber += orderItems.getQuantity();
					if(seckillTotalStock < seckillSalesNumber){
						// ????????????????????????????????????
						msgModel.setIsSucc(false);
						msgModel.setMessage(String.format("\"%s\"??????????????????", orderItems.getProductName()));
						if(StringUtil.nullToBoolean(dbProudct.getIsSpceProduct())){
							msgModel.setMessage(String.format("\"%s\",\"%s\"??????????????????", orderItems.getProductName(), orderItems.getProductTags()));
						}
					}else if(stockNumber < seckillTotalStock){
						// ?????????????????????????????????????????????
						msgModel.setIsSucc(false);
						msgModel.setMessage(String.format("\"%s\"??????????????????", orderItems.getProductName()));
						if(StringUtil.nullToBoolean(dbProudct.getIsSpceProduct())){
							msgModel.setMessage(String.format("\"%s\",\"%s\"??????????????????", orderItems.getProductName(), orderItems.getProductTags()));
						}
					}
				}else{
					// ???????????????????????????????????????
					if(StringUtil.nullToBoolean(dbProudct.getIsSeckillReadStatus())){
						seckillTotalStock += orderItems.getQuantity();
						if(stockNumber < seckillTotalStock){
							// ????????????????????????????????????
							msgModel.setIsSucc(false);
							msgModel.setMessage(String.format("\"%s\"??????????????????", orderItems.getProductName()));
							if(StringUtil.nullToBoolean(dbProudct.getIsSpceProduct())){
								msgModel.setMessage(String.format("\"%s\",\"%s\"??????????????????", orderItems.getProductName(), orderItems.getProductTags()));
							}
						}
					}else if(StringUtil.nullToInteger(orderItems.getQuantity()) > stockNumber){
						msgModel.setIsSucc(false);
						msgModel.setMessage(String.format("\"%s\"??????????????????", orderItems.getProductName()));
						if(StringUtil.nullToBoolean(dbProudct.getIsSpceProduct())){
							msgModel.setMessage(String.format("\"%s\",\"%s\"??????????????????", orderItems.getProductName(), orderItems.getProductTags()));
						}
						return msgModel;
					}
				} 
			}

			msgModel.setIsSucc(true);
			return msgModel;
		}catch(Exception e){
			e.printStackTrace();
		}			

		msgModel.setIsSucc(false);
		msgModel.setMessage("????????????");
		return msgModel;
	}



	

	/**
	 * ??????banner????????????
	 * @param wholesale
	 * @return
	 */
	public static List<Map<String, Object>> getProductImageList(Product product){
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		try{
			ProductImageListByIdCacheManger productImageListByIdCacheManger = Constants.ctx.getBean(ProductImageListByIdCacheManger.class);
			List<ProductImage> imageList = productImageListByIdCacheManger.getSession(product.getProductId(), ProductImage.IMAGE_TYPE_HEADER);
			if (imageList != null && imageList.size() > 0) {
				for (ProductImage image : imageList) {
					Map<String, Object> objectMap = new HashMap<String, Object>();
					objectMap.put("imageURL", StringUtil.null2Str(image.getImagePath()));
					mapList.add(objectMap);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapList;
	}


//	/**
//	 * ????????????
//	 */
//	public static MsgModel<Long> getProductByKeyword(String keyword, Product product){
//		MsgModel<Long> msgModel = new MsgModel<Long>();
//		try {
//			TagModelListCacheManager tagModelListCacheManager = Constants.ctx.getBean(TagModelListCacheManager.class);
//
//			//?????????????????????
//			List<Long> productTagIdList = StringUtil.stringToLongArray(product.getTagIds());
//			List<TagModel> tagModelList = tagModelListCacheManager.getSession();
//			//????????????????????????????????????
//			Set<String> wordsSet = new HashSet<String>();
//			if(tagModelList != null && tagModelList.size() > 0 ){
//				for(TagModel tagModel : tagModelList) {
//					wordsSet.add(StringUtil.null2Str(tagModel.getName()));
//				}
//			}
//
//			// ???????????????,?????????????????????
//			List<String> keywordList = IKUtil.getKeywordList(keyword);
//			if (keywordList != null && keywordList.size() > 0) {
//				Set<String> keywordSet = new HashSet<String>();
//				for (String keyStr : keywordList) {
//					keywordSet.add(keyStr);
//				}
//
//				Long brandId = 0L;
//				Long categoryId = 0L;
//				int brandTagNumber = 0;
//				int categoryTagNumber = 0;
//				int productTagNumber = 0;
//				Set<String> productNameSet = new HashSet<String>();
//				if (tagModelList != null && tagModelList.size() > 0) {
//					for (String strKey : keywordSet) {
//						boolean isContainStrKey = false;
//						for (TagModel tagModel : tagModelList) {
//							if (StringUtil.compareObject(strKey, StringUtil.null2Str(tagModel.getName()))) {
//								isContainStrKey = true;
//								if(productTagIdList != null && productTagIdList.contains(tagModel.getTagId())) {
//									//?????????????????????
//									productTagNumber++;
//								}else if (StringUtil.compareObject(StringUtil.nullToInteger(tagModel.getTagType()),
//										TagModel.BRAND_TAG_TYPE)) {
//									// ?????????????????????????????????????????????
//									brandTagNumber++;
//									brandId = StringUtil.nullToLong(tagModel.getObjectId());
//								} else if (StringUtil.compareObject(StringUtil.nullToInteger(tagModel.getTagType()),
//										TagModel.CATEGORY_TAG_TYPE)) {
//									// ?????????????????????????????????????????????
//									categoryTagNumber++;
//									categoryId = StringUtil.nullToLong(tagModel.getObjectId());
//								}
//							}
//						}
//						if (!isContainStrKey) {
//							productNameSet.add(strKey);
//						}
//					}
//				}
//
//				if(productTagNumber >= 2) {
//					//???????????????????????????2????????????
//					msgModel.setIsSucc(true);
//					return msgModel;
//				}else if(productTagNumber == 1) {
//					if(brandTagNumber == 0 && categoryTagNumber == 0) {
//						msgModel.setIsSucc(true);
//						return msgModel;
//					}else if(brandTagNumber == 1 && StringUtil.compareObject(product.getBrandId(), brandId)) {
//						msgModel.setIsSucc(true);
//						return msgModel;
//					}else if(categoryTagNumber == 1 &&(product.getCategoryFidList().contains(categoryId)
//							|| product.getCategoryIdList().contains(categoryId))) {
//						msgModel.setIsSucc(true);
//						return msgModel;
//					}
//				}else if (brandTagNumber >= 2 || categoryTagNumber >= 2 || (brandTagNumber == 0 && categoryTagNumber == 0)) {
//					// ????????????????????????
//					for (String strKey : keywordSet) {
//						if (StringUtil.null2Str(product.getName()).toUpperCase().contains(strKey)) {
//							//?????????????????????
//							strKey = StringUtil.null2Str(strKey).replaceAll("\\s+", "").toUpperCase();
//							if (StringUtil.null2Str(product.getName()).replaceAll("\\s+", "").toUpperCase().contains(strKey)) {
//								msgModel.setIsSucc(true);
//								return msgModel;
//							}
//						}
//					}
//				} else if (brandTagNumber == 1 && categoryTagNumber == 1) {
//					if(StringUtil.compareObject(StringUtil.nullToLong(product.getBrandId()), brandId) && (
//							product.getCategoryFidList().contains(categoryId)
//							|| product.getCategoryIdList().contains(categoryId))) {
//						if (productNameSet != null && productNameSet.size() > 0) {
//							// ??????????????????
//							for (String name : productNameSet) {
//								//?????????????????????
//								name = StringUtil.null2Str(name).replaceAll("\\s+", "").toUpperCase();
//								if (StringUtil.null2Str(product.getName()).replaceAll("\\s+", "").toUpperCase().contains(name)) {
//									msgModel.setIsSucc(true);
//									return msgModel;
//								}
//							}
//						}else {
//							// ????????????????????????
//							msgModel.setIsSucc(true);
//							return msgModel;
//						}
//					}
//
//				} else if (brandTagNumber == 1 ) {
//					// ????????????????????????
//					if (StringUtil.compareObject(StringUtil.nullToLong(product.getBrandId()), brandId)) {
//						if (productNameSet != null && productNameSet.size() > 0) {
//							// ??????????????????
//							for (String name : productNameSet) {
//								//?????????????????????
//								name = StringUtil.null2Str(name).replaceAll("\\s+", "").toUpperCase();
//								if (StringUtil.null2Str(product.getName()).replaceAll("\\s+", "").toUpperCase().contains(name)) {
//									msgModel.setIsSucc(true);
//									return msgModel;
//								}
//							}
//						}else {
//							//???????????????????????????????????????????????????????????????
//							msgModel.setIsSucc(true);
//							return msgModel;
//						}
//					}
//				}else if(categoryTagNumber == 1) {
//					// ????????????????????????
//					if (product.getCategoryFidList().contains(categoryId)
//							|| product.getCategoryIdList().contains(categoryId)) {
//						if (productNameSet != null && productNameSet.size() > 0) {
//							// ??????????????????
//							for (String name : productNameSet) {
//								//?????????????????????
//								name = StringUtil.null2Str(name).replaceAll("\\s+", "").toUpperCase();
//								if (StringUtil.null2Str(product.getName()).replaceAll("\\s+", "").toUpperCase().contains(name)) {
//									msgModel.setIsSucc(true);
//									return msgModel;
//								}
//							}
//						}else {
//							//???????????????????????????????????????????????????????????????
//							msgModel.setIsSucc(true);
//							return msgModel;
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//
//		}
//
//		msgModel.setIsSucc(false);
//		return msgModel;
//	}



	/**
	 * ??????????????????????????????????????????????????????
	 * @param product
	 * @param userInfo
	 * @return
	 */
	public static void getRealProductPriceByUserLevel(Product product,UserInfo userInfo){
		try {
			List<Integer> higherLevelList = new ArrayList<Integer>();
			higherLevelList.add(UserLevel.USER_LEVEL_V2);    //v2
			higherLevelList.add(UserLevel.USER_LEVEL_V3);    //v3
			Integer level = StringUtil.nullToInteger(userInfo.getPaymentUserLevel());
			if(!higherLevelList.contains(level)) {
				//????????????????????????????????????????????????
				return;
			}else if(StringUtil.nullToBoolean(product.getIsGroupProduct())) {
				//?????????????????????
				return;
			}

			if(StringUtil.compareObject(level, UserLevel.USER_LEVEL_V2)) {
				//v2??????
				if(StringUtil.nullToBoolean(product.getIsOpenV2Price())) {
					//???????????????v2???
					if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
						if(product.getProductSpecList() != null && product.getProductSpecList().size() > 0){
							Set<Double> v2PriceSet = new HashSet<Double>();
							List<ProductSpec> productSpecList = product.getProductSpecList();
							for(ProductSpec productSpec : productSpecList) {
								v2PriceSet.add(productSpec.getV2Price());
								productSpec.setRealSellPrice(productSpec.getV2Price());
								if(!(StringUtil.nullToBoolean(product.getIsSeckillProduct())
										&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus()))) {
									//??????????????????????????????????????????
									Integer profit = StringUtil.nullToDoubleFormat(productSpec.getPriceRecommend() - productSpec.getV2Price()).intValue();
									productSpec.setProductProfit(profit);


								}
							}

							//????????????
							Collections.sort(productSpecList, new Comparator<ProductSpec>(){
								@Override
								public int compare(ProductSpec o1, ProductSpec o2) {
									if(StringUtil.nullToBoolean(product.getIsSeckillProduct())) {
										//????????????
										Double object1 = StringUtil.nullToDouble(o1.getSeckillPrice());
										Double object2 = StringUtil.nullToDouble(o2.getSeckillPrice());
										return object1.compareTo(object2);
									}else if(StringUtil.nullToInteger(v2PriceSet.size()) == 1) {
										//???????????????????????????????????????
										Double object1 = StringUtil.nullToDouble(o1.getProductProfit());
										Double object2 = StringUtil.nullToDouble(o2.getProductProfit());
										return -object1.compareTo(object2);
									}else {
										//??????????????????v2?????????
										Double object1 = StringUtil.nullToDouble(o1.getV2Price());
										Double object2 = StringUtil.nullToDouble(o2.getV2Price());
										return object1.compareTo(object2);
									}
								}
							});

							//??????????????????????????????
							ProductSpec productSpec = productSpecList.get(0);
							product.setProductSpecId(StringUtil.nullToLong(productSpec.getProductSpecId()));
							product.setPriceRecommend(productSpec.getPriceRecommend());
							product.setPriceCost(productSpec.getPriceCost());
							product.setV2Price(productSpec.getV2Price());
							product.setV3Price(productSpec.getV3Price());
							product.setRealSellPrice(productSpec.getRealSellPrice());
							product.setSeckillPrice(productSpec.getSeckillPrice());
							product.setSeckillProfit(productSpec.getSeckillProfit());
							product.setSeckillTotalStock(productSpec.getSeckillTotalStock());
							product.setSeckillSalesNumber(productSpec.getSeckillSalesNumber());
							product.setSeckillLockNumber(productSpec.getSeckillLockNumber());
							product.setProductProfit(productSpec.getProductProfit());
							product.setRecommendProfit(product.getProductProfit());
						}
					}else {
						product.setRealSellPrice(product.getV2Price());
						if(!(StringUtil.nullToBoolean(product.getIsSeckillProduct())
								&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus()))) {
							Integer profit = StringUtil.nullToDoubleFormat(product.getPriceRecommend() - product.getV2Price()).intValue();
							product.setProductProfit(profit);
							product.setRecommendProfit(product.getProductProfit());
						}
					}
					product.setProductLevelType(1);
					product.setCommodityPriceTag("V2?????????");
				}
			}else if(StringUtil.compareObject(level, UserLevel.USER_LEVEL_V3)) {
				if(StringUtil.nullToBoolean(product.getIsOpenV3Price())) {
					//???????????????v3???
					if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
						if(product.getProductSpecList() != null && product.getProductSpecList().size() > 0){
							Set<Double> v3PriceSet = new HashSet<Double>();
							List<ProductSpec> productSpecList = product.getProductSpecList();
							for(ProductSpec productSpec : productSpecList) {
								v3PriceSet.add(productSpec.getV3Price());
								productSpec.setRealSellPrice(productSpec.getV3Price());
								if(!(StringUtil.nullToBoolean(product.getIsSeckillProduct())
										&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus()))) {
									//??????????????????????????????????????????
									Integer profit = StringUtil.nullToDoubleFormat(productSpec.getPriceRecommend() - productSpec.getV3Price()).intValue();
									productSpec.setProductProfit(profit);

								}
							}

							Collections.sort(productSpecList, new Comparator<ProductSpec>(){
								@Override
								public int compare(ProductSpec o1, ProductSpec o2) {
									if(StringUtil.nullToBoolean(product.getIsSeckillProduct())) {
										//????????????
										Double object1 = StringUtil.nullToDouble(o1.getSeckillPrice());
										Double object2 = StringUtil.nullToDouble(o2.getSeckillPrice());
										return object1.compareTo(object2);
									}else if(StringUtil.nullToInteger(v3PriceSet.size()) == 1) {
										//???????????????????????????????????????(?????????????????????????????????)
										Double object1 = StringUtil.nullToDouble(o1.getProductProfit());
										Double object2 = StringUtil.nullToDouble(o2.getProductProfit());
										return -object1.compareTo(object2);
									}else {
										//??????????????????v3?????????
										Double object1 = StringUtil.nullToDouble(o1.getV3Price());
										Double object2 = StringUtil.nullToDouble(o2.getV3Price());
										return object1.compareTo(object2);
									}
								}
							});

							//??????????????????????????????
							ProductSpec productSpec = productSpecList.get(0);
							product.setProductSpecId(StringUtil.nullToLong(productSpec.getProductSpecId()));
							product.setPriceRecommend(productSpec.getPriceRecommend());
							product.setPriceCost(productSpec.getPriceCost());
							product.setV2Price(productSpec.getV2Price());
							product.setV3Price(productSpec.getV3Price());
							product.setRealSellPrice(productSpec.getRealSellPrice());
							product.setSeckillPrice(productSpec.getSeckillPrice());
							product.setSeckillProfit(productSpec.getSeckillProfit());
							product.setSeckillTotalStock(productSpec.getSeckillTotalStock());
							product.setSeckillSalesNumber(productSpec.getSeckillSalesNumber());
							product.setSeckillLockNumber(productSpec.getSeckillLockNumber());
							product.setProductProfit(productSpec.getProductProfit());
							product.setRecommendProfit(product.getProductProfit());
						}
					}else {
						product.setRealSellPrice(product.getV3Price());
						if(!(StringUtil.nullToBoolean(product.getIsSeckillProduct())
								&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus()))) {
							Integer profit = StringUtil.nullToDoubleFormat(product.getPriceRecommend() - product.getV3Price()).intValue();
							product.setProductProfit(profit);
							product.setRecommendProfit(product.getProductProfit());
						}
					}
					product.setProductLevelType(2);
					product.setCommodityPriceTag("V3?????????");
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ????????????????????????
	 * @param product
	 */
	public static void getProductSortWeight(Map<Long, Product> productMap) {
		try {
			if(productMap != null && productMap.size() > 0) {
				// ????????????
				List<Map.Entry<Long, Product>> mappingList = new ArrayList<Map.Entry<Long, Product>>(
						productMap.entrySet());
				Collections.sort(mappingList, new Comparator<Map.Entry<Long, Product>>() {
					public int compare(Entry<Long, Product> o1, Entry<Long, Product> o2) {
						try {
							Product productObj1 = o1.getValue();
							Product productObj2 = o2.getValue();
							int salesNumber1 = StringUtil.nullToInteger(productObj1.getSalesNumber());
							int salesNumber2 = StringUtil.nullToInteger(productObj2.getSalesNumber());

							// ????????????
							Integer month1 = StringUtil.nullToInteger(DateUtil.getMonthBetween(DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN,productObj1.getCreateTime()),DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, DateUtil.getCurrentDate())).size());
							Double averageMonthSaleNumber1 = StringUtil.nullToDoubleFormat(salesNumber1 / month1);

							Integer month2 = StringUtil.nullToInteger(DateUtil.getMonthBetween(DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN,productObj2.getCreateTime()),DateUtil.formatDate(DateUtil.DATE_TIME_PATTERN, DateUtil.getCurrentDate())).size());
							Double averageMonthSaleNumber2 = StringUtil.nullToDoubleFormat(salesNumber2 / month2);

							return -averageMonthSaleNumber2.compareTo(averageMonthSaleNumber1);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return 0;
					}
				});

				if (mappingList != null && mappingList.size() > 0) {
					int score = 1;
					for (Map.Entry<Long, Product> entry : mappingList) {
						Product product = entry.getValue();
						if (product != null && product.getProductId() != null) {
							Double sortWeight = new Double(0);

							sortWeight = DoubleUtil.mul(StringUtil.nullToDouble(score), Constants.MONTH_SALE_WEIGHT);
							if (StringUtil.nullToBoolean(product.getIsSeckillProduct())
									|| StringUtil.nullToBoolean(product.getIsOpenV2Price())
									|| StringUtil.nullToBoolean(product.getIsOpenV3Price())) {
								sortWeight = DoubleUtil.mul(sortWeight, Constants.ACTIVITY_SALE_WEIGHT);
							}

							List<Long> tagIdList = StringUtil.stringToLongArray(StringUtil.null2Str(product.getTagIds()));
							if (tagIdList != null 
									&& tagIdList.size() > 0
									&& tagIdList.contains(Constants.PRODUCT_RECOMMEND_TAG_ID)) {
								sortWeight = DoubleUtil.mul(sortWeight, Constants.CUSTOM_SALE_WEIGHT);
							}
							product.setSortWeight(sortWeight);
							score++;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ????????????????????????
	 * @param product
	 * @param userInfo
	 */
	public static void orderProductPriceRegion(Product product, UserInfo userInfo,Boolean isShareProduct) {
		try {
			if(!StringUtil.nullToBoolean(product.getIsGroupProduct())) {
				// ????????????????????????
				if (StringUtil.nullToBoolean(product.getIsSpceProduct())) {

					if (product.getProductSpecList() != null && product.getProductSpecList().size() > 0) {
						try {
							// ????????????
							List<Integer> commomLevelList = new ArrayList<Integer> ();
							commomLevelList.add(UserLevel.USER_LEVEL_COMMON);	//????????????
							commomLevelList.add(UserLevel.USER_LEVEL_BUYERS);	//VIP??????
							// ??????
							Collections.sort(product.getProductSpecList(), new Comparator<ProductSpec>() {
								@Override
								public int compare(ProductSpec o1, ProductSpec o2) {
									Double paymentPrice1 = 0D;
									Double paymentPrice2 = 0D;
									if(commomLevelList.contains(StringUtil.nullToInteger(userInfo.getLevel()))) {
										paymentPrice1 = StringUtil.nullToDoubleFormat(o1.getPriceRecommend());
										paymentPrice2 = StringUtil.nullToDoubleFormat(o2.getPriceRecommend());
									}else {
										paymentPrice1 = StringUtil.nullToDoubleFormat(o1.getRealSellPrice());
										paymentPrice2 = StringUtil.nullToDoubleFormat(o2.getRealSellPrice());
									}
									return paymentPrice1.compareTo(paymentPrice2);
								}
							});

							// ????????????????????????????????????
							ProductSpec productSpec = product.getProductSpecList().get(0);
							Double minPaymentPrice = productSpec.getPaymentPrice();
							Double maxPaymentPrice = product.getProductSpecList().get(product.getProductSpecList().size() - 1).getPaymentPrice();

							if(StringUtil.nullToBoolean(isShareProduct) || commomLevelList.contains(StringUtil.nullToInteger(userInfo.getLevel()))) {
								minPaymentPrice = StringUtil.nullToDoubleFormat(productSpec.getPriceRecommend());
								maxPaymentPrice = StringUtil.nullToDoubleFormat(product.getProductSpecList().get(product.getProductSpecList().size() - 1).getPriceRecommend());
							}else {
								minPaymentPrice = StringUtil.nullToDoubleFormat(productSpec.getRealSellPrice());
								maxPaymentPrice = StringUtil.nullToDoubleFormat(product.getProductSpecList().get(product.getProductSpecList().size() - 1).getRealSellPrice());
							}
							// ??????????????????
							if (StringUtil.nullToBoolean(product.getIsSeckillProduct())) {
								//??????????????????
								product.setPaymentOriginalPriceRegion(String.format("%s~%s", StringUtil.nullToDoubleFormat(minPaymentPrice),
										StringUtil.nullToDoubleFormat(maxPaymentPrice)));
								if (StringUtil.nullToDoubleFormat(minPaymentPrice).compareTo(StringUtil.nullToDoubleFormat(maxPaymentPrice)) == 0) {
									product.setPaymentOriginalPriceRegion(StringUtil.null2Str(StringUtil.nullToDoubleFormat(minPaymentPrice)));
								}
								// ??????
								Collections.sort(product.getProductSpecList(), new Comparator<ProductSpec>() {
									@Override
									public int compare(ProductSpec o1, ProductSpec o2) {
										Double seckillPrice1 = StringUtil.nullToDoubleFormat(o1.getSeckillPrice());
										Double seckillPrice2 = StringUtil.nullToDoubleFormat(o2.getSeckillPrice());
										return seckillPrice1.compareTo(seckillPrice2);
									}
								});
								String minSeckillPrice = StringUtil
										.nullToDoubleFormatDecimal(product.getProductSpecList().get(0).getSeckillPrice());
								String maxSeckillPrice = StringUtil.nullToDoubleFormatDecimal(product.getProductSpecList()
										.get(product.getProductSpecList().size() - 1).getSeckillPrice());
								if (!StringUtil.nullToBoolean(product.getIsSeckillReadStatus())) {
									// ?????????
									minPaymentPrice = StringUtil.nullToDoubleFormat(minSeckillPrice);
									maxPaymentPrice = StringUtil.nullToDoubleFormat(maxSeckillPrice);
								} 
								product.setSeckillPriceRegion(String.format("%s~%s", StringUtil.nullToDoubleFormat(minSeckillPrice), StringUtil.nullToDoubleFormat(maxSeckillPrice)));
								if (StringUtil.nullToDoubleFormat(minSeckillPrice)
										.compareTo(StringUtil.nullToDoubleFormat(maxSeckillPrice)) == 0) {
									product.setSeckillPriceRegion(StringUtil.nullToDoubleFormatStr(minSeckillPrice));
								}
							}

							//?????????????????????????????????????????????app???
							product.setMinPaymentPrice(minPaymentPrice);
							product.setMaxPaymentPrice(maxPaymentPrice);
							product.setPaymentPriceRegion(String.format("%s~%s", StringUtil.nullToDoubleFormat(minPaymentPrice),
									StringUtil.nullToDoubleFormat(maxPaymentPrice)));
							if (StringUtil.nullToDoubleFormat(minPaymentPrice).compareTo(StringUtil.nullToDoubleFormat(maxPaymentPrice)) == 0) {
								product.setPaymentPriceRegion(StringUtil.nullToDoubleFormatStr(minPaymentPrice));
							}

							if(Constants.PRODUCT_TYPE_CROSS_LIST.contains(product.getProductType())) {
								Double minTax = 0D;
								MsgModel<Double> minTaxModel = ProductUtil.getProductTax(minPaymentPrice, product.getProductType(), product.getIsFreeTax());
								if(StringUtil.nullToBoolean(minTaxModel.getIsSucc())) {
									minTax = StringUtil.nullToDoubleFormat(minTaxModel.getData());
								}

								Double maxTax = 0D;
								MsgModel<Double> maxTaxModel = ProductUtil.getProductTax(maxPaymentPrice, product.getProductType(), product.getIsFreeTax());
								if(StringUtil.nullToBoolean(maxTaxModel.getIsSucc())) {
									maxTax = StringUtil.nullToDoubleFormat(maxTaxModel.getData());
								}
								// ????????????
								product.setPaymentTaxRegion(String.format("%s~%s", StringUtil.nullToDoubleFormat(minTax), StringUtil.nullToDoubleFormat(maxTax)));
								if (minTax.compareTo(maxTax) == 0) {
									product.setPaymentTaxRegion(StringUtil.nullToDoubleFormatStr(minTax));
								}
							}

							// ????????????
							Collections.sort(product.getProductSpecList(), new Comparator<ProductSpec>() {
								@Override
								public int compare(ProductSpec o1, ProductSpec o2) {
									Double profit1 = StringUtil.nullToDoubleFormat(o1.getProductProfit());
									Double profit2 = StringUtil.nullToDoubleFormat(o2.getProductProfit());
									return profit1.compareTo(profit2);
								}
							});

							// ????????????
							ProductSpec spec = product.getProductSpecList().get(0);
							Integer minProductProfit = StringUtil.nullToInteger(spec.getProductProfit());
							Integer maxProductProfit = StringUtil.nullToInteger(product.getProductSpecList()
									.get(product.getProductSpecList().size() - 1).getProductProfit());
							product.setProfitRegion(String.format("%s~%s", minProductProfit, maxProductProfit));
							if (minProductProfit.compareTo(maxProductProfit) == 0) {
								product.setProfitRegion(StringUtil.null2Str(minProductProfit));
							}

							// ????????????
							Collections.sort(product.getProductSpecList(), new Comparator<ProductSpec>() {
								@Override
								public int compare(ProductSpec o1, ProductSpec o2) {
									Double price1 = StringUtil.nullToDoubleFormat(o1.getRealSellPrice());
									Double price2 = StringUtil.nullToDoubleFormat(o2.getRealSellPrice());
									if(StringUtil.nullToBoolean(product.getIsSeckillProduct()) && !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())) {
										//????????????
										price1 = StringUtil.nullToDouble(o1.getSeckillPrice());        //?????????
										price2 = StringUtil.nullToDouble(o2.getSeckillPrice());        //?????????
									}
									Double sellPrice1 = StringUtil.nullToDoubleFormat(price1 + StringUtil.nullToDoubleFormat(o1.getProductProfit()));
									Double sellPrice2 = StringUtil.nullToDoubleFormat(price2 + StringUtil.nullToDoubleFormat(o2.getProductProfit()));
									return sellPrice1.compareTo(sellPrice2);
								}
							});
							// ??????????????????
							ProductSpec minProductSpec = product.getProductSpecList().get(0);
							ProductSpec maxProductSpec = product.getProductSpecList().get(product.getProductSpecList().size() - 1);

							Double minSellPrice = DoubleUtil.add(StringUtil.nullToDouble(minProductSpec.getRealSellPrice()),  StringUtil.nullToDouble(minProductSpec.getProductProfit()));
							Double maxSellPrice = DoubleUtil.add(StringUtil.nullToDouble(maxProductSpec.getRealSellPrice()),StringUtil.nullToDouble(maxProductSpec.getProductProfit()));
							if(StringUtil.nullToBoolean(product.getIsSeckillProduct()) && !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())) {
								//????????????

								minSellPrice = DoubleUtil.add(StringUtil.nullToDouble(minProductSpec.getSeckillPrice()), StringUtil.nullToDouble(minProductSpec.getProductProfit()));
								maxSellPrice = DoubleUtil.add(StringUtil.nullToDouble(maxProductSpec.getSeckillPrice()), StringUtil.nullToDouble(maxProductSpec.getProductProfit())); 
							}
							if(Constants.PRODUCT_TYPE_CROSS_LIST.contains(product.getProductType())) {

								MsgModel<Double> minSellPriceModel = ProductUtil.getProductTax(minSellPrice, product.getProductType(), product.getIsFreeTax());
								if(StringUtil.nullToBoolean(minSellPriceModel.getIsSucc())) {
									minSellPrice = DoubleUtil.add(minSellPrice, StringUtil.nullToDouble(minSellPriceModel.getData()));
								}

								MsgModel<Double> maxSellPriceModel = ProductUtil.getProductTax(maxSellPrice, product.getProductType(), product.getIsFreeTax());
								if(StringUtil.nullToBoolean(maxSellPriceModel.getIsSucc())) {
									maxSellPrice = DoubleUtil.add(maxSellPrice, StringUtil.nullToDouble(maxSellPriceModel.getData()));
								}
							}

							product.setSellPriceRegion(String.format("%s~%s", StringUtil.nullToDoubleFormat(minSellPrice), StringUtil.nullToDoubleFormat(maxSellPrice)));
							if (minSellPrice.compareTo(maxSellPrice) == 0) {
								product.setSellPriceRegion(StringUtil.nullToDoubleFormatStr(minSellPrice));
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
						//??????????????????????????????
						if(StringUtil.nullToBoolean(isShareProduct)
								&& Constants.PRODUCT_TYPE_CROSS_LIST.contains(product.getProductType())) {
							for(ProductSpec productSpec : product.getProductSpecList()) {
								MsgModel<Double> msgModel = ProductUtil.getProductTax(productSpec.getPaymentPrice(), product.getProductType(), product.getIsFreeTax());
								if(StringUtil.nullToBoolean(msgModel.getIsSucc())) {
									productSpec.setPaymentPrice(StringUtil.nullToDoubleFormat(DoubleUtil.add(productSpec.getPaymentPrice(), StringUtil.nullToDouble(msgModel.getData()))));
								}
							}
						}
					}
				}else {
					//????????????
					product.setPaymentPriceRegion(StringUtil.nullToDoubleFormatStr(product.getPaymentPrice()));
					product.setPaymentOriginalPriceRegion(StringUtil.nullToDoubleFormatStr((product.getPaymentOriginalPrice())));
					product.setSeckillPriceRegion(StringUtil.nullToDoubleFormatStr((product.getSeckillPrice())));
					product.setMinPaymentPrice(StringUtil.nullToDoubleFormat(product.getPaymentPrice()));
					product.setMaxPaymentPrice(StringUtil.nullToDoubleFormat(product.getPaymentPrice()));
					product.setProfitRegion(StringUtil.null2Str(product.getProductProfit()));
					product.setPaymentTaxRegion(StringUtil.nullToDoubleFormatStr((product.getTax())));

					Double sellPrice = DoubleUtil.add(product.getRealSellPrice(), StringUtil.nullToDouble(product.getProductProfit()));
					if(StringUtil.nullToBoolean(product.getIsSeckillProduct()) && !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())) {
						//????????????
						sellPrice = DoubleUtil.add(product.getSeckillPrice(),StringUtil.nullToDouble(product.getProductProfit()));
					}
					product.setSellPriceRegion(StringUtil.null2Str(StringUtil.nullToDoubleFormat(sellPrice)));
					if(Constants.PRODUCT_TYPE_CROSS_LIST.contains(product.getProductType())) {
						MsgModel<Double> msgModel = ProductUtil.getProductTax(sellPrice, product.getProductType(), product.getIsFreeTax());
						if(StringUtil.nullToBoolean(msgModel.getIsSucc())) {
							product.setSellPriceRegion(StringUtil.nullToDoubleFormatStr((DoubleUtil.add(sellPrice, StringUtil.nullToDouble(msgModel.getData())))));
						}
					}
				}
			}
			if(StringUtil.nullToBoolean(isShareProduct)) {
				//???????????????????????????
				product.setPaymentOriginalPriceRegion(product.getPaymentPriceRegion());
				product.setPaymentPriceRegion(product.getSellPriceRegion());
				if(StringUtil.nullToBoolean(product.getIsGroupProduct())) {
					product.setPaymentGroupPriceRegion(product.getSellPriceRegion());
				}
				log.info(String.format("?????????????????????productId=%s,??????=%s", product.getProductId(),product.getPaymentPriceRegion()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * ??????????????????????????????
	 * @param product
	 * @param remainNumber  ??????????????????
	 */
	public static void setProductStockNumber(Product product, int remainNumber) {
		if(remainNumber >= 0) {
			if(!StringUtil.nullToBoolean(product.getIsSpceProduct())) {
				// ??????????????????????????????,?????????????????????????????????????????????
				if(remainNumber < StringUtil.nullToInteger(product.getPaymentStockNumber())) {
					product.setPaymentStockNumber(remainNumber);
				}
				
				if(StringUtil.nullToBoolean(product.getIsSeckillProduct())
						&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())
						&& StringUtil.nullToBoolean(product.getIsSeckillLimit())) {
					//????????????
					if(remainNumber < StringUtil.nullToInteger(product.getSeckillLimitNumber()) ) {
						product.setSeckillLimitNumber(remainNumber);
					}
				}
			}else {
				if(product.getProductSpecList() != null && product.getProductSpecList().size() > 0) {
					for(ProductSpec productSpec : product.getProductSpecList()) {
						if(remainNumber < StringUtil.nullToInteger(productSpec.getPaymentStockNumber())) {
							productSpec.setPaymentStockNumber(remainNumber);
						}
						if(StringUtil.nullToBoolean(product.getIsSeckillProduct())
								&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())
								&& StringUtil.nullToBoolean(product.getIsSeckillLimit())) {
							//????????????
							if(remainNumber < StringUtil.nullToInteger(productSpec.getSeckillLimitNumber()) ) {
								productSpec.setSeckillLimitNumber(remainNumber);
							}
						}
					}
				}
			}
		}
	}


	/**
	 * ??????????????????????????????????????????
	 * @param productId
	 * @param userInfo
	 * @return
	 */
	public static void checkCouponByProduct(Product product,UserInfo userInfo){
		try {
			UserCouponListByUserIdCacheManager userCouponListByUserIdCacheManager = Constants.ctx.getBean(UserCouponListByUserIdCacheManager.class);
			List<UserCoupon> userCouponList = userCouponListByUserIdCacheManager.getSession(StringUtil.nullToLong(userInfo.getUserId()));
		    if(userCouponList != null && !userCouponList.isEmpty()) {
		    	List<Coupon> productCouponList = new ArrayList<Coupon>();
		    	Map<Long, Coupon> couponMap = Constants.COUPON_MAP;
		    	for(UserCoupon userCoupon : userCouponList) {
		    		//???????????????
		    		Coupon coupon = couponMap.get(StringUtil.nullToLong(userCoupon.getCouponId()));
		    		if(coupon == null || coupon.getCouponId() == null
		    				|| !StringUtil.nullToBoolean(coupon.getIsEnable())
		    				|| (StringUtil.nullToDouble(coupon.getFullAmount()).compareTo(0D) <= 0)
		    				|| (StringUtil.nullToDouble(coupon.getGiveAmount()).compareTo(0D) <= 0)
		    				|| !StringUtil.compareObject(userCoupon.getCouponStatus(), UserCoupon.USER_COUPON_STATUS_NOT_USED)) {
		    			continue;
		    		}
		    		
		    		Integer attribute = StringUtil.nullToInteger(coupon.getAttribute());
		    		if(StringUtil.compareObject(attribute, Coupon.COUPON_ATTRIBUTE_ALL)) {
		    			//?????????
		    			productCouponList.add(coupon);
		    		}else if(StringUtil.compareObject(attribute, Coupon.COUPON_ATTRIBUTE_CATEGORY)) {
		    			//?????????
						List<Long> productCategoryIdList = StringUtil.stringToLongArray(coupon.getAttributeContent());
                        if(productCategoryIdList != null && !productCategoryIdList.isEmpty()
                        		&& productCategoryIdList.retainAll(product.getCategoryIdList())) {
                        	productCouponList.add(coupon);
                        }
		    		}else{
		    			//?????????
						List<Long> productIdList = StringUtil.stringToLongArray(coupon.getAttributeContent());
						if(productIdList != null && !productIdList.isEmpty()
                        		&& productIdList.contains(StringUtil.nullToLong(product.getProductId()))) {
                        	productCouponList.add(coupon);
                        }
		    		}
		    	}
		    	
		    	if(productCouponList != null && !productCouponList.isEmpty()) {
		    		//??????????????????
		    		Collections.sort(productCouponList,new Comparator<Coupon>() {
						@Override
						public int compare(Coupon o1, Coupon o2) {
							Double amount1 = StringUtil.nullToDouble(o1.getGiveAmount());
							Double amount2 = StringUtil.nullToDouble(o2.getGiveAmount());
							return -amount1.compareTo(amount2);
						}
					});
		    		Coupon coupon = productCouponList.get(0);
		    		if(coupon != null && coupon.getCouponId() != null) {
		    			product.setCouponIntro(String.format("???%s???%s",StringUtil.getRealNumber(coupon.getFullAmount()) ,StringUtil.getRealNumber(coupon.getGiveAmount())));
		    		}
		    	}
		    }
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * ??????????????????
	 * @param product
	 * @param userInfo
	 * @param isMustSell
	 */
	public static void checkIsAggrProduct(Product product, UserInfo userInfo, Boolean isMustSell) {
		try {
			List<Long> aggrProductIdList = StringUtil.stringToLongArray(StringUtil.null2Str(product.getAggrProductIds()));
			if (aggrProductIdList != null && !aggrProductIdList.isEmpty()) {
				List<Product> aggrProductList = new ArrayList<Product>();
				for (Long aggrProductId : aggrProductIdList) {
					MsgModel<Product> asgModel = ProductUtil.getProductByUserLevel(aggrProductId, null, userInfo, isMustSell, false);
					if (StringUtil.nullToBoolean(asgModel.getIsSucc())) {
						aggrProductList.add(asgModel.getData());
					}
				}

				if (aggrProductList != null && !aggrProductList.isEmpty()) {
					// ??????
					Collections.sort(aggrProductList, new Comparator<Product>() {
						@Override
						public int compare(Product o1, Product o2) {
							int sort = StringUtil.booleanToInt(o1.getIsPaymentSoldout()).compareTo(StringUtil.booleanToInt(o2.getIsPaymentSoldout()));

							if (sort == 0) {
								// ??????
								sort = -StringUtil.booleanToInt(o1.getIsSeckillProduct()).compareTo(StringUtil.booleanToInt(o2.getIsSeckillProduct()));
								if (sort == 0) {
									// ????????????
									Integer productType1 = StringUtil.nullToInteger(o1.getProductType());
									Integer productType2 = StringUtil.nullToInteger(o2.getProductType());
									sort = productType1.compareTo(productType2);
								}
							}

							return sort;
						}
					});

					List<Integer> productTypeList = new ArrayList<Integer>();
					productTypeList.add(GoodsType.GOODS_TYPE_DIRECT); // ??????
					productTypeList.add(GoodsType.GOODS_TYPE_DIRECT_GO);// ??????
					
					// ??????????????????????????????
					aggrProductList.add(0, product);
					for (Product aggrProduct : aggrProductList) {
						ProductWarehouse aggrProductWarehouse = ProductUtil.getProductWarehouse(StringUtil.nullToLong(aggrProduct.getWareHouseId()));
						if (aggrProductWarehouse != null && aggrProductWarehouse.getWarehouseId() != null) {
							aggrProduct.setProductType(StringUtil.nullToInteger(aggrProductWarehouse.getProductType()));
							aggrProduct.setWareHouseName(String.format("%s??????", StringUtil.null2Str(aggrProductWarehouse.getName())));
							if (productTypeList.contains(aggrProductWarehouse.getProductType())) {
								aggrProduct.setPayIntro("???????????????7~10?????????????????????");
							} else {
								aggrProduct.setPayIntro("???????????????1~2??????????????????");
							}
						}
					}
					product.setIsAggrProduct(true);
					product.setAggrProductList(aggrProductList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ??????????????????
	 * @param product
	 * @return
	 */
	public static MsgModel<Double> getProductTax(Double paymentPrice, Integer productType, Boolean isFreeTax){
		MsgModel<Double> msgModel = new MsgModel<Double>();
		try {
			List<Integer> productTypeList = new ArrayList<Integer>();
			productTypeList.add(GoodsType.GOODS_TYPE_CROSS);   //??????
			productTypeList.add(GoodsType.GOODS_TYPE_DIRECT);  //??????

			Double tax = new Double(0D);
			if(!StringUtil.nullToBoolean(isFreeTax)) {
				if(productTypeList.contains(StringUtil.nullToInteger(productType))) {
					tax = DoubleUtil.mul(paymentPrice, Product.TAXRATE);
				}
			}
			msgModel.setIsSucc(true);
			msgModel.setData(StringUtil.nullToDoubleFormat(tax));
			return msgModel;
		}catch(Exception e) {
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	
	/**
	 * ???????????????????????????????????????????????????
	 * @return
	 */
	public static Map<String,List<ProductCategory>> getProductCategroyList(){
		Map<String,List<ProductCategory>> map = new HashMap<String,List<ProductCategory>>();
		try {
			ProductCategoryAllListCacheManager productCategoryAllListCacheManager = Constants.ctx.getBean(ProductCategoryAllListCacheManager.class);
			List<ProductCategory> productCategoryList = productCategoryAllListCacheManager.getSession();
			List<ProductCategory> firstCategroyList = new ArrayList<ProductCategory>();
			List<ProductCategory> secondCategroyList = new ArrayList<ProductCategory>();

			if(productCategoryList != null && !productCategoryList.isEmpty()) {
				for(ProductCategory productCategory : productCategoryList) {
					if(StringUtil.compareObject(productCategory.getLevel(), 1)) {
						firstCategroyList.add(productCategory);
					}else {
						if(StringUtil.nullToBoolean(productCategory.getIsRecommend())) {
							//??????
							secondCategroyList.add(productCategory);
						}
					}
				}
			}
			
			//??????
			Collections.sort(secondCategroyList,new Comparator<ProductCategory>() {
				@Override
				public int compare(ProductCategory o1, ProductCategory o2) {
					Integer sort1 = StringUtil.nullToInteger(o1.getSort());
					Integer sort2 = StringUtil.nullToInteger(o2.getSort());
					return sort1.compareTo(sort2);
				}
			});
			
			Collections.sort(firstCategroyList,new Comparator<ProductCategory>() {
				@Override
				public int compare(ProductCategory o1, ProductCategory o2) {
					Integer sort1 = StringUtil.nullToInteger(o1.getSort());
					Integer sort2 = StringUtil.nullToInteger(o2.getSort());
					return sort1.compareTo(sort2);
				}
			});
			
			map.put("secondCategoryList", secondCategroyList);
			map.put("firstCategroyList", firstCategroyList);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	/**
	 * ?????????????????????????????????
	 * @param product
	 * @return
	 */
	public static void getFirstAggrProduct(Product product){
		try {
			if(StringUtil.nullToBoolean(product.getIsAggrProduct())) {
				//????????????
				List<Product> aggrProductList = product.getAggrProductList();
				if(aggrProductList != null && !aggrProductList.isEmpty()) {
					// ??????
					Collections.sort(aggrProductList, new Comparator<Product>() {
						@Override
						public int compare(Product o1, Product o2) {
							int sort = StringUtil.booleanToInt(o1.getIsPaymentSoldout()).compareTo(StringUtil.booleanToInt(o2.getIsPaymentSoldout()));
							
							if (sort == 0) {
								// ??????
								sort = -StringUtil.booleanToInt(o1.getIsSeckillProduct()).compareTo(StringUtil.booleanToInt(o2.getIsSeckillProduct()));
								if (sort == 0) {
									// ????????????
									Integer productType1 = StringUtil.nullToInteger(o1.getProductType());
									Integer productType2 = StringUtil.nullToInteger(o2.getProductType());
									sort = productType1.compareTo(productType2);
								}
							}
							return sort;
						}
					});
					product = aggrProductList.get(0);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}

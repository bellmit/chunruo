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
import com.chunruo.cache.portal.impl.ProductAnswerListByQuestionIdCacheManager;
import com.chunruo.cache.portal.impl.ProductByIdCacheManager;
import com.chunruo.cache.portal.impl.ProductCategoryAllListCacheManager;
import com.chunruo.cache.portal.impl.ProductImageListByIdCacheManger;
import com.chunruo.cache.portal.impl.ProductQuestionListByProductIdCacheManager;
import com.chunruo.cache.portal.impl.RechargeTemplateListCacheManager;
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
import com.chunruo.core.model.ProductAnswer;
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
import com.chunruo.core.service.ProductSpecManager;
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
	 * 根据用户等级获取价格
	 * @param priceWholesale
	 * @param priceRecommend
	 * @param userInfo
	 * @return
	 */
	public static MsgModel<Double> getPaymentPriceByUserInfo(Product product,Double priceWholesale, Double priceRecommend, UserInfo userInfo,Boolean isFromProductGroup){
		MsgModel<Double> msgModel = new MsgModel<Double> ();
		try{
			// 所有用户等级
			List<Integer> userLevelList = new ArrayList<Integer> ();
			userLevelList.add(UserLevel.USER_LEVEL_COMMON);	//普通用户
			userLevelList.add(UserLevel.USER_LEVEL_BUYERS);	//VIP用户
			userLevelList.add(UserLevel.USER_LEVEL_DEALER);	//经销商
			userLevelList.add(UserLevel.USER_LEVEL_AGENT);	//平台总代
			userLevelList.add(UserLevel.USER_LEVEL_V2);	    //v2
			userLevelList.add(UserLevel.USER_LEVEL_V3);	    //v3

			// 高级等级用户
			List<Integer> userLevelWholesale = new ArrayList<Integer> ();
			userLevelWholesale.add(UserLevel.USER_LEVEL_DEALER);	// 经销商
			userLevelWholesale.add(UserLevel.USER_LEVEL_AGENT);		// 平台总代
			userLevelWholesale.add(UserLevel.USER_LEVEL_V2);		// v2
			userLevelWholesale.add(UserLevel.USER_LEVEL_V3);		// v3

			// 默认普通用户
			Integer userLevel = UserLevel.USER_LEVEL_COMMON;
			if(StringUtil.nullToBoolean(userInfo.getIsAgent()) && userLevelList.contains(StringUtil.nullToInteger(userInfo.getLevel()))){
				userLevel = StringUtil.nullToInteger(userInfo.getLevel());
			}

			boolean isShareProduct = false;  //是否分享商品
			//商品分享信息
			ProductShareRecord productShareRecord = userInfo.getProductShareRecord();
			if(productShareRecord != null 
					&& productShareRecord.getRecordId() != null
					&& StringUtil.nullToBoolean(userInfo.getIsShareUser())) {
				isShareProduct = true;
			}

			// 默认商品支付价格为组合售卖价格
			Double paymentPrice = StringUtil.nullToDoubleFormat(priceRecommend);
			if(StringUtil.nullToBoolean(isShareProduct)
					&& StringUtil.nullToBoolean(isFromProductGroup)
					&& !StringUtil.nullToBoolean(product.getIsFreeTax())
					&& Constants.PRODUCT_TYPE_CROSS_LIST.contains(product.getProductType())) {
				//分享商品显示售价
				MsgModel<Double> taxModel = ProductUtil.getProductTax(paymentPrice, product.getProductType(), product.getIsFreeTax());
				if(StringUtil.nullToBoolean(taxModel.getIsSucc())) {
					paymentPrice = DoubleUtil.add(paymentPrice, StringUtil.nullToDouble(taxModel.getData()));
				}
			}else if(userLevelWholesale.contains(userLevel) && !StringUtil.nullToBoolean(isShareProduct)){
				// 市场价格等级用户并且不是分享商品
				paymentPrice = StringUtil.nullToDoubleFormat(priceWholesale);
			}

			msgModel.setIsSucc(true);
			msgModel.setData(paymentPrice);
			return msgModel;
		}catch(Exception e){
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		msgModel.setMessage("获取支付价格错误");
		return msgModel;
	}

	/**
	 * 检查是否有效秒杀场次
	 * @param productSeckill
	 * @return
	 */
	public static MsgModel<Integer> checkSeckillSeason(ProductSeckill productSeckill){
		MsgModel<Integer> msgModel = new MsgModel<Integer> ();
		try{
			// 秒杀场次信息
			if(productSeckill != null 
					&& productSeckill.getSeckillId() != null
					&& DateUtil.isEffectiveTime(DateUtil.DATE_HOUR, productSeckill.getStartTime())
					&& DateUtil.isEffectiveTime(DateUtil.DATE_HOUR, productSeckill.getEndTime())){
				// 明天23:50:00时间技术
				String strLastDate = DateUtil.formatDate(DateUtil.DATE_FORMAT_YEAR, DateUtil.getDateAfterByDay(DateUtil.getCurrentDate(), 1));
				Date lastDate = DateUtil.parseDate(DateUtil.DATE_FORMAT_HOUR, String.format("%s 23:50:00", strLastDate));

				// 检查秒杀场次的开始时间和结束时间是否有效
				Date seckillStartDate = DateUtil.parseDate(DateUtil.DATE_FORMAT_HOUR, productSeckill.getStartTime());
				Date seckillEndDate = DateUtil.parseDate(DateUtil.DATE_FORMAT_HOUR, productSeckill.getEndTime());
				if(seckillStartDate != null && seckillEndDate != null){
					Long seckillStartTime = seckillStartDate.getTime();
					Long seckillEndTime = seckillEndDate.getTime();
					Long currentTimeMillis = System.currentTimeMillis();
					if(currentTimeMillis <= seckillEndTime && seckillStartTime <= lastDate.getTime()){
						//默认秒杀为即将开始状态
						Integer seckillType = ProductSeckill.SECKILL_TYPE_READY;
						if(seckillStartTime < currentTimeMillis){
							//秒杀已开抢状态
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
	 * 检查组合商品
	 * @param product
	 * @return
	 */
	public static MsgModel<Integer> checkGroupProduct(Product product){
		MsgModel<Integer> msgModel = new MsgModel<Integer> ();
		try{
			// 检查组合商品的子商品是否有效
			Map<Long, List<ProductGroup>> productGroupListMap = product.getProductGroupListMap();
			if(productGroupListMap == null || productGroupListMap.size() <= 0){
				msgModel.setIsSucc(false);
				msgModel.setMessage(String.format("\"%s\"组合商品已下架或不存在", product.getName()));
				return msgModel;
			}

			// 商品库存数
			List<Integer> paymentStockNumberList = new ArrayList<Integer>();
			// 商品仓库id
			Set<Long> wareHouseIdSet = new HashSet<Long>();

			// 组合商品每项检查
			for(Entry<Long, List<ProductGroup>> entry : productGroupListMap.entrySet()){
				Long productId = entry.getKey();
				List<ProductGroup> productGroupList = entry.getValue();
				if(productGroupList == null || productGroupList.size() <= 0){
					// 单个商品已下架(组合商品下架)
					product.setIsSoldout(false);
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"组合商品售罄", product.getName()));
					return msgModel;
				}

				// 检查商品详情是否有效
				MsgModel<Product> xmsgModel = ProductUtil.getProductByProductId(productId, true);
				if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
					// 单个商品已下架(组合商品下架)
					product.setIsSoldout(false);
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"组合商品售罄", product.getName()));
					return msgModel;
				}

				// 检查商品是否为秒杀商品
				Product xproduct = xmsgModel.getData();
				if(StringUtil.nullToBoolean(xproduct.getIsSeckillProduct())){
					// 秒杀商品不能和组合商品同时使用
					product.setIsSoldout(false);
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"组合商品秒杀售罄", xproduct.getName()));
					return msgModel;
				}

				wareHouseIdSet.add(xproduct.getWareHouseId());
				boolean isPaymentSoldout = true;
				if(StringUtil.nullToBoolean(xproduct.getIsSpceProduct())){
					// 规格商品
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
							// 商品成本价格不能为空
							isConfigureError = true;
						}else if(priceCost.compareTo(new Double(0.001)) <= 0){
							// 商品成本价格不能低于0.001
							isConfigureError = true;
						}else if(priceWholesale.compareTo(priceCost) < 0){
							// 商品市场价格不能低于成本价格
							isConfigureError = true;
						}else if(priceRecommend.compareTo(priceWholesale) <= 0){
							// 商品售卖价格不能低于市场成本价格
							isConfigureError = true;
						}

						// 商品配置信息是否有效
						if(isConfigureError){
							String message = String.format("\"%s\"组合商品已售罄", StringUtil.null2Str(xproduct.getName()));
							msgModel.setIsSucc(false);
							msgModel.setMessage(message);
							return msgModel;
						}

						// 默认设置0库存
						productGroup.setPaymentStockNumber(0);
						productGroup.setIsPaymentSoldout(true);

						// 动态组合商品的库存数
						if(productSpecMap.containsKey(productGroup.getProductSpecId())){
							ProductSpec productSpec = productSpecMap.get(productGroup.getProductSpecId());
							Integer paymentStockNumber = productSpec.getPaymentStockNumber() / productGroup.getSaleTimes();
							productGroup.setPaymentStockNumber(paymentStockNumber);
						}

						// 其中一个单规格大于0就可以正常购买
						if(StringUtil.nullToInteger(productGroup.getPaymentStockNumber()) > 0){
							isPaymentSoldout = false;
							productGroup.setIsPaymentSoldout(false);
							paymentStockNumberList.add(productGroup.getPaymentStockNumber());
						}
					}
				}else{
					// 普通商品
					for(ProductGroup productGroup : productGroupList){
						boolean isConfigureError = false;
						Double priceCost = StringUtil.nullToDoubleFormat(productGroup.getGroupPriceCost());
						Double priceWholesale = StringUtil.nullToDoubleFormat(productGroup.getGroupPriceWholesale());
						Double priceRecommend = StringUtil.nullToDoubleFormat(productGroup.getGroupPriceRecommend());
						if(priceCost == null  
								|| priceWholesale == null 
								|| priceRecommend == null){
							// 商品成本价格不能为空
							isConfigureError = true;
						}else if(priceCost.compareTo(new Double(0.001)) <= 0){
							// 商品成本价格不能低于0.001
							isConfigureError = true;
						}else if(priceWholesale.compareTo(priceCost) < 0){
							// 商品市场价格不能低于成本价格
							isConfigureError = true;
						}else if(priceRecommend.compareTo(priceWholesale) <= 0){
							// 商品售卖价格不能低于市场成本价格
							isConfigureError = true;
						}

						// 商品配置信息是否有效
						if(isConfigureError){
							String message = String.format("\"%s\"组合商品已售罄", StringUtil.null2Str(xproduct.getName()));
							msgModel.setIsSucc(false);
							msgModel.setMessage(message);
							return msgModel;
						}

						// 默认设置0库存
						productGroup.setPaymentStockNumber(0);
						productGroup.setIsPaymentSoldout(true);

						Integer paymentStockNumber = xproduct.getPaymentStockNumber() / productGroup.getSaleTimes();
						productGroup.setPaymentStockNumber(paymentStockNumber);

						// 其中一个单规格大于0就可以正常购买
						if(StringUtil.nullToInteger(productGroup.getPaymentStockNumber()) > 0){
							isPaymentSoldout = false;
							productGroup.setIsPaymentSoldout(false);
							paymentStockNumberList.add(productGroup.getPaymentStockNumber());
						}
					}
				}

				// 检查组合里所有商品必须同一个仓库
				if (!StringUtil.compareObject(wareHouseIdSet.size(), 1)) {
					isPaymentSoldout = true;

					// 组合商品仓库配置错误
					product.setIsSoldout(false);
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"组合商品仓库配置错误", product.getName()));
					return msgModel;
				}

				// 单个组合商品按倍数已售罄
				if(StringUtil.nullToBoolean(isPaymentSoldout)){
					product.setIsSoldout(false);
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"组合商品倍数已售罄", product.getName()));
					return msgModel;
				}
			}

			// 组合商品库存为子商品的最小库存
			Collections.sort(paymentStockNumberList);
			product.setPaymentStockNumber(paymentStockNumberList.get(0));
			msgModel.setIsSucc(true);
			return msgModel;
		}catch(Exception e){
			e.printStackTrace();
		}
		msgModel.setIsSucc(false);
		msgModel.setMessage(String.format("\"%s\"组合商品售罄", product.getName()));
		return msgModel;
	}


	/**
	 * 检查商品是否购买过
	 * @param productId
	 * @return
	 */
	public static boolean isExistBuyProduct(Long productId, Long userId){
		try{
			//检查商品是否购买过
			OrderListByUserIdCacheManager orderListByUserIdCacheManager = Constants.ctx.getBean(OrderListByUserIdCacheManager.class);
			List<Order> orderList = orderListByUserIdCacheManager.getSession(userId);
			if (orderList != null && orderList.size() > 0){
				// 检查订单是否支付成功
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
	 * 产地信息
	 * 仓库类型
	 * @param productType
	 * @param countryName
	 * @return
	 */
	public static List<BarrageVo> getProductInfoList(Integer productType,ProductBrand brand) {
		List<BarrageVo> barrageVoList = new ArrayList<BarrageVo>();
		//商品类型(1:国内;2:跨境;3:直邮)
		if(StringUtil.compareObject(GoodsType.GOODS_TYPE_CROSS, productType)){
			barrageVoList.add(new BarrageVo(StringUtil.null2Str(Constants.conf.getProperty("product.type.cross.default.image")),"跨境商品"));
			barrageVoList.add(new BarrageVo(StringUtil.null2Str(Constants.conf.getProperty("product.type.cross.plane.image")),"保税仓发货"));
		}else if(StringUtil.compareObject(GoodsType.GOODS_TYPE_DIRECT, productType)
				|| StringUtil.compareObject(GoodsType.GOODS_TYPE_DIRECT_GO, productType)){
			barrageVoList.add(new BarrageVo(StringUtil.null2Str(Constants.conf.getProperty("product.type.cross.default.image")),"跨境商品"));
			barrageVoList.add(new BarrageVo(StringUtil.null2Str(Constants.conf.getProperty("product.type.cross.plane.image")),"海外直邮"));
		}

		if(brand != null && brand.getBrandId() != null) {
			if(!StringUtil.isNull(StringUtil.null2Str(brand.getCountryName()))) {
				barrageVoList.add(new BarrageVo(StringUtil.null2Str(brand.getCountryImage()),StringUtil.null2Str(brand.getCountryName())));
			}else {
				barrageVoList.add(new BarrageVo(StringUtil.null2Str(brand.getCountryImage()),"其他国家"));
			}
		}

		return barrageVoList;
	}

	/**
	 * 根据仓库查询商品类型
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
	 * 根据仓库查询商品类型
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
	 * 根据品牌查询商品所属国家
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
	 * 根据品牌查询商品所属国家
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
	 * 检查商品规格是否存在
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

				// 检查规格信息是否存在有效
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
		msgModel.setMessage(String.format("\"%s\"规格商品已下架或不存在", product.getName()));
		return msgModel;
	}

	/**
	 * 按用户等级计算商品价格
	 * 不包含规格信息
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
		msgModel.setMessage("检查商品信息错误");
		return msgModel;
	}

	/**
	 * 按用户等级计算商品价格
	 * 包含规格商品信息
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
		msgModel.setMessage("检查商品规格信息错误");
		return msgModel; 
	}


	/**
	 * 规整分享商品价格
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
			//检查商品
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
		msgModel.setMessage("服务器错误");
		return msgModel;
	}

	/**
	 * 按用户等级计算商品价格
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
			userLevelList.add(UserLevel.USER_LEVEL_BUYERS);	//VIP用户
			userLevelList.add(UserLevel.USER_LEVEL_DEALER);	//经销商
			
			List<Integer> userLevelWholesale = new ArrayList<Integer> ();
			userLevelWholesale.add(UserLevel.USER_LEVEL_DEALER);	// 经销商

			Integer userLevel = UserLevel.USER_LEVEL_COMMON;
			if(userLevelList.contains(StringUtil.nullToInteger(userInfo.getLevel()))){
				userLevel = StringUtil.nullToInteger(userInfo.getLevel());
			}

			//设置用户实时购买等级
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
						msgModel.setMessage(String.format("\"%s,%s\"商品已售罄", product.getName(), StringUtil.null2Str(productSpec.getProductTags())));
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
				//非分享商品
				if(userLevelWholesale.contains(userLevel)){
					// 市场价格等级用户
					paymentPrice = StringUtil.nullToDoubleFormat(realSellPrice);
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
			product.setPaymentPrice(product.getPriceRecommend());
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

		// 商品对象不存在错误
		msgModel.setIsSucc(false);
		msgModel.setMessage("商品已下架或不存在");
		return msgModel;
	}

	/**
	 * 商品缓存对象克隆复制新对象
	 * @param productId
	 * @return
	 */
	public static Product getProduct(Long productId){
		try{
			ProductByIdCacheManager productByIdCacheManager = Constants.ctx.getBean(ProductByIdCacheManager.class);
			Product product = productByIdCacheManager.getSession(productId);
			if(product != null){
				// 浅度复制对象
				return product.clone();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 检查商品是否有效
	 * 私有方法只能在本类中使用
	 * @param productId
	 * @param isMustSell(是否必须出售状态)
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

				if(!StringUtil.nullToBoolean(product.getIsGroupProduct())){

					// 检查商品是否正常
					MsgModel<Integer> xsgModel = ProductUtil.checkProductConfigure(product);
					if(!StringUtil.nullToBoolean(xsgModel.getIsSucc())){
						// 已售罄
						isPaymentSoldout = true;
						if(isMustSell
								&& !StringUtil.nullToBoolean(product.getIsSoldout())
								&& !StringUtil.nullToBoolean(product.getIsSeckillProduct())){
							// 非秒杀商品更新下架状态商品
							productManager.updateProductSoldoutStatus(productId, true);
							try{
								productByIdCacheManager.removeSession(productId);
							}catch(Exception e){
								e.printStackTrace();
							}

							// 代理商品已下架
							String message = String.format("\"%s\"商品已售罄", StringUtil.null2Str(product.getName()));
							msgModel.setIsSucc(false);
							msgModel.setMessage(message);
							return msgModel;
						}

						// 检查是否秒杀配置错误商品(即将开始)
						if(!StringUtil.nullToBoolean(isMustSell)
								&& StringUtil.nullToBoolean(product.getIsSeckillProduct()) 
								&& StringUtil.nullToBoolean(product.getIsSeckillReadStatus())){
							product.setIsSeckillProduct(false);
							product.setIsSeckillReadStatus(false);
						}
					}
				}else{
					// 检查组合商品
					MsgModel<Integer> xsgModel = ProductUtil.checkGroupProduct(product);
					if(!StringUtil.nullToBoolean(xsgModel.getIsSucc())){
						// 已售罄
						isPaymentSoldout = true;
						if(isMustSell && !StringUtil.nullToBoolean(product.getIsSoldout())){
							// 组合商品更新下架状态商品
							productManager.updateProductSoldoutStatus(productId, true);
							try{
								productByIdCacheManager.removeSession(productId);
							}catch(Exception e){
								e.printStackTrace();
							}

							// 代理商品已下架
							msgModel.setIsSucc(false);
							msgModel.setMessage(xsgModel.getMessage());
							return msgModel;
						}
					}
				}

				// 检查商品是否售罄
				if(StringUtil.nullToBoolean(product.getIsSoldout())
						|| !StringUtil.nullToBoolean(product.getStatus())
						|| StringUtil.nullToBoolean(product.getIsDelete())){
					// 已售罄
					isPaymentSoldout = true;
				}

				// 检查商品是否已下架
				if(isPaymentSoldout){
					product.setStockNumber(0);
					product.setPaymentStockNumber(0);

					if(isMustSell){
						// 代理商品已下架
						String message = String.format("\"%s\"商品已售罄", StringUtil.null2Str(product.getName()));
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

		// 商品对象不存在错误
		msgModel.setIsSucc(false);
		msgModel.setMessage("商品已下架或不存在");
		return msgModel;
	}

	/**
	 * 检查商品价格配置信息是否有效
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
					String message = String.format("\"%s\"商品规格解析错误", StringUtil.null2Str(product.getName()));
					msgModel.setIsSucc(false);
					msgModel.setMessage(message);
					return msgModel;
				}

				//商品规格信息
				boolean isPaymentSoldout = true;
				if(product.getProductSpecList() != null && product.getProductSpecList().size() > 0){
					int paymentStockNumber = 0;
					int salesNumber = 0;
					int paymentSeckillTotalStock = 0;
					List<ProductSpec> productSpecList = new ArrayList<ProductSpec> ();
					Set<Double> priceWholesaleSet = new HashSet<Double>();
					for(ProductSpec productSpec : product.getProductSpecList()){
						// 检查商品是否有效转换对象
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

						//其中一个商品规格正常表示成功
						MsgModel<Integer> cmsgModel = ProductUtil.checkSingleProductConfigure(product, productVerify);
						if(!StringUtil.nullToBoolean(product.getIsSoldout()) && StringUtil.nullToBoolean(cmsgModel.getIsSucc())){
							isPaymentSoldout = false;

							// 剩余库存
							int surplusStock = StringUtil.nullToInteger(cmsgModel.getData());
							paymentStockNumber += surplusStock;
							productSpec.setPaymentStockNumber(surplusStock);
							productSpec.setIsPaymentSoldout(false);
						}else{
							// 单规格已售罄
							productSpec.setPaymentStockNumber(0);
							productSpec.setIsPaymentSoldout(true);
						}

						//商品总销量
						salesNumber += StringUtil.nullToInteger(productSpec.getSalesNumber());

						// 秒杀总库存数量
						if(StringUtil.nullToInteger(productSpec.getSeckillTotalStock()) > 0){
							paymentSeckillTotalStock += productSpec.getSeckillTotalStock();
						}

						// 是否大礼包商品
						if(StringUtil.nullToBoolean(product.getIsTeamPackage())){
							productSpec.setPriceRecommend(productSpec.getPriceWholesale());
						}

						//规格商品默认分享利润
						Integer profit = StringUtil.nullToDoubleFormat(productSpec.getPriceRecommend() - productSpec.getPriceWholesale()).intValue();
						// 检查是否秒杀
						if(StringUtil.nullToBoolean(product.getIsSeckillProduct())
								&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())){
							if(StringUtil.nullToInteger(productSpec.getSeckillLimitNumber()) > 0) {
								// 检查是否秒杀开启限购模式
								productSpec.setIsSeckillLimit(true);
							}
							
							//秒杀商品默认分享利润
							profit = StringUtil.nullToDoubleFormat(productSpec.getPriceRecommend() - productSpec.getSeckillPrice()).intValue();
							priceWholesaleSet.add(productSpec.getSeckillPrice());
						}else {
							priceWholesaleSet.add(productSpec.getPriceWholesale());
						}
						productSpec.setRealSellPrice(productSpec.getPriceWholesale());
						productSpec.setProductProfit(profit);
						productSpecList.add(productSpec);
					}

					// 规格综合库存数
					product.setSalesNumber(salesNumber);
					product.setPaymentStockNumber(paymentStockNumber);
					product.setPaymentSeckillTotalStock(paymentSeckillTotalStock);

					// 补规格价格到商品价格中
					if(productSpecList != null && productSpecList.size() > 0){
						Collections.sort(productSpecList, new Comparator<ProductSpec>(){
							@Override
							public int compare(ProductSpec o1, ProductSpec o2) {
								if(StringUtil.nullToInteger(priceWholesaleSet.size()) == 1) {
									//价格相同的情况下，利润倒叙(秒杀商品价格都是秒杀价)
									Double object1 = StringUtil.nullToDouble(o1.getProductProfit());
									Double object2 = StringUtil.nullToDouble(o2.getProductProfit());
									return object1.compareTo(object2);
								}else if(StringUtil.nullToBoolean(product.getIsSeckillProduct())
										&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())) {
									//秒杀商品按照秒杀价排序
									Double object1 = StringUtil.nullToDouble(o1.getSeckillPrice());
									Double object2 = StringUtil.nullToDouble(o2.getSeckillPrice());
									return object1.compareTo(object2);								
								}else {
									//普通商品按照市场价排序
									Double object1 = StringUtil.nullToDouble(o1.getPriceWholesale());
									Double object2 = StringUtil.nullToDouble(o2.getPriceWholesale());
									return object1.compareTo(object2);
								}
							}
						});

						//规格价格到商品价格中
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

				// 多个规格商品已下架
				if(isPaymentSoldout){
					String message = String.format("\"%s\"商品已售罄", StringUtil.null2Str(product.getName()));
					msgModel.setIsSucc(false);
					msgModel.setMessage(message);
					return msgModel;
				}else{
					msgModel.setIsSucc(true);
					return msgModel;
				}
			}else{
				// 检查商品是否有效转换对象
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

				//普通商品价格信息校验
				MsgModel<Integer> cmsgModel = ProductUtil.checkSingleProductConfigure(product, productVerify);
				if(!StringUtil.nullToBoolean(product.getIsSoldout()) && StringUtil.nullToBoolean(cmsgModel.getIsSucc())){
					// 秒杀总库存数量
					int paymentSeckillTotalStock = 0;
					if(StringUtil.nullToInteger(product.getSeckillTotalStock()) > 0){
						paymentSeckillTotalStock += product.getSeckillTotalStock();
					}
					product.setPaymentSeckillTotalStock(paymentSeckillTotalStock);

					// 商品可售卖的库存信息
					product.setPaymentStockNumber(StringUtil.nullToInteger(cmsgModel.getData()));
				}
				return cmsgModel;
			}
		}catch(Exception e){
			log.info("错误配置商品id================"+product.getProductId());
		}

		String message = String.format("\"%s\"商品信息解析错误", StringUtil.null2Str(product.getName()));
		msgModel.setIsSucc(false);
		msgModel.setMessage(message);
		return msgModel;
	}

	/**
	 * 检查商品价格配置信息是否有效
	 * @param wholesale
	 * @param priceCost
	 * @param priceWholesale
	 * @param priceRecommend
	 * @return
	 */
	public static MsgModel<Integer> checkSingleProductConfigure(Product product, ProductVerifyVo productVerify){
		MsgModel<Integer> msgModel = new MsgModel<Integer> ();
		try{
			ProductManager productManager = Constants.ctx.getBean(ProductManager.class);
			ProductSpecManager productSpecManager = Constants.ctx.getBean(ProductSpecManager.class);
			ProductByIdCacheManager productByIdCacheManager = Constants.ctx.getBean(ProductByIdCacheManager.class);

			// 检查商品配置信息是否有效
			boolean isConfigureError = false;
			StringBuffer errorBuffer = new StringBuffer ();
			int stockNumber = StringUtil.nullToInteger(productVerify.getStockNumber());
			Double priceCost = StringUtil.nullToDoubleFormat(productVerify.getPriceCost());
			Double priceRecommend = StringUtil.nullToDoubleFormat(productVerify.getPriceRecommend());
			if(priceCost == null  
					|| priceRecommend == null){
				// 商品成本价格不能为空
				isConfigureError = true;
				errorBuffer.append("商品成本价格不能为空");
			}else if(priceCost.compareTo(new Double(0.001)) <= 0){
				// 商品成本价格不能低于0.001
				isConfigureError = true;
				errorBuffer.append("商品成本价格不能低于0.001");
			}else if(priceRecommend.compareTo(priceCost) < 0) {
				// 售卖价不能小于成本价
				isConfigureError = true;
				errorBuffer.append("售卖价不能小于成本价");
			}
			
			// 打印商品配置错误原因
			if(StringUtil.nullToBoolean(isConfigureError) 
					&& StringUtil.nullToBoolean(product.getStatus())
					&& !StringUtil.nullToBoolean(product.getIsSoldout())) {
				Long productSpecId = StringUtil.nullToLong(productVerify.getProductSpecId());
				log.debug(String.format("商品售罄[productId=%s,productSpecId=%s]%s", product.getProductId(), productSpecId, errorBuffer.toString()));
			}

			// 商品配置信息是否有效
			if(isConfigureError || StringUtil.nullToInteger(stockNumber) <= 0){
				String message = String.format("\"%s\"商品已售罄", StringUtil.null2Str(product.getName()));
				msgModel.setIsSucc(false);
				msgModel.setMessage(message);
				return msgModel;
			}

			// 检查是否秒杀商品(重新计算商品库存信息)
			if(StringUtil.nullToBoolean(product.getIsSeckillProduct())){
				int seckillTotalStock = StringUtil.nullToInteger(productVerify.getSeckillTotalStock());		//秒杀库存数量
				int seckillSalesNumber = StringUtil.nullToInteger(productVerify.getSeckillSalesNumber());	//秒杀商品销量
				int seckillLockNumber = StringUtil.nullToInteger(productVerify.getSeckillLockNumber());		//秒杀锁定库存数量
				Double seckillPrice = StringUtil.nullToDoubleFormat(productVerify.getSeckillPrice());		//秒杀价格
				Double seckillProfit = StringUtil.nullToDoubleFormat(productVerify.getSeckillProfit());		//秒杀利润

				// 检查商品的秒杀状态
				if(StringUtil.nullToBoolean(product.getIsSeckillReadStatus())){
					// 秒杀即将开始
					if(seckillTotalStock <= 0){
						//秒杀库存数量<=0错误
						seckillTotalStock = 0;
					}else if(seckillSalesNumber < 0){
						//秒杀商品销量<0
						seckillSalesNumber = 0;
					}else if(seckillLockNumber < 0){
						//秒杀锁定库存数量<0
						seckillLockNumber = 0;
					}

					// 检查秒杀剩余库存数量是否有效
					int surplusStock = seckillTotalStock - (seckillSalesNumber + seckillLockNumber);	
					if(surplusStock <= 0){
						String message = String.format("\"%s\"秒杀库存信息错误", StringUtil.null2Str(product.getName()));
						msgModel.setIsSucc(false);
						msgModel.setMessage(message);
						return msgModel;
					}

					// 普通剩余库存数
					//stockNumber = stockNumber - surplusStock;
				}else {
					// 已开始的秒杀
					boolean isConfigureSeckillError = false;
					if(seckillTotalStock <= 0){
						//秒杀库存数量<=0错误
						isConfigureSeckillError = true;
					}else if(seckillSalesNumber < 0){
						//秒杀商品销量<0
						isConfigureSeckillError = true;
					}else if(seckillLockNumber < 0){
						//秒杀锁定库存数量<0
						isConfigureSeckillError = true;
					}else if(seckillPrice.compareTo(new Double(0.001)) <= 0){
						// 秒杀商品价格不能低于0.001
						isConfigureSeckillError = true;
					}else if(seckillProfit.compareTo(new Double(0.0)) < 0){
						// 秒杀商品利润不能大于秒杀商品价格
						isConfigureSeckillError = true;
					}

					// 秒杀库存信息错误
					if(isConfigureSeckillError){
						String message = String.format("\"%s\"秒杀库存信息错误", StringUtil.null2Str(product.getName()));
						msgModel.setIsSucc(false);
						msgModel.setMessage(message);
						return msgModel;
					}

					// 检查秒杀剩余库存数量是否有效
					int surplusStock = seckillTotalStock - (seckillSalesNumber + seckillLockNumber);	
					if(surplusStock <= 0){
						String message = String.format("\"%s\"秒杀库存信息错误", StringUtil.null2Str(product.getName()));
						msgModel.setIsSucc(false);
						msgModel.setMessage(message);
						return msgModel;
					}

					if(stockNumber < surplusStock) {
						if(StringUtil.nullToBoolean(product.getIsSpceProduct())) {
							//规格商品
							productSpecManager.updateProductSeckillTotalNumber(productVerify.getProductSpecId(), stockNumber);
						}else {
							// 组合商品更新下架状态商品
							productManager.updateProductSeckillTotalNumber(product.getProductId(), stockNumber);
						}
						try{
							productByIdCacheManager.removeSession(product.getProductId());
						}catch(Exception e){
							e.printStackTrace();
						}
					}else {
						// 秒杀剩余库存数
						stockNumber = surplusStock;
					}
				}
			}

			// 商品正常分销状态
			msgModel.setData(stockNumber);
			msgModel.setIsSucc(true);
			return msgModel;
		}catch(Exception e){
			e.printStackTrace();
		}

		String message = String.format("\"%s\"商品信息解析错误", StringUtil.null2Str(product.getName()));
		msgModel.setIsSucc(false);
		msgModel.setMessage(message);
		return msgModel;
	}

	/**
	 * 支持关键字模糊匹配，根据商品标签
	 * @param name
	 * @param keyword
	 * @param isVague
	 * @return
	 */
	public static MsgModel<Long> getKeywordFuzzyMatchByTagNames(String keyword, String productName, String specTagNames, List<String> tagNameList){
		MsgModel<Long> msgModel = new MsgModel<Long> ();
		try{
			// 关键字搜索,支持多规则匹配
			List<String> keywordList = IKUtil.getKeywordList(keyword,null);
			if(keywordList != null && keywordList.size() > 0){
				// 根据品牌标签|分类标签搜索
				if(tagNameList != null && tagNameList.size() > 0){
					for(String strKey : keywordList){
						//去掉空格字符串
						strKey = StringUtil.null2Str(strKey).replaceAll("\\s+", "").toUpperCase();
						for(String tagName : tagNameList){
							tagName = StringUtil.null2Str(tagName).replaceAll("\\s+", "").toUpperCase();
							if(StringUtil.compareObject(tagName, strKey)){
								msgModel.setIsSucc(true);
								return msgModel;
							}
						}
					}
				}

				// 根据规格标签搜索
				if(!StringUtil.isNull(specTagNames)){
					specTagNames = StringUtil.null2Str(specTagNames).toUpperCase();
					List<String> specTagNameArray = StringUtil.strToStrList(StringUtil.null2Str(specTagNames), ",");
					if(specTagNameArray != null && specTagNameArray.size() > 0){
						for(String strKey : keywordList){
							//去掉空格字符串
							strKey = StringUtil.null2Str(strKey).replaceAll("\\s+", "").toUpperCase();
							for(String specTagName : specTagNameArray){
								specTagName = StringUtil.null2Str(specTagName).replaceAll("\\s+", "").toUpperCase();
								if(StringUtil.null2Str(specTagName).contains(strKey)){
									msgModel.setIsSucc(true);
									return msgModel;
								}
							}
						}
					}
				}

				// 商品名称全包含关键字
				keyword = StringUtil.null2Str(keyword).replaceAll("\\s+", "").toUpperCase();
				if(StringUtil.null2Str(productName).toUpperCase().contains(keyword)){
					msgModel.setIsSucc(true);
					return msgModel;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		msgModel.setIsSucc(false);
		return msgModel;
	}

	/**
	 * 支持关键字模糊匹配，根据商品标签
	 * @param name
	 * @param keyword
	 * @param isVague
	 * @return
	 */
	public static MsgModel<Long> getKeywordFuzzyMatchByName(String keyword, String name){
		MsgModel<Long> msgModel = new MsgModel<Long> ();
		try{
			// 关键字搜索,支持多规则匹配
			List<String> keywordList = IKUtil.getKeywordList(keyword,null);
			if(keywordList != null && keywordList.size() > 0){
				for(String strKey : keywordList){
					//去掉空格字符串
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
	 * 检查订单商品列表是否有效
	 * @param orderItemsList
	 * @return
	 */
	public static MsgModel<Integer> checkOrderItems(List<OrderItems> orderItemsList){
		MsgModel<Integer> msgModel = new MsgModel<Integer> ();
		if(orderItemsList == null || orderItemsList.size() <= 0){
			msgModel.setIsSucc(false);
			msgModel.setMessage("订单商品列表为空");
			return msgModel;
		}

		try{
			// 检查订单的每个商品信息是否有效
			Map<Long, OrderItems> productIdOrderItemsMap = new HashMap<Long, OrderItems> ();
			for(OrderItems orderItems : orderItemsList){
				productIdOrderItemsMap.put(orderItems.getProductId(), orderItems);
			}

			// 查找批发市场是否存在对应商品
			ProductManager productManager = Constants.ctx.getBean(ProductManager.class);
			List<Product> productList = productManager.getProductListByProudctIdList(StringUtil.longSetToList(productIdOrderItemsMap.keySet()));
			if(productList == null || productList.size() <= 0){
				msgModel.setIsSucc(false);
				msgModel.setMessage("订单异常,商品已售罄");
				return msgModel;
			}

			// list对象转换Map对象
			Map<Long, Product> productMap = new HashMap<Long, Product> ();
			for(Product product : productList){
				productMap.put(product.getProductId(), product);
			}

			// 检查单个商品是否有下架是否有库存
			for(Entry<Long, OrderItems> entry : productIdOrderItemsMap.entrySet()){
				Long productId = entry.getKey();
				OrderItems orderItems = entry.getValue();
				log.debug(String.format("[productId=%s, orderItems=%s]", productId, StringUtil.objectToJSON(orderItems)));

				// 检查商品是否已下架
				if(!StringUtil.compareObject(StringUtil.nullToLong(productId), StringUtil.nullToLong(orderItems.getProductId()))){
					//批发市场商品是否已下架
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"商品已售罄", StringUtil.null2Str(orderItems.getProductName())));
					return msgModel;
				}else if(!productMap.containsKey(productId)){
					//批发市场商品是否已下架
					msgModel.setIsSucc(false);
					msgModel.setMessage(String.format("\"%s\"商品已售罄", StringUtil.null2Str(orderItems.getProductName())));
					return msgModel;
				}

				Product dbProudct = productMap.get(entry.getKey());
				log.debug(String.format("[productId=%s, dbProudct=%s]", productId, StringUtil.objectToJSON(dbProudct)));

				// 检查商品信息是否有效
				MsgModel<Integer> xmsgModel = ProductUtil.checkProductConfigure(dbProudct);
				if(!StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
					msgModel.setIsSucc(false);
					msgModel.setMessage(xmsgModel.getMessage());
					return msgModel;
				}

				// 数据库商品库存数量
				int stockNumber = StringUtil.nullToInteger(dbProudct.getStockNumber());
				int seckillTotalStock = StringUtil.nullToInteger(dbProudct.getSeckillTotalStock());
				int seckillSalesNumber = StringUtil.nullToInteger(dbProudct.getSeckillSalesNumber());
				if(StringUtil.nullToBoolean(orderItems.getIsSpceProduct())){
					stockNumber = 0;
					seckillTotalStock = 0;
					seckillSalesNumber = 0;
					MsgModel<ProductSpec> pmsgModel = ProductUtil.checkExistProductSpecByProductSpecId(dbProudct, orderItems.getProductSpecId());
					if(StringUtil.nullToBoolean(pmsgModel.getIsSucc())){
						// 规格商品的库存数信息
						stockNumber = StringUtil.nullToInteger(pmsgModel.getData().getStockNumber());
						seckillTotalStock = StringUtil.nullToInteger(pmsgModel.getData().getSeckillTotalStock());
						seckillSalesNumber = StringUtil.nullToInteger(pmsgModel.getData().getSeckillSalesNumber());
					}
				}

				// 检查是否秒杀订单
				if(StringUtil.nullToBoolean(orderItems.getIsSeckillProduct())){
					// 秒杀商品
					seckillSalesNumber += orderItems.getQuantity();
					if(seckillTotalStock < seckillSalesNumber){
						// 可买商品大于秒杀总库存数
						msgModel.setIsSucc(false);
						msgModel.setMessage(String.format("\"%s\"商品库存不足", orderItems.getProductName()));
						if(StringUtil.nullToBoolean(dbProudct.getIsSpceProduct())){
							msgModel.setMessage(String.format("\"%s\",\"%s\"商品库存不足", orderItems.getProductName(), orderItems.getProductTags()));
						}
					}else if(stockNumber < seckillTotalStock){
						// 秒杀总库存数大于商品的总库存数
						msgModel.setIsSucc(false);
						msgModel.setMessage(String.format("\"%s\"商品库存不足", orderItems.getProductName()));
						if(StringUtil.nullToBoolean(dbProudct.getIsSpceProduct())){
							msgModel.setMessage(String.format("\"%s\",\"%s\"商品库存不足", orderItems.getProductName(), orderItems.getProductTags()));
						}
					}
				}else{
					// 检查是否即将开始的秒杀商品
					if(StringUtil.nullToBoolean(dbProudct.getIsSeckillReadStatus())){
						seckillTotalStock += orderItems.getQuantity();
						if(stockNumber < seckillTotalStock){
							// 可买商品小于秒杀总库存数
							msgModel.setIsSucc(false);
							msgModel.setMessage(String.format("\"%s\"商品库存不足", orderItems.getProductName()));
							if(StringUtil.nullToBoolean(dbProudct.getIsSpceProduct())){
								msgModel.setMessage(String.format("\"%s\",\"%s\"商品库存不足", orderItems.getProductName(), orderItems.getProductTags()));
							}
						}
					}else if(StringUtil.nullToInteger(orderItems.getQuantity()) > stockNumber){
						msgModel.setIsSucc(false);
						msgModel.setMessage(String.format("\"%s\"商品库存不足", orderItems.getProductName()));
						if(StringUtil.nullToBoolean(dbProudct.getIsSpceProduct())){
							msgModel.setMessage(String.format("\"%s\",\"%s\"商品库存不足", orderItems.getProductName(), orderItems.getProductTags()));
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
		msgModel.setMessage("订单异常");
		return msgModel;
	}


	/**
	 * 获取商品的问答列表
	 * @param productId 商品市场id
	 * @param isProdcutDetail	是否来至商品详情页
	 * @param userId	用户id
	 * @return
	 */
	public static List<ProductQuestion> getProductQuestion(Long productId, Long userId){
		List<ProductQuestion> questionList = new ArrayList<ProductQuestion>();
		ProductQuestionListByProductIdCacheManager productQuestionListByProductIdCacheManager = Constants.ctx.getBean(ProductQuestionListByProductIdCacheManager.class);

		//获取问题列表
		Map<String, ProductQuestion> questionMap = productQuestionListByProductIdCacheManager.getSession(productId);
		if (questionMap == null || questionMap.size() <= 0){
			//商品没有问答
			return null;
		}

		//创建时间排序
		List<Map.Entry<String, ProductQuestion>> mappingList = new ArrayList<Map.Entry<String, ProductQuestion>> (questionMap.entrySet());
		Collections.sort(mappingList, new Comparator<Map.Entry<String, ProductQuestion>>(){
			public int compare(Map.Entry<String, ProductQuestion> obj1, Map.Entry<String, ProductQuestion> obj2){
				ProductQuestion question1 = obj1.getValue();
				ProductQuestion question2 = obj2.getValue();
				Long time1 = (question1 == null || question1.getCreateTime() == null) ? 0L : question1.getCreateTime().getTime();
				Long time2 = (question2 == null || question2.getCreateTime() == null) ? 1L : question2.getCreateTime().getTime();
				return (time1.longValue() < time2.longValue()) ? 1 : -1;
			}
		}); 

		for(Map.Entry<String, ProductQuestion> entry : mappingList){
			ProductQuestion question = entry.getValue();
			if(question != null 
					&& question.getQuestionId() != null  
					&& !StringUtil.nullToBoolean(question.getIsDelete())){
				//如果是自己的提问或者审核通的提问需要显示
				if(StringUtil.compareObject(question.getUserId(), userId) || StringUtil.nullToBoolean(question.getStatus())){
					questionList.add(question);
					//若果是商品详情页则取最新的一条有效提问
				}
			}
		}
		return questionList;
	}

	/**
	 * 获取问题回答列表
	 * @param questionId
	 * @param isProdcutDetail
	 * @return
	 */
	public static List<ProductAnswer> getAnswerListByQuestionId(Long questionId, Long userId){
		List<ProductAnswer> answerList = new ArrayList<ProductAnswer>();
		try{
			ProductAnswerListByQuestionIdCacheManager productAnswerListByQuestionIdCacheManager = Constants.ctx.getBean(ProductAnswerListByQuestionIdCacheManager.class);
			//更新缓存
			productAnswerListByQuestionIdCacheManager.removeSession(questionId);
			//获取答案列表
			Map<String, ProductAnswer> answerMap = productAnswerListByQuestionIdCacheManager.getSession(questionId);
			if (answerMap == null || answerMap.size() == 0){
				//商品没有问答
				return null;
			}

			//排序
			List<Map.Entry<String, ProductAnswer>> mappingList = new ArrayList<Map.Entry<String, ProductAnswer>> (answerMap.entrySet());
			Collections.sort(mappingList, new Comparator<Map.Entry<String, ProductAnswer>>(){
				public int compare(Map.Entry<String, ProductAnswer> obj1, Map.Entry<String, ProductAnswer> obj2){
					ProductAnswer answer1 = obj1.getValue();
					ProductAnswer answer2 = obj2.getValue();
					Long time1 = (answer1 == null || answer1.getCreateTime() == null) ? 0L : answer1.getCreateTime().getTime();
					Long time2 = (answer2 == null || answer2.getCreateTime() == null) ? 1L : answer2.getCreateTime().getTime();
					return (time1.longValue() < time2.longValue()) ? 1 : -1;
				}
			}); 

			for(Map.Entry<String, ProductAnswer> entry : mappingList){
				ProductAnswer answer = entry.getValue();
				if(answer != null 
						&& answer.getAnswerId() != null 
						&& !StringUtil.nullToBoolean(answer.getIsDelete())){
					//如果是自己的回答或者审核通的回答需要显示
					if(StringUtil.compareObject(answer.getUserId(), userId) || StringUtil.nullToBoolean(answer.getStatus())){
						answerList.add(answer);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return answerList;
	}

	/**
	 * 保存搜索关键词
	 * @param keyword
	 * @return
	 */
	public static void recordKeywords(final String keyword){
		try {
			BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
				@Override
				public void run() {
					try{
						KeywordsManager keywordsManager = Constants.ctx.getBean(KeywordsManager.class);
						List<Keywords> keywordsList = keywordsManager.getAll();
						//若数据库已有，搜索次数加1
						if (keywordsList != null && keywordsList.size() > 0) {
							for (Keywords keywords : keywordsList) {
								if (Objects.equals(keywords.getName(), keyword)) {
									keywords.setSeekCount(keywords.getSeekCount() + 1);
									keywordsManager.update(keywords);
									return;
								}
							}
						}
						//若数据库没有，创建新关键词
						Keywords words = new Keywords();
						words.setName(keyword);
						words.setSeekCount(1);
						words.setIsDefault(false);
						words.setCreateTime(DateUtil.getCurrentDate());
						words.setUpdateTime(words.getCreateTime());
						keywordsManager.save(words);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 商品banner图片列表
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


	/**
	 * 商品搜索
	 */
	public static MsgModel<Long> getProductByKeyword(String keyword, Product product){
		MsgModel<Long> msgModel = new MsgModel<Long>();
		try {
			TagModelListCacheManager tagModelListCacheManager = Constants.ctx.getBean(TagModelListCacheManager.class);

			//商品自定义标签
			List<Long> productTagIdList = StringUtil.stringToLongArray(product.getTagIds());
			List<TagModel> tagModelList = tagModelListCacheManager.getSession();
			//从数据库中加载自定义单词
			Set<String> wordsSet = new HashSet<String>();
			if(tagModelList != null && tagModelList.size() > 0 ){
				for(TagModel tagModel : tagModelList) {
					wordsSet.add(StringUtil.null2Str(tagModel.getName()));
				}
			}

			// 关键字搜索,支持多规则匹配
			List<String> keywordList = IKUtil.getKeywordList(keyword, wordsSet);
			if (keywordList != null && keywordList.size() > 0) {
				Set<String> keywordSet = new HashSet<String>();
				for (String keyStr : keywordList) {
					keywordSet.add(keyStr);
				}

				Long brandId = 0L;
				Long categoryId = 0L;
				int brandTagNumber = 0;
				int categoryTagNumber = 0;
				int productTagNumber = 0;
				Set<String> productNameSet = new HashSet<String>();
				if (tagModelList != null && tagModelList.size() > 0) {
					for (String strKey : keywordSet) {
						boolean isContainStrKey = false;
						for (TagModel tagModel : tagModelList) {
							if (StringUtil.compareObject(strKey, StringUtil.null2Str(tagModel.getName()))) {
								isContainStrKey = true;
								if(productTagIdList != null && productTagIdList.contains(tagModel.getTagId())) {
									//商品自定义标签
									productTagNumber++;
								}else if (StringUtil.compareObject(StringUtil.nullToInteger(tagModel.getTagType()),
										TagModel.BRAND_TAG_TYPE)) {
									// 统计关键字中品牌标签出现的次数
									brandTagNumber++;
									brandId = StringUtil.nullToLong(tagModel.getObjectId());
								} else if (StringUtil.compareObject(StringUtil.nullToInteger(tagModel.getTagType()),
										TagModel.CATEGORY_TAG_TYPE)) {
									// 统计关键字中分类标签出现的次数
									categoryTagNumber++;
									categoryId = StringUtil.nullToLong(tagModel.getObjectId());
								}
							}
						}
						if (!isContainStrKey) {
							productNameSet.add(strKey);
						}
					}
				}

				if(productTagNumber >= 2) {
					//同一个商品标签出现2次及以上
					msgModel.setIsSucc(true);
					return msgModel;
				}else if(productTagNumber == 1) {
					if(brandTagNumber == 0 && categoryTagNumber == 0) {
						msgModel.setIsSucc(true);
						return msgModel;
					}else if(brandTagNumber == 1 && StringUtil.compareObject(product.getBrandId(), brandId)) {
						msgModel.setIsSucc(true);
						return msgModel;
					}else if(categoryTagNumber == 1 &&(product.getCategoryFidList().contains(categoryId)
							|| product.getCategoryIdList().contains(categoryId))) {
						msgModel.setIsSucc(true);
						return msgModel;
					}
				}else if (brandTagNumber >= 2 || categoryTagNumber >= 2 || (brandTagNumber == 0 && categoryTagNumber == 0)) {
					// 商品名称模糊搜索
					for (String strKey : keywordSet) {
						if (StringUtil.null2Str(product.getName()).toUpperCase().contains(strKey)) {
							//去掉空格字符串
							strKey = StringUtil.null2Str(strKey).replaceAll("\\s+", "").toUpperCase();
							if (StringUtil.null2Str(product.getName()).replaceAll("\\s+", "").toUpperCase().contains(strKey)) {
								msgModel.setIsSucc(true);
								return msgModel;
							}
						}
					}
				} else if (brandTagNumber == 1 && categoryTagNumber == 1) {
					if(StringUtil.compareObject(StringUtil.nullToLong(product.getBrandId()), brandId) && (
							product.getCategoryFidList().contains(categoryId)
							|| product.getCategoryIdList().contains(categoryId))) {
						if (productNameSet != null && productNameSet.size() > 0) {
							// 有多余的名字
							for (String name : productNameSet) {
								//去掉空格字符串
								name = StringUtil.null2Str(name).replaceAll("\\s+", "").toUpperCase();
								if (StringUtil.null2Str(product.getName()).replaceAll("\\s+", "").toUpperCase().contains(name)) {
									msgModel.setIsSucc(true);
									return msgModel;
								}
							}
						}else {
							// 品牌和分类的交集
							msgModel.setIsSucc(true);
							return msgModel;
						}
					}

				} else if (brandTagNumber == 1 ) {
					// 品牌标签出现一次
					if (StringUtil.compareObject(StringUtil.nullToLong(product.getBrandId()), brandId)) {
						if (productNameSet != null && productNameSet.size() > 0) {
							// 有多余的名字
							for (String name : productNameSet) {
								//去掉空格字符串
								name = StringUtil.null2Str(name).replaceAll("\\s+", "").toUpperCase();
								if (StringUtil.null2Str(product.getName()).replaceAll("\\s+", "").toUpperCase().contains(name)) {
									msgModel.setIsSucc(true);
									return msgModel;
								}
							}
						}else {
							//没有标签之外的名字，则取此品牌或分类下名称
							msgModel.setIsSucc(true);
							return msgModel;
						}
					}
				}else if(categoryTagNumber == 1) {
					// 品牌标签出现一次
					if (product.getCategoryFidList().contains(categoryId)
							|| product.getCategoryIdList().contains(categoryId)) {
						if (productNameSet != null && productNameSet.size() > 0) {
							// 有多余的名字
							for (String name : productNameSet) {
								//去掉空格字符串
								name = StringUtil.null2Str(name).replaceAll("\\s+", "").toUpperCase();
								if (StringUtil.null2Str(product.getName()).replaceAll("\\s+", "").toUpperCase().contains(name)) {
									msgModel.setIsSucc(true);
									return msgModel;
								}
							}
						}else {
							//没有标签之外的名字，则取此品牌或分类下名称
							msgModel.setIsSucc(true);
							return msgModel;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{

		}

		msgModel.setIsSucc(false);
		return msgModel;
	}



	/**
	 * 根据用户等级获取商品获取商品真正价格
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
				//不属于高等级用户直接返回不用处理
				return;
			}else if(StringUtil.nullToBoolean(product.getIsGroupProduct())) {
				//组合商品不处理
				return;
			}

			if(StringUtil.compareObject(level, UserLevel.USER_LEVEL_V2)) {
				//v2用户
				if(StringUtil.nullToBoolean(product.getIsOpenV2Price())) {
					//商品开启了v2价
					if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
						if(product.getProductSpecList() != null && product.getProductSpecList().size() > 0){
							Set<Double> v2PriceSet = new HashSet<Double>();
							List<ProductSpec> productSpecList = product.getProductSpecList();
							for(ProductSpec productSpec : productSpecList) {
								v2PriceSet.add(productSpec.getV2Price());
								productSpec.setRealSellPrice(productSpec.getV2Price());
								if(!(StringUtil.nullToBoolean(product.getIsSeckillProduct())
										&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus()))) {
									//非秒杀中的商品，利润重新计算
									Integer profit = StringUtil.nullToDoubleFormat(productSpec.getPriceRecommend() - productSpec.getV2Price()).intValue();
									productSpec.setProductProfit(profit);


								}
							}

							//重新排序
							Collections.sort(productSpecList, new Comparator<ProductSpec>(){
								@Override
								public int compare(ProductSpec o1, ProductSpec o2) {
									if(StringUtil.nullToBoolean(product.getIsSeckillProduct())) {
										//秒杀价格
										Double object1 = StringUtil.nullToDouble(o1.getSeckillPrice());
										Double object2 = StringUtil.nullToDouble(o2.getSeckillPrice());
										return object1.compareTo(object2);
									}else if(StringUtil.nullToInteger(v2PriceSet.size()) == 1) {
										//价格相同的情况下，利润倒叙
										Double object1 = StringUtil.nullToDouble(o1.getProductProfit());
										Double object2 = StringUtil.nullToDouble(o2.getProductProfit());
										return -object1.compareTo(object2);
									}else {
										//普通商品按照v2价排序
										Double object1 = StringUtil.nullToDouble(o1.getV2Price());
										Double object2 = StringUtil.nullToDouble(o2.getV2Price());
										return object1.compareTo(object2);
									}
								}
							});

							//规格价格到商品价格中
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
					product.setCommodityPriceTag("V2专享价");
				}
			}else if(StringUtil.compareObject(level, UserLevel.USER_LEVEL_V3)) {
				if(StringUtil.nullToBoolean(product.getIsOpenV3Price())) {
					//商品开启了v3价
					if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
						if(product.getProductSpecList() != null && product.getProductSpecList().size() > 0){
							Set<Double> v3PriceSet = new HashSet<Double>();
							List<ProductSpec> productSpecList = product.getProductSpecList();
							for(ProductSpec productSpec : productSpecList) {
								v3PriceSet.add(productSpec.getV3Price());
								productSpec.setRealSellPrice(productSpec.getV3Price());
								if(!(StringUtil.nullToBoolean(product.getIsSeckillProduct())
										&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus()))) {
									//非秒杀中的商品，利润重新计算
									Integer profit = StringUtil.nullToDoubleFormat(productSpec.getPriceRecommend() - productSpec.getV3Price()).intValue();
									productSpec.setProductProfit(profit);

								}
							}

							Collections.sort(productSpecList, new Comparator<ProductSpec>(){
								@Override
								public int compare(ProductSpec o1, ProductSpec o2) {
									if(StringUtil.nullToBoolean(product.getIsSeckillProduct())) {
										//秒杀价格
										Double object1 = StringUtil.nullToDouble(o1.getSeckillPrice());
										Double object2 = StringUtil.nullToDouble(o2.getSeckillPrice());
										return object1.compareTo(object2);
									}else if(StringUtil.nullToInteger(v3PriceSet.size()) == 1) {
										//价格相同的情况下，利润倒叙(秒杀商品价格都是秒杀价)
										Double object1 = StringUtil.nullToDouble(o1.getProductProfit());
										Double object2 = StringUtil.nullToDouble(o2.getProductProfit());
										return -object1.compareTo(object2);
									}else {
										//普通商品按照v3价排序
										Double object1 = StringUtil.nullToDouble(o1.getV3Price());
										Double object2 = StringUtil.nullToDouble(o2.getV3Price());
										return object1.compareTo(object2);
									}
								}
							});

							//规格价格到商品价格中
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
					product.setCommodityPriceTag("V3专享价");
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置商品排序权重
	 * @param product
	 */
	public static void getProductSortWeight(Map<Long, Product> productMap) {
		try {
			if(productMap != null && productMap.size() > 0) {
				// 销量排序
				List<Map.Entry<Long, Product>> mappingList = new ArrayList<Map.Entry<Long, Product>>(
						productMap.entrySet());
				Collections.sort(mappingList, new Comparator<Map.Entry<Long, Product>>() {
					public int compare(Entry<Long, Product> o1, Entry<Long, Product> o2) {
						try {
							Product productObj1 = o1.getValue();
							Product productObj2 = o2.getValue();
							int salesNumber1 = StringUtil.nullToInteger(productObj1.getSalesNumber());
							int salesNumber2 = StringUtil.nullToInteger(productObj2.getSalesNumber());

							// 平均月销
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
	 * 规整商品价格区间
	 * @param product
	 * @param userInfo
	 */
	public static void orderProductPriceRegion(Product product, UserInfo userInfo,Boolean isShareProduct) {
		try {
			if(!StringUtil.nullToBoolean(product.getIsGroupProduct())) {
				// 规格商品价格区间
				if (StringUtil.nullToBoolean(product.getIsSpceProduct())) {

					if (product.getProductSpecList() != null && product.getProductSpecList().size() > 0) {
						try {
							// 普通用户
							List<Integer> commomLevelList = new ArrayList<Integer> ();
							commomLevelList.add(UserLevel.USER_LEVEL_COMMON);	//普通用户
							commomLevelList.add(UserLevel.USER_LEVEL_BUYERS);	//VIP用户
							// 排序
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

							// 最低规格价格到商品价格中
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
							// 秒杀价格区间
							if (StringUtil.nullToBoolean(product.getIsSeckillProduct())) {
								//秒杀原价区间
								product.setPaymentOriginalPriceRegion(String.format("%s~%s", StringUtil.nullToDoubleFormat(minPaymentPrice),
										StringUtil.nullToDoubleFormat(maxPaymentPrice)));
								if (StringUtil.nullToDoubleFormat(minPaymentPrice).compareTo(StringUtil.nullToDoubleFormat(maxPaymentPrice)) == 0) {
									product.setPaymentOriginalPriceRegion(StringUtil.null2Str(StringUtil.nullToDoubleFormat(minPaymentPrice)));
								}
								// 排序
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
									// 开抢中
									minPaymentPrice = StringUtil.nullToDoubleFormat(minSeckillPrice);
									maxPaymentPrice = StringUtil.nullToDoubleFormat(maxSeckillPrice);
								} 
								product.setSeckillPriceRegion(String.format("%s~%s", StringUtil.nullToDoubleFormat(minSeckillPrice), StringUtil.nullToDoubleFormat(maxSeckillPrice)));
								if (StringUtil.nullToDoubleFormat(minSeckillPrice)
										.compareTo(StringUtil.nullToDoubleFormat(maxSeckillPrice)) == 0) {
									product.setSeckillPriceRegion(StringUtil.nullToDoubleFormatStr(minSeckillPrice));
								}
							}

							//真正购买价格区间（秒杀、分享、app）
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
								// 税费区间
								product.setPaymentTaxRegion(String.format("%s~%s", StringUtil.nullToDoubleFormat(minTax), StringUtil.nullToDoubleFormat(maxTax)));
								if (minTax.compareTo(maxTax) == 0) {
									product.setPaymentTaxRegion(StringUtil.nullToDoubleFormatStr(minTax));
								}
							}

							// 利润排序
							Collections.sort(product.getProductSpecList(), new Comparator<ProductSpec>() {
								@Override
								public int compare(ProductSpec o1, ProductSpec o2) {
									Double profit1 = StringUtil.nullToDoubleFormat(o1.getProductProfit());
									Double profit2 = StringUtil.nullToDoubleFormat(o2.getProductProfit());
									return profit1.compareTo(profit2);
								}
							});

							// 利润区间
							ProductSpec spec = product.getProductSpecList().get(0);
							Integer minProductProfit = StringUtil.nullToInteger(spec.getProductProfit());
							Integer maxProductProfit = StringUtil.nullToInteger(product.getProductSpecList()
									.get(product.getProductSpecList().size() - 1).getProductProfit());
							product.setProfitRegion(String.format("%s~%s", minProductProfit, maxProductProfit));
							if (minProductProfit.compareTo(maxProductProfit) == 0) {
								product.setProfitRegion(StringUtil.null2Str(minProductProfit));
							}

							// 售价排序
							Collections.sort(product.getProductSpecList(), new Comparator<ProductSpec>() {
								@Override
								public int compare(ProductSpec o1, ProductSpec o2) {
									Double price1 = StringUtil.nullToDoubleFormat(o1.getRealSellPrice());
									Double price2 = StringUtil.nullToDoubleFormat(o2.getRealSellPrice());
									if(StringUtil.nullToBoolean(product.getIsSeckillProduct()) && !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())) {
										//秒杀商品
										price1 = StringUtil.nullToDouble(o1.getSeckillPrice());        //秒杀价
										price2 = StringUtil.nullToDouble(o2.getSeckillPrice());        //秒杀价
									}
									Double sellPrice1 = StringUtil.nullToDoubleFormat(price1 + StringUtil.nullToDoubleFormat(o1.getProductProfit()));
									Double sellPrice2 = StringUtil.nullToDoubleFormat(price2 + StringUtil.nullToDoubleFormat(o2.getProductProfit()));
									return sellPrice1.compareTo(sellPrice2);
								}
							});
							// 含税售价区间
							ProductSpec minProductSpec = product.getProductSpecList().get(0);
							ProductSpec maxProductSpec = product.getProductSpecList().get(product.getProductSpecList().size() - 1);

							Double minSellPrice = DoubleUtil.add(StringUtil.nullToDouble(minProductSpec.getRealSellPrice()),  StringUtil.nullToDouble(minProductSpec.getProductProfit()));
							Double maxSellPrice = DoubleUtil.add(StringUtil.nullToDouble(maxProductSpec.getRealSellPrice()),StringUtil.nullToDouble(maxProductSpec.getProductProfit()));
							if(StringUtil.nullToBoolean(product.getIsSeckillProduct()) && !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())) {
								//秒杀商品

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
						//分享商品规格价格调整
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
					//普通商品
					product.setPaymentPriceRegion(StringUtil.nullToDoubleFormatStr(product.getPaymentPrice()));
					product.setPaymentOriginalPriceRegion(StringUtil.nullToDoubleFormatStr((product.getPaymentOriginalPrice())));
					product.setSeckillPriceRegion(StringUtil.nullToDoubleFormatStr((product.getSeckillPrice())));
					product.setMinPaymentPrice(StringUtil.nullToDoubleFormat(product.getPaymentPrice()));
					product.setMaxPaymentPrice(StringUtil.nullToDoubleFormat(product.getPaymentPrice()));
					product.setProfitRegion(StringUtil.null2Str(product.getProductProfit()));
					product.setPaymentTaxRegion(StringUtil.nullToDoubleFormatStr((product.getTax())));

					Double sellPrice = DoubleUtil.add(product.getRealSellPrice(), StringUtil.nullToDouble(product.getProductProfit()));
					if(StringUtil.nullToBoolean(product.getIsSeckillProduct()) && !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())) {
						//秒杀商品
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
				//分享商品价格为售价
				product.setPaymentOriginalPriceRegion(product.getPaymentPriceRegion());
				product.setPaymentPriceRegion(product.getSellPriceRegion());
				if(StringUtil.nullToBoolean(product.getIsGroupProduct())) {
					product.setPaymentGroupPriceRegion(product.getSellPriceRegion());
				}
				log.info(String.format("分享页面价格，productId=%s,价格=%s", product.getProductId(),product.getPaymentPriceRegion()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 用户可购买单商品数量
	 * @param product
	 * @param remainNumber  剩余可购库存
	 */
	public static void setProductStockNumber(Product product, int remainNumber) {
		if(remainNumber >= 0) {
			if(!StringUtil.nullToBoolean(product.getIsSpceProduct())) {
				// 商品库存大于限购库存,设置当前用户的库存就是限购库存
				if(remainNumber < StringUtil.nullToInteger(product.getPaymentStockNumber())) {
					product.setPaymentStockNumber(remainNumber);
				}
				
				if(StringUtil.nullToBoolean(product.getIsSeckillProduct())
						&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())
						&& StringUtil.nullToBoolean(product.getIsSeckillLimit())) {
					//秒杀商品
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
							//秒杀商品
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
	 * 检查是否充值赠品
	 * @param productId
	 * @param userInfo
	 * @return
	 */
	public static void checkIsRechargeGiftProduct(Product product,UserInfo userInfo){
		try {
			RechargeTemplateListCacheManager rechargeTemplateListCacheManager = Constants.ctx.getBean(RechargeTemplateListCacheManager.class);

			List<Integer> userLevelList = new ArrayList<Integer>();
			userLevelList.add(UserLevel.USER_LEVEL_DEALER);
			userLevelList.add(UserLevel.USER_LEVEL_AGENT);
			userLevelList.add(UserLevel.USER_LEVEL_V2);
			userLevelList.add(UserLevel.USER_LEVEL_V3);
			Map<String, RechargeTemplate> rechargeTemplateMap = rechargeTemplateListCacheManager.getSession();
			if(rechargeTemplateMap != null && !rechargeTemplateMap.isEmpty()) {
				for(Map.Entry<String, RechargeTemplate> entry : rechargeTemplateMap.entrySet()) {
					RechargeTemplate rechargeTemplate = entry.getValue();
					if(rechargeTemplate != null && rechargeTemplate.getTemplateId() != null
							&& StringUtil.nullToBoolean(rechargeTemplate.getIsEnable())
							&& !StringUtil.nullToBoolean(rechargeTemplate.getIsDelete())
							&& StringUtil.nullToDouble(rechargeTemplate.getAmount()).compareTo(0D) > 0
							&& StringUtil.compareObject(StringUtil.nullToInteger(rechargeTemplate.getType()), RechargeTemplate.RECHARGE_TEMPLATE_TYPE_PRODUCT)
							&& StringUtil.compareObject(StringUtil.nullToLong(rechargeTemplate.getProductId()), StringUtil.nullToLong(product.getProductId()))) {
						Integer userLevel = StringUtil.nullToInteger(rechargeTemplate.getUserLevel());
						Integer level = StringUtil.nullToInteger(userInfo.getLevel());
						if(StringUtil.compareObject(userLevel, RechargeTemplate.RECHARGE_TEMPLATE_USERLEVEL_ALLUSER)
								|| (StringUtil.compareObject(userLevel, RechargeTemplate.RECHARGE_TEMPLATE_USERLEVEL_ALLDECLARE)
										&& userLevelList.contains(level))
								|| StringUtil.compareObject(level, userLevel)) {
							product.setIsRechargeGiftProduct(true);
							product.setRechargeDesc(String.format("充值%s元免费赠送", StringUtil.nullToDouble(rechargeTemplate.getAmount()).intValue()));
							product.setRechargeNotes(String.format("充值%s元即可免费获得%s一份，更多充值活动，请至充值中心查看!", StringUtil.nullToDouble(rechargeTemplate.getAmount()).intValue(),StringUtil.null2Str(product.getName())));
							return;
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 检查用户时候含有此商品优惠券
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
		    		//检查优惠券
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
		    			//全场券
		    			productCouponList.add(coupon);
		    		}else if(StringUtil.compareObject(attribute, Coupon.COUPON_ATTRIBUTE_CATEGORY)) {
		    			//分类全
						List<Long> productCategoryIdList = StringUtil.stringToLongArray(coupon.getAttributeContent());
                        if(productCategoryIdList != null && !productCategoryIdList.isEmpty()
                        		&& productCategoryIdList.retainAll(product.getCategoryIdList())) {
                        	productCouponList.add(coupon);
                        }
		    		}else{
		    			//商品券
						List<Long> productIdList = StringUtil.stringToLongArray(coupon.getAttributeContent());
						if(productIdList != null && !productIdList.isEmpty()
                        		&& productIdList.contains(StringUtil.nullToLong(product.getProductId()))) {
                        	productCouponList.add(coupon);
                        }
		    		}
		    	}
		    	
		    	if(productCouponList != null && !productCouponList.isEmpty()) {
		    		//送金额，降序
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
		    			product.setCouponIntro(String.format("满%s减%s",StringUtil.getRealNumber(coupon.getFullAmount()) ,StringUtil.getRealNumber(coupon.getGiveAmount())));
		    		}
		    	}
		    }
		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 检查聚合商品
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
					// 排序
					Collections.sort(aggrProductList, new Comparator<Product>() {
						@Override
						public int compare(Product o1, Product o2) {
							int sort = StringUtil.booleanToInt(o1.getIsPaymentSoldout()).compareTo(StringUtil.booleanToInt(o2.getIsPaymentSoldout()));

							if (sort == 0) {
								// 秒杀
								sort = -StringUtil.booleanToInt(o1.getIsSeckillProduct()).compareTo(StringUtil.booleanToInt(o2.getIsSeckillProduct()));
								if (sort == 0) {
									// 商品类型
									Integer productType1 = StringUtil.nullToInteger(o1.getProductType());
									Integer productType2 = StringUtil.nullToInteger(o2.getProductType());
									sort = productType1.compareTo(productType2);
								}
							}

							return sort;
						}
					});

					List<Integer> productTypeList = new ArrayList<Integer>();
					productTypeList.add(GoodsType.GOODS_TYPE_DIRECT); // 直邮
					productTypeList.add(GoodsType.GOODS_TYPE_DIRECT_GO);// 行邮
					
					// 当前商品默认排在第一
					aggrProductList.add(0, product);
					for (Product aggrProduct : aggrProductList) {
						ProductWarehouse aggrProductWarehouse = ProductUtil.getProductWarehouse(StringUtil.nullToLong(aggrProduct.getWareHouseId()));
						if (aggrProductWarehouse != null && aggrProductWarehouse.getWarehouseId() != null) {
							aggrProduct.setProductType(StringUtil.nullToInteger(aggrProductWarehouse.getProductType()));
							aggrProduct.setWareHouseName(String.format("%s发货", StringUtil.null2Str(aggrProductWarehouse.getName())));
							if (productTypeList.contains(aggrProductWarehouse.getProductType())) {
								aggrProduct.setPayIntro("支付成功后7~10个工作日内送达");
							} else {
								aggrProduct.setPayIntro("支付成功后1~2个工作日发货");
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
	 * 计算商品税费
	 * @param product
	 * @return
	 */
	public static MsgModel<Double> getProductTax(Double paymentPrice, Integer productType, Boolean isFreeTax){
		MsgModel<Double> msgModel = new MsgModel<Double>();
		try {
			List<Integer> productTypeList = new ArrayList<Integer>();
			productTypeList.add(GoodsType.GOODS_TYPE_CROSS);   //跨境
			productTypeList.add(GoodsType.GOODS_TYPE_DIRECT);  //直邮

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
	 * 获取商品一级分类、二级分类集合列表
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
							//精选
							secondCategroyList.add(productCategory);
						}
					}
				}
			}
			
			//排序
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
	 * 获取聚合商品第一个商品
	 * @param product
	 * @return
	 */
	public static void getFirstAggrProduct(Product product){
		try {
			if(StringUtil.nullToBoolean(product.getIsAggrProduct())) {
				//聚合商品
				List<Product> aggrProductList = product.getAggrProductList();
				if(aggrProductList != null && !aggrProductList.isEmpty()) {
					// 排序
					Collections.sort(aggrProductList, new Comparator<Product>() {
						@Override
						public int compare(Product o1, Product o2) {
							int sort = StringUtil.booleanToInt(o1.getIsPaymentSoldout()).compareTo(StringUtil.booleanToInt(o2.getIsPaymentSoldout()));
							
							if (sort == 0) {
								// 秒杀
								sort = -StringUtil.booleanToInt(o1.getIsSeckillProduct()).compareTo(StringUtil.booleanToInt(o2.getIsSeckillProduct()));
								if (sort == 0) {
									// 商品类型
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

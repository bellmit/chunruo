package com.chunruo.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Component;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Area;
import com.chunruo.core.model.Country;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.ProductIntro;
import com.chunruo.core.model.PurchaseDoubt;
import com.chunruo.core.model.RefundReason;
import com.chunruo.core.model.WeChatAppConfig;
import com.chunruo.core.repository.RefundReasonRepository;
import com.chunruo.core.service.AreaManager;
import com.chunruo.core.service.CountryManager;
import com.chunruo.core.service.ProductCategoryManager;
import com.chunruo.core.service.ProductIntroManager;
import com.chunruo.core.service.PurchaseDoubtManager;
import com.chunruo.core.service.WeChatAppConfigManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Component
public class CoreInitUtil {

	/**
	 * 更新系统缓存
	 */
	public static void init() {
//		CoreInitUtil.initBankList();
		CoreInitUtil.initAreaConstantsList();
//		CoreInitUtil.initProductIntroConstantsList();
//		CoreInitUtil.initProductCountryConstantsList();
		CoreInitUtil.initProductCategoryConstantsList();
		CoreInitUtil.initWeChatAppConfigConstantsList();
//		CoreInitUtil.initUserLevelExplainConstantsList();
//		CoreInitUtil.initCouponTaskConstantsList();
//		CoreInitUtil.initCouponConstantsList();
//		CoreInitUtil.initRollingNotice();
//		CoreInitUtil.initPurchaseDoubtConstantsList();
//		CoreInitUtil.initDefKeywords();
//		CoreInitUtil.initApplyAgentProfession();
//		CoreInitUtil.initSignImageText();
//		CoreInitUtil.initActivity();
//		CoreInitUtil.initActivityPizeConstantsList();
//		CoreInitUtil.initActivityInfoConstantsList();
//		CoreInitUtil.initLuckDrawPrizeConstantsMap();
//		CoreInitUtil.initTeamRuleExplainConstantsList();
//		CoreInitUtil.initProductGiftList();
//		CoreInitUtil.initCategoryBrandList();
//		CoreInitUtil.initDiscovery();
//		CoreInitUtil.initInvitesCourtesy();
//		CoreInitUtil.initGuideProduct();
//		CoreInitUtil.initTrainTeacher();
//		CoreInitUtil.initUserSaleStandard();
//		CoreInitUtil.initAdviserUser();
	}

	/**
	 * 随机数
	 * @return
	 */
	public static String getRandomNo() {
		// 订单号生产规则
		Random random = new Random();
		int max = 100000;
		int min = 999999;
		int randomNumber = random.nextInt(max) % (max - min + 1) + min;
		String yyyyMMddHHmm = DateUtil.formatDate(DateUtil.DATE_TIME_HORI_PATTERN, DateUtil.getCurrentDate());
		String orderNo = yyyyMMddHHmm + randomNumber;
		return orderNo;
	}
	
	/**
	 * 随机数
	 * @return
	 */
	public static synchronized String getRandomStackNo() {
		// 订单号生产规则
		Random random = new Random();
		int max = 10000;
		int min = 99999;
		int randomNumber = random.nextInt(max) % (max - min + 1) + min;
		String yyyyMMdd = DateUtil.formatDate("yyyyMMdd", DateUtil.getCurrentDate());
		String hhmm = DateUtil.formatDate("HHmm", DateUtil.getCurrentDate());
		String orderNo = yyyyMMdd + randomNumber + "010" + hhmm;
		return orderNo;
	}

	/**
	 * 微信应用列表
	 */
	public static void initWeChatAppConfigConstantsList() {
		WeChatAppConfigManager weChatAppConfigManager = Constants.ctx.getBean(WeChatAppConfigManager.class);
		List<WeChatAppConfig> weChatAppConfigList = weChatAppConfigManager.getAll();
		if (weChatAppConfigList != null && weChatAppConfigList.size() > 0) {
			Constants.WECHAT_CONFIG_ID_MAP.clear();
			Constants.WECHAT_APP_ID_MAP.clear();
			for (WeChatAppConfig weChatAppConfig : weChatAppConfigList) {
				Constants.WECHAT_APP_ID_MAP.put(weChatAppConfig.getAppId(), weChatAppConfig);
				Constants.WECHAT_CONFIG_ID_MAP.put(weChatAppConfig.getConfigId(), weChatAppConfig);
			}
		}
	}

	/**
	 * 初始化商品分类
	 */
	public static void initProductCategoryConstantsList() {
		ProductCategoryManager productCategoryManager = Constants.ctx.getBean(ProductCategoryManager.class);
		List<ProductCategory> productCategoryList = productCategoryManager.getProductCategoryByStatus(1);
		if (productCategoryList != null && productCategoryList.size() > 0) {
			Constants.PRODUCT_CATEGORY_TREE_LIST.clear();
			Constants.PRODUCT_CATEGORY_MAP.clear();
			for (ProductCategory productCategory : productCategoryList) {
				Constants.PRODUCT_CATEGORY_MAP.put(productCategory.getCategoryId(), productCategory);
			}
			// 父子节点合并
			List<ProductCategory> parentCategoryList = new ArrayList<ProductCategory>();
			Map<Long, List<ProductCategory>> parentCategoryMap = new HashMap<Long, List<ProductCategory>>();
			for (ProductCategory productCategory : productCategoryList) {
				if (StringUtil.compareObject(productCategory.getLevel(), "1")) {
					// 一级分类
					parentCategoryList.add(productCategory);
				} else if (productCategory.getParentId() != null
						&& StringUtil.nullToInteger(productCategory.getParentId()) > 0) {
					// 子节点 
					if (parentCategoryMap.containsKey(productCategory.getParentId())) {
						parentCategoryMap.get(productCategory.getParentId()).add(productCategory);
					} else {
						List<ProductCategory> list = new ArrayList<ProductCategory>();
						list.add(productCategory);
						parentCategoryMap.put(productCategory.getParentId(), list);
					}
				}
			}

			// 父子关系合并
			if (parentCategoryList != null && parentCategoryList.size() > 0) {
				// 父类一级分类排序
				Collections.sort(parentCategoryList, new Comparator<ProductCategory>() {
					public int compare(ProductCategory obj1, ProductCategory obj2) {
						Integer sort1 = StringUtil.nullToInteger(obj1.getSort());
						Integer sort2 = StringUtil.nullToInteger(obj2.getSort());
						return sort1.compareTo(sort2);
					}
				});

				// 二级分类信息
				if (parentCategoryMap != null && parentCategoryMap.size() > 0) {
					for (ProductCategory productCategory : parentCategoryList) {
						if (parentCategoryMap.containsKey(productCategory.getCategoryId())) {
							List<ProductCategory> childCategoryList = parentCategoryMap
									.get(productCategory.getCategoryId());
							Collections.sort(childCategoryList, new Comparator<ProductCategory>() {
								public int compare(ProductCategory obj1, ProductCategory obj2) {
									Integer sort1 = StringUtil.nullToInteger(obj1.getSort());
									Integer sort2 = StringUtil.nullToInteger(obj2.getSort());
									return sort1.compareTo(sort2);
								}
							});
							productCategory.setChildCategoryList(childCategoryList);
						}
					}
				}
				Constants.PRODUCT_CATEGORY_TREE_LIST.addAll(parentCategoryList);
			}
		}

		// 退款退货原因列表
		RefundReasonRepository refundReasonRepository = Constants.ctx.getBean(RefundReasonRepository.class);
		List<RefundReason> refundReasonList = refundReasonRepository.findAll();
		if (refundReasonList != null && refundReasonList.size() > 0) {
			Constants.REFUND_REASON_MAP.clear();
			for (RefundReason refundReason : refundReasonList) {
				Constants.REFUND_REASON_MAP.put(refundReason.getReasonId(), refundReason);
			}
		}

	}


	

	/**
	 * 地区省市区初始化
	 */
	public static void initAreaConstantsList() {
		AreaManager areaManager = Constants.ctx.getBean(AreaManager.class);
		List<Area> areaList = areaManager.getAreaListByIsDisUse(false);
		if (areaList != null && areaList.size() > 0) {
			Constants.AREA_MAP.clear();
			Constants.PROVINCE_AREA_LIST.clear();
			Constants.CITY_ARE_AMAP.clear();
			Constants.COUNTRY_AREA_MAP.clear();
			Constants.AREA_LIST_MAP.clear();
			Constants.AREA_TREE_LIST.clear();

			for (Area area : areaList) {
				Constants.AREA_MAP.put(area.getAreaId(), area);
				if (StringUtil.compareObject(area.getLevel(), Area.LEVEL_PROVINCE)) {
					// 省份
					Constants.PROVINCE_AREA_LIST.add(area);
				} else if (StringUtil.compareObject(area.getLevel(), Area.LEVEL_CITY)) {
					// 城市
					if (Constants.CITY_ARE_AMAP.containsKey(area.getParentId())) {
						Constants.CITY_ARE_AMAP.get(area.getParentId()).add(area);
					} else {
						List<Area> list = new ArrayList<Area>();
						list.add(area);
						Constants.CITY_ARE_AMAP.put(area.getParentId(), list);
					}
				} else if (StringUtil.compareObject(area.getLevel(), Area.LEVEL_COUNTY)) {
					// 县区
					if (Constants.COUNTRY_AREA_MAP.containsKey(area.getParentId())) {
						Constants.COUNTRY_AREA_MAP.get(area.getParentId()).add(area);
					} else {
						List<Area> list = new ArrayList<Area>();
						list.add(area);
						Constants.COUNTRY_AREA_MAP.put(area.getParentId(), list);
					}
				}

				// 省份->城市列表,城市->地区列表
				if (area.getParentId() != null) {
					if (Constants.AREA_LIST_MAP != null && Constants.AREA_LIST_MAP.containsKey(area.getParentId())) {
						Constants.AREA_LIST_MAP.get(area.getParentId()).add(area);
					} else {
						List<Area> list = new ArrayList<Area>();
						list.add(area);
						Constants.AREA_LIST_MAP.put(area.getParentId(), list);
					}
				}
			}

			// 省市区集合
			if (Constants.PROVINCE_AREA_LIST != null && Constants.PROVINCE_AREA_LIST.size() > 0) {
				Long lastTime = null;
				List<Area> allAreaTreeList = new ArrayList<Area>();
				for (Area provinceArea : Constants.PROVINCE_AREA_LIST) {
					// 找出区域对象最后更新时间
					if (provinceArea.getUpdateTime() != null) {
						if (lastTime == null || lastTime.longValue() < provinceArea.getUpdateTime().getTime()) {
							lastTime = provinceArea.getUpdateTime().getTime();
						}
					}

					// 省份遍历
					if (Constants.CITY_ARE_AMAP != null && Constants.CITY_ARE_AMAP.size() > 0
							&& Constants.CITY_ARE_AMAP.containsKey(provinceArea.getAreaId())) {
						List<Area> cityAreaList = Constants.CITY_ARE_AMAP.get(provinceArea.getAreaId());
						if (cityAreaList != null && cityAreaList.size() > 0) {
							for (Area cityArea : cityAreaList) {
								// 找出区域对象最后更新时间
								if (cityArea.getUpdateTime() != null) {
									if (lastTime == null || lastTime.longValue() < cityArea.getUpdateTime().getTime()) {
										lastTime = cityArea.getUpdateTime().getTime();
									}
								}

								// 城市遍历
								if (Constants.COUNTRY_AREA_MAP != null && Constants.COUNTRY_AREA_MAP.size() > 0
										&& Constants.COUNTRY_AREA_MAP.containsKey(cityArea.getAreaId())) {
									List<Area> countryAreaList = Constants.COUNTRY_AREA_MAP.get(cityArea.getAreaId());
									if (countryAreaList != null && countryAreaList.size() > 0) {
										for (Area countryArea : countryAreaList) {
											// 找出区域对象最后更新时间
											if (countryArea.getUpdateTime() != null) {
												if (lastTime == null || lastTime.longValue() < countryArea
														.getUpdateTime().getTime()) {
													lastTime = countryArea.getUpdateTime().getTime();
												}
											}
										}
										// 子区县列表
										cityArea.setChildAreaList(countryAreaList);
									}
								}
							}
						}
						// 子城市列表
						provinceArea.setChildAreaList(cityAreaList);
					}

					// 全部省份列表
					allAreaTreeList.add(provinceArea);
				}

				// 补每个对象的最后统一更新时间
				for (Area provinceArea : allAreaTreeList) {
					provinceArea.setLastTime(lastTime);
				}
				Constants.AREA_TREE_LIST.addAll(allAreaTreeList);
			}
		}
	}

	/**
	 * 商品说明列表
	 */
	public static void initProductIntroConstantsList() {
		ProductIntroManager productIntroManager = Constants.ctx.getBean(ProductIntroManager.class);
		List<ProductIntro> productIntroList = productIntroManager.getAll();
		if (productIntroList != null && productIntroList.size() > 0) {
			Constants.PRODUCT_INTRO_MAP.clear();
			for (ProductIntro productIntro : productIntroList) {
				Constants.PRODUCT_INTRO_MAP.put(productIntro.getIntroId(), productIntro);
			}
		}
	}
	
	/**
	 * 购买答疑列表
	 */
	public static void initPurchaseDoubtConstantsList() {
		PurchaseDoubtManager purchaseDoubtManager = Constants.ctx.getBean(PurchaseDoubtManager.class);
		List<PurchaseDoubt> purchaseDoubtList = purchaseDoubtManager.getAll();
		if (purchaseDoubtList != null && purchaseDoubtList.size() > 0) {
			Constants.PURCHASE_DOUBT_MAP.clear();
			for (PurchaseDoubt purchaseDoubt : purchaseDoubtList) {
				Constants.PURCHASE_DOUBT_MAP.put(purchaseDoubt.getDoubtId(), purchaseDoubt);
			}
		}
	}	

	/**
	 * 商品国家归属列表
	 */
	public static void initProductCountryConstantsList() {
		CountryManager countryManager = Constants.ctx.getBean(CountryManager.class);
		List<Country> productCountryList = countryManager.getCountryListByIsProductShow(true);
		if (productCountryList != null && productCountryList.size() > 0) {
			Constants.PRODUCT_COUNTRY_MAP.clear();
			for (Country country : productCountryList) {
				Constants.PRODUCT_COUNTRY_MAP.put(country.getCountryId(), country);
			}
		}
	}



}

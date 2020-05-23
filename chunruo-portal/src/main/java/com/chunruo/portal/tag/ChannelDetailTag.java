package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import com.chunruo.cache.portal.impl.DateCacheManager;
import com.chunruo.cache.portal.impl.FxChannelListMapByIdCacheManager;
import com.chunruo.cache.portal.impl.FxChildrenListByPageIdCacheManager;
import com.chunruo.cache.portal.impl.FxPageByIdCacheManager;
import com.chunruo.cache.portal.impl.FxPageListByChannelIdCacheManager;
import com.chunruo.cache.portal.impl.UserInfoByIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.FxChannel;
import com.chunruo.core.model.FxChildren;
import com.chunruo.core.model.FxPage;
import com.chunruo.core.model.Keywords;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.ProductCheckUtil;
import com.chunruo.portal.util.ProductUtil;
import com.chunruo.portal.vo.TagModel;

/**
 * 分销市场频道详情
 * @author chunruo
 *
 */
public class ChannelDetailTag extends BaseTag {

	public TagModel<List<FxChildren>> getData(Object channelId_1, Object pageId_1){
		// 首页频道ID
		Long channelId = null;
		if(channelId_1 != null && StringUtil.isNumber(channelId_1)){
			channelId = StringUtil.nullToLong(channelId_1);
		}
		
		// 频道内页ID
		Long pageId = null;
		if(pageId_1 != null && StringUtil.isNumber(pageId_1)){
			pageId = StringUtil.nullToLong(pageId_1);
		}
		
		TagModel<List<FxChildren>> tagModel = new TagModel<List<FxChildren>> ();
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		try{
//			UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
//			if(userInfo == null || userInfo.getUserId() == null){
//				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
//				tagModel.setMsg("用户未登陆");
//				return tagModel;
//			}
		
			
			DateCacheManager dateCacheManager = Constants.ctx.getBean(DateCacheManager.class);
			FxPageByIdCacheManager fxPageByIdCacheManager = Constants.ctx.getBean(FxPageByIdCacheManager.class);
			UserInfoByIdCacheManager userInfoByIdCacheManager = Constants.ctx.getBean(UserInfoByIdCacheManager.class);
			FxChannelListMapByIdCacheManager fxChannelListMapByIdCacheManager = Constants.ctx.getBean(FxChannelListMapByIdCacheManager.class);
			FxPageListByChannelIdCacheManager fxPageListByChannelIdCacheManager = Constants.ctx.getBean(FxPageListByChannelIdCacheManager.class);
			FxChildrenListByPageIdCacheManager fxChildrenListByPageIdCacheManager = Constants.ctx.getBean(FxChildrenListByPageIdCacheManager.class);
			

			//从缓存重新读取用户信息
//			userInfo = userInfoByIdCacheManager.getSession(userInfo.getUserId());
			
			//如果频道id不存在 判断内页id是否存在
			if (StringUtil.isNull(channelId) || StringUtil.compareObject(channelId, 0)){
				FxPage page = null;
				if (!StringUtil.isNull(pageId) && !StringUtil.compareObject(0, pageId)){
					page = fxPageByIdCacheManager.getSession(pageId);
					if (page == null || page.getPageId() == null || StringUtil.nullToBoolean(page.getIsDelete())){
						//不存在或者已删除
						tagModel.setCode(PortalConstants.CODE_ERROR);
						tagModel.setMsg("内页或专题不存在");
						return tagModel;
					}
				}

				if (page != null && page.getPageId() != null){
					channelId = page.getChannelId();
					resultMap.put("pageName", StringUtil.null2Str(page.getPageName()));
				}
				
			}

			// 市场列表最后更新时间
			String cacheName = fxChannelListMapByIdCacheManager.getCacheName();
			Long sessionNextLastTime = dateCacheManager.getSession(cacheName);
			tagModel.setObjectId(sessionNextLastTime);

			Long firstChannelId = 0L;
			boolean isFirstStart = false;
			// 分销市场频道列表
			Map<String, FxChannel> fxChannelListMap = fxChannelListMapByIdCacheManager.getSession();
			if(fxChannelListMap != null && fxChannelListMap.size() > 0){
				List<Map.Entry<String, FxChannel>> mappingList = new ArrayList<Map.Entry<String, FxChannel>> (fxChannelListMap.entrySet());
				Collections.sort(mappingList, new Comparator<Map.Entry<String, FxChannel>>(){
					// 排序
					public int compare(Map.Entry<String, FxChannel> obj1, Map.Entry<String, FxChannel> obj2){
						int stor1 = StringUtil.nullToInteger(obj1.getValue().getSort());
						int stor2 = StringUtil.nullToInteger(obj2.getValue().getSort());
						return (stor2 < stor1) ? 1 : -1;
					}
				});

				if(mappingList != null && mappingList.size() > 0){
					// 秒杀频道ID
					Long channelSeckillId = StringUtil.nullToLong(Constants.conf.getProperty("jkd.seckill.channel.id"));
					List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>> ();
					for(Map.Entry<String, FxChannel> entry : mappingList){
						FxChannel fxChannel = entry.getValue();
						// 检查是否秒杀
						Integer isSeckill = Constants.NO;
						if(StringUtil.compareObject(channelSeckillId, fxChannel.getChannelId())){
							isSeckill = Constants.YES;
						}
						
						Map<String, Object> objectMap = new HashMap<String, Object> ();
						objectMap.put("channelId", fxChannel.getChannelId());				//序号
						objectMap.put("channelName", fxChannel.getChannelName());			//渠道名称
						objectMap.put("status", fxChannel.getStatus());						//状态(0:停止;1:启用;2:删除)
						objectMap.put("sort", fxChannel.getSort());							//排序
						objectMap.put("isSeckill", StringUtil.nullToInteger(isSeckill));	//是否秒杀
						if(StringUtil.compareObject(fxChannel.getChannelName(), "首页")) {
							firstChannelId = fxChannel.getChannelId();
							objectMap.put("isHomeChannel", Constants.YES);	//是否首页频道
						}
						mapList.add(objectMap);
					}
					tagModel.setMapList(mapList);
					if(channelId == null) {
						//默认第一个频道
						channelId = firstChannelId;
						isFirstStart = true;
					}
				}
			}


			if(channelId != null 
					&& fxChannelListMap != null
					&& fxChannelListMap.size() > 0
					&& fxChannelListMap.containsKey(StringUtil.null2Str(channelId))){
				Map<String, FxPage> fxPageListMap = fxPageListByChannelIdCacheManager.getSession(channelId);
				if(fxPageListMap != null && fxPageListMap.size() > 0){
					//判断是否是专题页面
					boolean isTheme=false;
					List<FxChildren> fxChildrenList = new ArrayList<FxChildren> ();
					if(pageId != null){
						//频道内页、专题数据
						List<Integer> pageThemecategoryTypeList = new ArrayList<Integer>();
						pageThemecategoryTypeList.add(FxPage.CATEGORY_TYPE_PAGE);
						pageThemecategoryTypeList.add(FxPage.CATEGORY_TYPE_THEME);
						FxPage fxPage = fxPageListMap.get(StringUtil.null2Str(pageId));
						if(fxPage != null && pageThemecategoryTypeList.contains(fxPage.getCategoryType())){
							if(StringUtil.compareObject(fxPage.getCategoryType(), FxPage.CATEGORY_TYPE_THEME)) {
								//是专题页面
								isTheme=true;
							}
							List<FxChildren> list = fxChildrenListByPageIdCacheManager.getSession(fxPage.getPageId());
							if(list != null && list.size() > 0){
								fxChildrenList.addAll(list);
							}
						}
					}else{
						//频道列表首页数据
						for(Map.Entry<String, FxPage> entry : fxPageListMap.entrySet()){
							FxPage fxPage = entry.getValue();
							if(fxPage != null && StringUtil.compareObject(fxPage.getCategoryType(), FxPage.CATEGORY_TYPE_HOME)){
								List<FxChildren> list = fxChildrenListByPageIdCacheManager.getSession(fxPage.getPageId());
								if(list != null && list.size() > 0){
									fxChildrenList.addAll(list);
								}
							}
						}
					}

					Collections.sort(fxChildrenList, new Comparator<FxChildren>(){
						// 排序
						public int compare(FxChildren obj1, FxChildren obj2){
							int sort1 = StringUtil.nullToInteger(obj1.getSort());
							int sort2 = StringUtil.nullToInteger(obj2.getSort());
							if(StringUtil.compareObject(sort1, sort2)){
								if(obj1.getCreateTime() == null){
									return 1;
								}else if(obj2.getCreateTime() == null){
									return -1;
								}
								return (obj1.getCreateTime().getTime() < obj2.getUpdateTime().getTime()) ? 1 : -1;
							}else{
								return (sort2 < sort1) ? 1 : -1;
							}
						}
					});

					// 频道详情
					if(fxChildrenList != null && fxChildrenList.size() > 0){
						//Banner、专题和导航
						List<Integer> bannerNavigationTypeList = new ArrayList<Integer> ();
						bannerNavigationTypeList.add(FxChildren.FXCHILDREN_TYP_BANNER);
						bannerNavigationTypeList.add(FxChildren.FXCHILDREN_TYP_NAVIGATION);
						bannerNavigationTypeList.add(FxChildren.FXCHILDREN_TYP_SPECIAL);
						bannerNavigationTypeList.add(FxChildren.FXCHILDREN_TYP_SECKILL);
						for(FxChildren fxChildren : fxChildrenList){  //页面所有类型的集合
							List<Map<String, String>> mapList = StringUtil.jsonToHashMapList(fxChildren.getContents());
							if(mapList != null && mapList.size() > 0){
								if(bannerNavigationTypeList.contains(StringUtil.nullToInteger(fxChildren.getType()))){
									//如果是专题banner
									if(isTheme) {
										resultMap.put("picture", fxChildren.getPicture());
										continue;
									}
									
									//Banner、专题和导航
									int index = 0;
									List<Map<String, Object>> detailMapList = new ArrayList<Map<String, Object>> ();
									for(Map<String, String> map : mapList){
										//首页显示专题商品
										if(StringUtil.compareObject(fxChildren.getType(),FxChildren.FXCHILDREN_TYP_SPECIAL)) {
											//专题页面id
											List<Long> specialIdList = StringUtil.stringToLongArray(mapList.get(0).get("content"));
											if(!CollectionUtils.isEmpty(specialIdList)) {
												getSpecialDetailMap(fxChildrenListByPageIdCacheManager, specialIdList, fxChildren, false);
											}															
										}
										
										Map<String, Object> detailMap = new HashMap<String, Object> ();
										detailMap.put("content", StringUtil.null2Str(map.get("content")));
										detailMap.put("targetType", StringUtil.null2Str(map.get("target_type")));
										detailMap.put("navigationName", StringUtil.null2Str(map.get("navigation_name")));
										detailMap.put("picture", StringUtil.null2Str(map.get("picture")));
										detailMap.put("giftId", StringUtil.null2Str(map.get("giftId")));
										detailMap.put("originId", StringUtil.null2Str(map.get("originId")));
										detailMap.put("appid", StringUtil.null2Str(map.get("appid")));
										detailMap.put("seckillId", StringUtil.null2Str(fxChildren.getSeckillId()));
										detailMap.put("seckillName", StringUtil.null2Str(fxChildren.getSeckillName()));
										detailMap.put("startTime", StringUtil.null2Str(fxChildren.getStartTime()));
										detailMap.put("endTime", StringUtil.null2Str(fxChildren.getEndTime()));
										
										// 检查内页标题
										FxPage fxPage = fxPageListMap.get(StringUtil.null2Str(map.get("content")));
										if(fxPage != null && fxPage.getPageId() != null){
											detailMap.put("title", fxPage.getPageName());
										}
										detailMapList.add(detailMap);
										
										if(StringUtil.compareObject(firstChannelId, channelId)
												&& index == 0 && isFirstStart) {
											//第一个频道默认色值
											resultMap.put("color", StringUtil.null2Str(map.get("navigation_name")));
										}
										index++;
									}
									fxChildren.setDetailMapList(detailMapList);
								}else if(StringUtil.compareObject(fxChildren.getType(), FxChildren.FXCHILDREN_TYP_PRODUCT) || StringUtil.compareObject(fxChildren.getType(), FxChildren.FXCHILDREN_TYP_MODULE)){
									//商品
									List<Long> productIdList = StringUtil.stringToLongArray(mapList.get(0).get("content"));
									if(productIdList != null && productIdList.size() > 0){
										List<Map<String, Object>> detailMapList = new ArrayList<Map<String, Object>> ();
										for(Long productId : productIdList){
											MsgModel<Product> msgModel = ProductUtil.getProductByProductId(productId, true);
											if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
												// 商品信息
												Product product = msgModel.getData();
												detailMapList.add(getDetailMap(product));
											}
										}
										fxChildren.setDetailMapList(detailMapList);
									}
								}
							}
							
							if(isFirstStart) {
								//首次启动不需要节点数据
								fxChildrenList.clear();
								break;
							}
						}
						
					}
					tagModel.setData(fxChildrenList);
				}
			}

			//获得默认搜索词
			Keywords keywords = Constants.DEFAULT_KEYWORDS;	
			if(keywords != null && keywords.getKeywordsId() != null){
				resultMap.put("defKeywords", StringUtil.decode(keywords.getName()));
			}
			tagModel.setDataMap(resultMap);
		}catch(Exception e){
			e.printStackTrace();
        }

		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}

	/**
	 * 解析商品
	 * @param wholesale userInfo
	 * @return
	 */
	public Map<String, Object> getDetailMap(Product product){
		// 秒杀商品即将开始状态
		ProductCheckUtil.checkSeckillProductPriceReadStatus(product);
		
		Map<String, Object> detailMap = new HashMap<String, Object> ();
		detailMap.put("productId", product.getProductId());
		detailMap.put("productName", product.getName());
		detailMap.put("image", product.getImage());
		detailMap.put("isShowPrice", StringUtil.nullToBoolean(product.getIsShowPrice()));
		detailMap.put("isSeckillProduct", StringUtil.nullToBoolean(product.getIsSeckillProduct()));
		detailMap.put("isGroupProduct", StringUtil.booleanToInt(product.getIsGroupProduct()));
		detailMap.put("salesNumber", StringUtil.nullToInteger(product.getSalesNumber()));
		detailMap.put("price", StringUtil.nullToDoubleFormatStr(product.getPaymentPrice()));
		detailMap.put("priceRecommend", StringUtil.nullToDoubleFormatStr(product.getPriceRecommend()));
		detailMap.put("seckillPrice", StringUtil.nullToDoubleFormatStr(product.getSeckillPrice()));
		detailMap.put("v2Price", StringUtil.nullToDoubleFormatStr(product.getV2Price()));
		detailMap.put("v3Price", StringUtil.nullToDoubleFormatStr(product.getV3Price()));
		detailMap.put("productProfit", StringUtil.nullToInteger(product.getProductProfit()));
		detailMap.put("productEffectIntro", product.getProductEffectIntro());
		detailMap.put("isProxy", StringUtil.nullToInteger(product.getIsProxy()));
		detailMap.put("isOpenV2Price", StringUtil.booleanToInt(product.getIsOpenV2Price()));
		detailMap.put("isOpenV3Price", StringUtil.booleanToInt(product.getIsOpenV3Price()));
		detailMap.put("isShowLevelPrice", StringUtil.booleanToInt(product.getIsShowLevelPrice()));
		detailMap.put("isShowV2Price", StringUtil.booleanToInt(product.getIsShowV2Price()));
		detailMap.put("isShowV3Price", StringUtil.booleanToInt(product.getIsShowV3Price()));
		detailMap.put("isOpenPriceRecommend", StringUtil.booleanToInt(product.getIsOpenPriceRecommend()));
		detailMap.put("isSoldOut", StringUtil.booleanToInt(product.getIsPaymentSoldout()));
		detailMap.put("isHaveProductMaterial", StringUtil.booleanToInt(product.getIsHaveProductMaterial()));
		detailMap.put("isTaskProduct", StringUtil.booleanToInt(product.getIsTaskProduct()));
		detailMap.put("taskProductTag", StringUtil.null2Str(product.getTaskProductTag()));
		detailMap.put("minPaymentPrice", StringUtil.nullToDoubleFormatStr(product.getMinPaymentPrice()));
		detailMap.put("maxPaymentPrice", StringUtil.nullToDoubleFormatStr(product.getMaxPaymentPrice()));
		detailMap.put("rewardNotes", StringUtil.null2Str(product.getRewardNotes()));
		detailMap.put("isRechargeGiftProduct", StringUtil.booleanToInt(product.getIsRechargeGiftProduct()));
		detailMap.put("originalPrice", StringUtil.nullToDoubleFormatStr(product.getPaymentOriginalPrice()));
		detailMap.put("isSeckillStarted", StringUtil.nullToBoolean(product.getIsSeckillStarted()));
		detailMap.put("seckillEndTime", StringUtil.nullToLong(product.getSeckillEndTime()));
		detailMap.put("couponIntro", StringUtil.null2Str(product.getCouponIntro()));
		detailMap.put("soldoutNoticeType", StringUtil.nullToLong(product.getSoldoutNoticeType()));
		detailMap.put("soldoutNotice", StringUtil.null2Str(product.getSoldoutNotice()));
		detailMap.put("seckillNoticeType", StringUtil.nullToInteger(product.getSeckillNoticeType()));

		return detailMap;
	}

	/**
	 * 解析专题商品
	 * @param wholesale userInfo
	 * @return
	 */
	public void getSpecialDetailMap(FxChildrenListByPageIdCacheManager fxChildrenListByPageIdCacheManager, List<Long> specialIdList, FxChildren fxChildren, boolean isNotWithinPage){
		if (!CollectionUtils.isEmpty(specialIdList)) {
			// 专题页面集合
			List<FxChildren> specialList = fxChildrenListByPageIdCacheManager.getSession(specialIdList.get(0));
			// 专题banner
			if (!CollectionUtils.isEmpty(specialList)) {
				List<Long> productIdList = new ArrayList<Long>();
				for (FxChildren speFxChildren : specialList) {
					if (StringUtil.compareObject(FxChildren.FXCHILDREN_TYP_MODULE, speFxChildren.getType())) {
						// 得到每个类型为商品的coetents数据
						List<Long> evenSpeGoodsIdList = StringUtil.stringToLongArray(
								StringUtil.jsonToHashMapList(speFxChildren.getContents()).get(0).get("content"));
						if (evenSpeGoodsIdList != null && evenSpeGoodsIdList.size() > 0) {
							for (Long productId : evenSpeGoodsIdList) {
								productIdList.add(productId);
							}
						}
					} else if (StringUtil.compareObject(FxChildren.FXCHILDREN_TYP_SPECIAL, speFxChildren.getType())) {
						// 专题图片要跟专题页面banner一致
						fxChildren.setPicture(speFxChildren.getPicture());
					}
				}

				if (productIdList != null && productIdList.size() > 0) {
					List<Map<String, Object>> detailSpecialMapList = new ArrayList<Map<String, Object>>();
					for (Long productId : productIdList) {
						// 最多显示8个商品
						if (detailSpecialMapList.size() >= 8) {
							break;
						}
						
						MsgModel<Product> msgModel = ProductUtil.getProductByProductId(productId, true);
						if (StringUtil.nullToBoolean(msgModel.getIsSucc())) {
							// 商品信息
							Product product = msgModel.getData();
							detailSpecialMapList.add(getDetailMap(product));
						}
					}
					fxChildren.setDetailSpecialMapList(detailSpecialMapList);
				}
			}
		}
	}
	
	
	
}

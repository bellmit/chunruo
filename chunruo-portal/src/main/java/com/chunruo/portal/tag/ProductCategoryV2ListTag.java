package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.chunruo.cache.portal.impl.FxChannelListMapByIdCacheManager;
import com.chunruo.cache.portal.impl.ProductCategoryListCacheManager;
import com.chunruo.cache.portal.impl.ProductListByCatIdCacheManager;
import com.chunruo.core.Constants;
import com.chunruo.core.model.FxChannel;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.util.ListPageUtil;
import com.chunruo.core.util.MathUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.ListPageVo;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.PortalUtil;
import com.chunruo.portal.util.ProductCheckUtil;
import com.chunruo.portal.util.ProductUtil;
import com.chunruo.portal.vo.TagModel;

/**
 * 按分类查询供货商商品列表
 * 
 * @author chunruo
 *
 */
public class ProductCategoryV2ListTag extends BaseTag {

	public TagModel<List<Product>> getData(Object categoryId_1, Object pageidx_1, Object pagesize_1, Object lastId_1) {
		Long categoryId = StringUtil.nullToLong(categoryId_1);
		Integer pageidx = StringUtil.nullToInteger(pageidx_1);
		Integer pagesize = StringUtil.nullToInteger(pagesize_1);
		Long lastId = StringUtil.nullToLong(lastId_1);
        Long startTime = System.currentTimeMillis();
		// 设置分页
		if (pagesize == null || pagesize < 1)
			pagesize = PortalConstants.PAGE_LIST_SIZE;
		if (pageidx == null || pageidx <= 0)
			pageidx = 1;

		TagModel<List<Product>> tagModel = new TagModel<List<Product>>();
		try {

			// 检查用户是否登录
			final UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
			if (userInfo == null || userInfo.getUserId() == null) {
				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
				tagModel.setMsg("用户未登陆");
				return tagModel;
			}

			ProductCategoryListCacheManager productCategoryListCacheManager = Constants.ctx.getBean(ProductCategoryListCacheManager.class);
			ProductListByCatIdCacheManager productListByCatIdCacheManager = Constants.ctx.getBean(ProductListByCatIdCacheManager.class);

			boolean isHaveCategory = false;
			List<ProductCategory> productCategoryList = productCategoryListCacheManager.getSession();
			if (productCategoryList != null && productCategoryList.size() > 0) {
				for (ProductCategory productCategory : productCategoryList) {
					if (StringUtil.compareObject(categoryId, StringUtil.nullToLong(productCategory.getCategoryId()))) {
						// 含有此分类
						isHaveCategory = true;
						break;
					}
				}
			}

			if (!isHaveCategory) {
				if (StringUtil.compareObject(categoryId, 0)) {
					ProductCategory defaultCategory = productCategoryList.get(0);
					if (defaultCategory != null && defaultCategory.getCategoryId() != null) {
						categoryId = StringUtil.nullToLong(defaultCategory.getCategoryId());
					}
				} else {
					tagModel.setCode(PortalConstants.CODE_SUCCESS);
					tagModel.setMsg("未找到此分类");
					return tagModel;
				}
			}

			Map<Long, Product> productMap = new HashMap<Long, Product>();
			// 获取分类下的所有商品列表
			List<String> productIdList = productListByCatIdCacheManager.getSession(categoryId);
			if (productIdList != null && productIdList.size() > 0) {
				// 定长线程池
				ExecutorService exec = Executors.newFixedThreadPool(2);
				try {
					// 装载多线程返回的结果
					List<Future<Product>> result = new ArrayList<Future<Product>>();
					for (final String productId : productIdList) {
						result.add(exec.submit(new Callable<Product>() {
							@Override
							public Product call() throws Exception {
								try {
									// 检查商品
									MsgModel<Product> msgModel = ProductUtil.getProductByUserLevel(StringUtil.nullToLong(productId), userInfo, false);
									if (StringUtil.nullToBoolean(msgModel.getIsSucc())) {
										Product product = msgModel.getData();
										if (StringUtil.nullToBoolean(product.getIsTeamPackage())) {
											// 大礼包不显示
											return null;
										}
										return product;
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								return null;
							}
						}));
					}

					//多线程返回对象结果
					for (Future<Product> future : result) {
						Product product = future.get();
						if (product != null && product.getProductId() != null) {
							// 搜索结果商品
							productMap.put(StringUtil.nullToLong(product.getProductId()), product);
						}
					}

				} catch (Exception e) {
					throw e;
				} finally {
					exec.shutdown();
				}
			}

			//设置排序
			ProductUtil.getProductSortWeight(productMap);
			
			// 商品排序
			List<Map.Entry<Long, Product>> productMappingList = new ArrayList<Map.Entry<Long, Product>>(productMap.entrySet());
			Collections.sort(productMappingList, new Comparator<Map.Entry<Long, Product>>() {
				@Override
				public int compare(Map.Entry<Long, Product> o1, Map.Entry<Long, Product> o2) {
					try {
						Product product1 = o1.getValue();
						Product product2 = o2.getValue();
						
						//售罄商品排在最后
						Integer soldoutQuantity1 = StringUtil.booleanToInt(product1.getIsPaymentSoldout());
						Integer soldoutQuantity2 = StringUtil.booleanToInt(product2.getIsPaymentSoldout());
						int sort = soldoutQuantity1.compareTo(soldoutQuantity2);
						if(sort == 0) {
							Double sortWeight1 = StringUtil.nullToDouble(product1.getSortWeight());
							Double sortWeight2 = StringUtil.nullToDouble(product2.getSortWeight());
							sort = sortWeight1.compareTo(sortWeight2);
						}
						return sort;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return 0;
				}
			});

			List<Long> realProductIdList = new ArrayList<Long>();
			if (productMappingList != null && productMappingList.size() > 0) {
				for (Map.Entry<Long, Product> entry : productMappingList) {
					realProductIdList.add(entry.getKey());
				}
			}

			/**
			 * 自动List分页工具
			 */
			ListPageUtil<Product> pageUtil = new ListPageUtil<Product>() {
				@Override
				public Product addObject(Long objectId) {
					// 返回对象自定义
					if (productMap.containsKey(objectId)) {
						Product product = productMap.get(objectId);
						// 秒杀商品即将开始状态
						ProductCheckUtil.checkSeckillProductPriceReadStatus(product);
						return product;
					}
					return null;
				}
			};

			/**
			 * 返回自动分页结果
			 */
			ListPageVo<List<Product>> listPageVo = pageUtil.getPageList(realProductIdList, lastId, pageidx, pagesize);
			if (StringUtil.nullToBoolean(listPageVo.getIsNextPageURL())) {
				StringBuffer urls = new StringBuffer(this.getRequestURL(request) + "&");
				urls.append("categoryId=" + categoryId + "&");
				urls.append("pageidx=" + (++pageidx) + "&");
				urls.append("lastId=" + listPageVo.getLastId() + "&");
				urls.append("pagesize=" + pagesize);
				tagModel.setNextPageURL(urls.toString());
			}

			Map<String, Object> dataMap = new HashMap<String, Object>();
			// 检查是否开启推荐价显示
			FxChannelListMapByIdCacheManager fxChannelListMapByIdCacheManager = Constants.ctx.getBean(FxChannelListMapByIdCacheManager.class);
			Map<String, FxChannel> fxChannelListMap = fxChannelListMapByIdCacheManager.getSession();
			if (fxChannelListMap != null && fxChannelListMap.size() > 0) {
				if (fxChannelListMap.containsKey(StringUtil.null2Str(Constants.conf.getProperty("jkd.price.recommend.id")))) {
					dataMap.put("isOpenPriceRecommend", true);
				}
			}

			dataMap.put("categoryId", StringUtil.nullToLong(categoryId));
			dataMap.put("level", StringUtil.nullToString(userInfo.getLevel()));
			dataMap.put("productCategoryList", productCategoryList);
			tagModel.setDataMap(dataMap);
			Long total = Long.parseLong(realProductIdList.size() + "");
			Integer totalPage = ProductCategoryV2ListTag.getPageNumber(total, pagesize);
			tagModel.setData(listPageVo.getDataList());
			tagModel.setPage(pageidx);
			tagModel.setPagesize(pagesize);
			tagModel.setTotal(total);
			tagModel.setTotalPage(totalPage);

		} catch (Exception e) {
			e.printStackTrace();
		}
		 Long endTime = System.currentTimeMillis();
		 log.info(String.format("商品分类耗时%s秒",MathUtil.div(StringUtil.null2Str((endTime - startTime)), "1000")));
		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}

	/**
	 * 总页数
	 * 
	 * @param totalNumber
	 * @param pagesize
	 * @return
	 */
	private static Integer getPageNumber(Long totalNumber, Integer pagesize) {
		Long pageNum = Long.parseLong(pagesize.toString());
		Long page = totalNumber / pageNum;
		if (totalNumber % pageNum > 0) {
			page = page + 1;
		}
		return page.intValue();
	}
}

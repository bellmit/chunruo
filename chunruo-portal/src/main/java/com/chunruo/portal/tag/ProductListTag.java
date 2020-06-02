package com.chunruo.portal.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.chunruo.cache.portal.impl.ProductListByBrandIdCacheManager;
import com.chunruo.cache.portal.impl.ProductListByCatIdCacheManager;
import com.chunruo.cache.portal.impl.ProductListByCouponIdCacheManager;
import com.chunruo.cache.portal.vo.ProductSortVo;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Product;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.ListPageUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.util.vo.ListPageVo;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.util.ProductUtil;
import com.chunruo.portal.vo.TagModel;


public class ProductListTag extends BaseTag {
	public final static int PRODUCT_SORT_SN = 1;  //销量(综合)
	public final static int PRODUCT_SORT_FX	= 2;  //人气(利润)
	public final static int PRODUCT_SORT_JG = 3;  //成本(拿货价)
	public final static	int SORT_TYPE_DESC = 2;	  //降序
	public final static int SORT_TYPE_ASC = 1;	  //升序
	protected static final Long profitPrice2 = null;

	public TagModel<List<Product>> getData(Object brandId_1, Object couponId_1, Object firstCategoryId_1, Object secondCategoryId_1, Object keyword_1, Object sort_1, Object sortType_1, Object minPrice_1, Object maxPrice_1, Object productType_1, Object pageidx_1, Object pagesize_1, Object lastId_1, Object friendUser_1){
		long startTime = System.currentTimeMillis();
		Long firstCategoryId = StringUtil.nullToLong(firstCategoryId_1);
		Long secondCategoryId = StringUtil.nullToLong(secondCategoryId_1);
		String keyword = StringUtil.null2Str(keyword_1);
		keyword = StringUtil.decode(keyword);
		Integer sort = StringUtil.nullToInteger(sort_1);  //排序字段,1-销量,2-人气，3-成本
		Integer sortType = StringUtil.nullToInteger(sortType_1); 
		Integer pageidx = StringUtil.nullToInteger(pageidx_1); 
		Integer pagesize = StringUtil.nullToInteger(pagesize_1);
		Long lastId = StringUtil.nullToLong(lastId_1);
		Long couponId = StringUtil.nullToLong(couponId_1); //优惠券ID
		Long brandId = StringUtil.nullToLong(brandId_1); //品牌ID
		
		Double minPrice = StringUtil.nullToDouble(minPrice_1);			//最低价
		Double maxPrice = StringUtil.nullToDouble(maxPrice_1);			//最高价
		Integer productType = StringUtil.nullToInteger(productType_1);	//商品类型
		// 是否进行价格区间校验
		Boolean checkPrice = false;
		if(maxPrice.compareTo(minPrice) == 1
				&& (minPrice.compareTo(0D) >= 0 && maxPrice.compareTo(0D) == 1)) {
			checkPrice = true;
		}
		
		//设置分页
		if (pagesize == null || pagesize < 1)
			pagesize = PortalConstants.PAGE_LIST_SIZE;
		if (pageidx == null || pageidx <= 0)
			pageidx = 1;

		//默认人气排序
		if(sort == null || sort < 1)
			sort = ProductSortVo.PRODUCT_SORT_FX;
		if(sortType == null || sortType < 1)
			sortType = ProductSortVo.SORT_TYPE_DESC;

		TagModel<List<Product>> tagModel = new TagModel<List<Product>> ();
		try{
			
			// 检查一级分类、二级分类是否有效
			Long categoryId = 0L;
			if(!StringUtil.compareObject(firstCategoryId, 0) || !StringUtil.compareObject(secondCategoryId, 0)){
				// 默认为一级分类搜索
				categoryId = firstCategoryId;
				if(!StringUtil.compareObject(secondCategoryId, 0)){
					// 二级分类不为空搜索
					categoryId = secondCategoryId;
				}
			}

//			// 检查用户是否登录
//			final UserInfo userInfo = PortalUtil.getCurrentUserInfo(request);
//			if(userInfo == null || userInfo.getUserId() == null){
//				tagModel.setCode(PortalConstants.CODE_NOLOGIN);
//				tagModel.setMsg("用户未登陆");
//				return tagModel;
//			}

			ProductListByCatIdCacheManager productListByCatIdCacheManager = Constants.ctx.getBean(ProductListByCatIdCacheManager.class);
			ProductListByCouponIdCacheManager productListByCouponIdCacheManager = Constants.ctx.getBean(ProductListByCouponIdCacheManager.class);
			ProductListByBrandIdCacheManager productListByBrandIdCacheManager = Constants.ctx.getBean(ProductListByBrandIdCacheManager.class);

			// 优惠券、品牌、分类三种类型查询
			List<String> realProductIdList = new ArrayList<>();
			if(couponId > 0){
				//根据优惠券查询可用商品
				realProductIdList = productListByCouponIdCacheManager.getSession(couponId);
			} else if(brandId > 0){
				//根据品牌查询商品
				realProductIdList = productListByBrandIdCacheManager.getSession(brandId);
			} else{
				realProductIdList = productListByCatIdCacheManager.getSession(categoryId);
			}

			// 找出有效上架的商品
			Map<Long, Product> productMap = new HashMap<Long, Product> ();
			if(realProductIdList != null && realProductIdList.size() > 0){
				final Boolean xcheckPrice = checkPrice;
				try {
					for(String strProductId : realProductIdList){

						MsgModel<Product> msgModel = ProductUtil.getProductByProductId(StringUtil.nullToLong(strProductId), true);
						if(StringUtil.nullToBoolean(msgModel.getIsSucc())){
							Product product = msgModel.getData();
							//商品价格校验
							if(xcheckPrice) {
								Double paymentPrice = StringUtil.nullToDouble(product.getPaymentPrice());
								if(paymentPrice.compareTo(minPrice) == -1
										|| paymentPrice.compareTo(maxPrice) == 1) {
									return null;
								}
							}
							// 搜索结果商品
							productMap.put(StringUtil.nullToLong(product.getProductId()), product);
						}
						
					}
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			// 根据搜索关键字匹配
			final Map<Long, Product> realProductMap = new HashMap<Long, Product> ();
			if(productMap != null && productMap.size() > 0){
				// 检查是否有关键字搜索
				final boolean isKeyword = !StringUtil.isNull(keyword);
				if(isKeyword){
					//保存关键词
					ProductUtil.recordKeywords(keyword);

					// 根据标签匹配
					List<Product> productList = new ArrayList<Product> ();
					for(Entry<Long, Product> entry : productMap.entrySet()){
						Product product = entry.getValue();
						MsgModel<Long> xmsgModel = ProductUtil.getProductByKeyword(keyword,product);
						if(StringUtil.nullToBoolean(xmsgModel.getIsSucc())){
							productList.add(product);
						}
					}

					// 最终关键字匹配结果
					if(productList != null && productList.size() > 0){
						for(Product product : productList){
							realProductMap.put(product.getProductId(), product);
						}
					}
				}else{
					realProductMap.putAll(productMap);
				}
			}

			//综合排序规则
			if(StringUtil.compareObject(sort, ProductListTag.PRODUCT_SORT_SN)) {
				ProductUtil.getProductSortWeight(realProductMap);
			}
			//  排序
			final int realSort = sort;
			final int realSortType = sortType;
			final boolean isAsc = StringUtil.compareObject(realSortType, ProductListTag.SORT_TYPE_ASC);
			List<Map.Entry<Long, Product>> mappingList = new ArrayList<Map.Entry<Long, Product>> (realProductMap.entrySet());
			Collections.sort(mappingList, new Comparator<Map.Entry<Long, Product>>(){
				public int compare(Entry<Long, Product> o1, Entry<Long, Product> o2){
					Product productObj1 = o1.getValue();
					Product productObj2 = o2.getValue();

					//售罄商品排在最后
					Integer soldoutQuantity1 = StringUtil.booleanToInt(productObj1.getIsPaymentSoldout());
					Integer soldoutQuantity2 = StringUtil.booleanToInt(productObj2.getIsPaymentSoldout());
					int sort = soldoutQuantity1.compareTo(soldoutQuantity2);
					if(sort == 0) {
						if(StringUtil.compareObject(realSort, ProductListTag.PRODUCT_SORT_SN)){
							// 按销量排序
							Double sortWeight1 = StringUtil.nullToDouble(productObj1.getSortWeight());
							Double sortWeight2 = StringUtil.nullToDouble(productObj2.getSortWeight());
							sort = sortWeight1.compareTo(sortWeight2);
						}else if(StringUtil.compareObject(realSort, ProductListTag.PRODUCT_SORT_FX)){
							// 按利润排序
							Double profit1 = StringUtil.nullToDouble(productObj1.getProductProfit());
							Double profit2 = StringUtil.nullToDouble(productObj2.getProductProfit());
							if(isAsc){
								sort = profit1.compareTo(profit2);
							}else{
								sort = -profit1.compareTo(profit2);
							}
						}else if(StringUtil.compareObject(realSort, ProductListTag.PRODUCT_SORT_JG)){
							// 按价格排序 如果是店长则以推荐价格排序
							Double price1 = StringUtil.nullToDouble(productObj1.getRealSellPrice());
							Double price2 = StringUtil.nullToDouble(productObj2.getRealSellPrice());
							if(isAsc){
								sort =   price1.compareTo(price2);
							}else{
								sort =   -price1.compareTo(price2);
							}
						}
					}
					return sort;
				}
			}); 
			
			List<Long> productIdList = new ArrayList<Long> ();
			if(mappingList != null && mappingList.size() > 0){
				for(Map.Entry<Long, Product> entry : mappingList){
					productIdList.add(entry.getKey());
				}
			}

			/**
			 * 自动List分页工具
			 */
			ListPageUtil<Product> pageUtil = new ListPageUtil<Product> (){
				@Override
				public Product addObject(Long objectId) {
					// 返回对象自定义
					if(realProductMap.containsKey(objectId)){
						Product product = realProductMap.get(objectId);
						return product;
					}
					return null;
				}
			};

			/**
			 * 返回自动分页结果
			 */
			ListPageVo<List<Product>> listPageVo = pageUtil.getPageList(productIdList, lastId, pageidx, pagesize);
			if (StringUtil.nullToBoolean(listPageVo.getIsNextPageURL())) {
				StringBuffer urls = new StringBuffer (this.getRequestURL(request) + "&");
				urls.append("categoryId=" + categoryId + "&");
				urls.append("keyword=" + StringUtil.encode(keyword) + "&");
				urls.append("sort=" + sort + "&");
				urls.append("sortType=" + sortType + "&");
				urls.append("minPrice=" + minPrice + "&");
				urls.append("maxPrice=" + maxPrice + "&");
				urls.append("productType=" + productType + "&");
				urls.append("pageidx=" + (++pageidx) + "&");
				urls.append("lastId=" + listPageVo.getLastId() + "&");
				urls.append("pagesize=" + pagesize);
				tagModel.setNextPageURL(urls.toString());
			}

			Long total = Long.parseLong(productIdList.size() + "");
			Integer totalPage = ProductListTag.getPageNumber(total, pagesize);
			tagModel.setData(listPageVo.getDataList());
			tagModel.setPage(pageidx);
			tagModel.setPagesize(pagesize);
			tagModel.setTotal(total);
			tagModel.setTotalPage(totalPage);
		}catch(Exception e){
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("商品搜索页面耗时:"+DoubleUtil.divide(StringUtil.nullToDouble((endTime - startTime)), 1000d));
		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}
	
	/**
	 * 总页数
	 * @param totalNumber
	 * @param pagesize
	 * @return
	 */
	private static Integer getPageNumber(Long totalNumber, Integer pagesize) {
		Long pageNum = Long.parseLong(pagesize.toString());
		Long page = totalNumber / pageNum;
		if(totalNumber % pageNum > 0){
			page = page + 1;
		}
		return page.intValue();
	}
}

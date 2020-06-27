package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductBrand;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.ProductGroup;
import com.chunruo.core.model.ProductImage;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.ProductSpecType;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.service.ProductGroupManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.ProductSpecManager;
import com.chunruo.core.service.ProductSpecTypeManager;
import com.chunruo.core.util.BaseThreadPool;
import com.chunruo.core.util.DoubleUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;

@Service("productByIdCacheManager")
public class ProductByIdCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private ProductManager productManager;
	@Autowired
	private ProductSpecManager productSpecManager;
	@Autowired
	private ProductGroupManager productGroupManager;
	@Autowired
	private ProductSpecTypeManager productSpecTypeManager;
	@Autowired
	private ProductImageListByIdCacheManger productImageListByIdCacheManger;
	@Autowired
	private ProductListByCatIdCacheManager productListByCatIdCacheManager;
	@Autowired
	private ProductListByBrandIdCacheManager productListByBrandIdCacheManager;
	@Autowired
	private ProductListBySeckillIdCacheManager productListBySeckillIdCacheManager;

	public Product getSession(Long productId){
		Product product = this.productManager.getProductByProductIdAndIsDelete(productId, false);
		if(product != null && product.getProductId() != null){
			//防止修改字段后写入数据库内，托管态 -> 游离态
			this.productManager.detach(product);
			product.setSeckillLockNumber(0);
			
			//检查商品是否配有素材
			List<ProductImage> productImageList = this.productImageListByIdCacheManger.getSession(StringUtil.nullToLong(product.getProductId()), ProductImage.IMAGE_TYPE_MATERIAL);
            if(productImageList != null && productImageList.size() > 0) {
            	product.setIsHaveProductMaterial(true);
            }
            
            //商品分类集合列表
            product.setCategoryFidList(StringUtil.stringToLongArray(StringUtil.null2Str(product.getCategoryFids())));
            product.setCategoryIdList(StringUtil.stringToLongArray(StringUtil.null2Str(product.getCategoryIds())));
            product.setPaymentPrice(StringUtil.nullToDoubleFormat(product.getPriceRecommend()));
            
            // 商品产品类型及产地信息
           this.getProductType(product);
		   
			// 根据商品的属性查询信息
			if(StringUtil.nullToBoolean(product.getIsGroupProduct())){
				// 组合商品
				this.getGroupProdutInfo(product);
			}else if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
				// 规格商品
				this.getSpecProdutInfo(product);
			}
		}
		return product;
	}

	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productById_'+#productId")
	public void removeSession(Long productId) {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}

	public Product addSession(Long productId, Product product){
		return product;
	}

	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject ();
		final ProductByIdCacheManager productByIdCacheManager = Constants.ctx.getBean(ProductByIdCacheManager.class);
		List<Product> productList = productManager.getProductByUpdateTime(new Date(nextLastTime));
		if(productList != null && productList.size() > 0){
			cacheObject.setSize(productList.size());
			Date lastUpdateTime = null;
			List<Long> seckillIdList = new ArrayList<Long> ();
			for(final Product product : productList){
				if(lastUpdateTime == null || lastUpdateTime.before(product.getUpdateTime())){
					lastUpdateTime = product.getUpdateTime();
				}
				
				// 检查是否为秒杀商品
				if(product.getSeckillId() != null && !seckillIdList.contains(product.getSeckillId())){
					seckillIdList.add(product.getSeckillId());
				}

				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
					@Override
					public void run() {
						try{
							// 更新分销市场商品ID
							productByIdCacheManager.removeSession(product.getProductId());
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}

			// 秒杀商品缓存更新
			if(!CollectionUtils.isEmpty(seckillIdList)){
				for(Long seckillId : seckillIdList){
					try{
						productListBySeckillIdCacheManager.removeSession(seckillId);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
			// 分类缓存信息更新
			List<ProductCategory> categoryList = Constants.PRODUCT_CATEGORY_TREE_LIST;
			if(!CollectionUtils.isEmpty(categoryList)){
				try{
					productListByCatIdCacheManager.removeSession(0L);
				}catch(Exception e){
					e.printStackTrace();
				}

				for(ProductCategory categroy : categoryList){
					try{
						productListByCatIdCacheManager.removeSession(categroy.getCategoryId());
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}

			// 品牌缓存信息更新
			Map<Long, ProductBrand> brandList = Constants.PRODUCT_BRAND_MAP;
			if(brandList != null && brandList.size() > 0){
				try{
					productListByBrandIdCacheManager.removeSession(0L);
				}catch(Exception e){
					e.printStackTrace();
				}

				for(Long brandId : brandList.keySet()){
					try{
						productListByBrandIdCacheManager.removeSession(brandId);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			cacheObject.setLastUpdateTime(lastUpdateTime);
		}
		return cacheObject;
	}
	
	/**
	 * 获取组合商品信息
	 * @param product
	 */
	private void getGroupProdutInfo(Product product){
		List<ProductGroup> productGroupList = this.productGroupManager.getProductGroupListByProductGroupId(product.getProductId());
		if(productGroupList != null && productGroupList.size() > 0){
			this.productGroupManager.detach(productGroupList);
			Map<Long, List<ProductGroup>> productGroupListMap = new HashMap<Long, List<ProductGroup>> ();
			for(ProductGroup productGroup : productGroupList){
				
				if(productGroupListMap.containsKey(productGroup.getProductId())){
					productGroupListMap.get(productGroup.getProductId()).add(productGroup);
				}else{
					List<ProductGroup> list = new ArrayList<ProductGroup> ();
					list.add(productGroup);
					productGroupListMap.put(productGroup.getProductId(), list);
				}
			}
			
			// 获取组合价格区间
			Double minGroupPriceCost = 0d;
			Double maxGroupPriceWholesale = 0d;
			Double minGroupPriceWholesale = 0d;
			Double maxGroupPriceRecommend = 0d;
			Double minGroupPriceRecommend = 0d;
			Integer minGroupProfit = 0;
			Integer maxGroupProfit = 0;
			for (Entry<Long, List<ProductGroup>> entry : productGroupListMap.entrySet()) {
				List<ProductGroup> list = entry.getValue();
				if (StringUtil.nullToInteger(list.size()) == 1) {
					minGroupPriceCost = list.get(0).getGroupPriceCost();
					maxGroupPriceWholesale = DoubleUtil.add(maxGroupPriceWholesale, StringUtil.nullToDouble(list.get(0).getGroupPriceWholesale()));
					minGroupPriceWholesale = DoubleUtil.add(minGroupPriceWholesale, StringUtil.nullToDouble(list.get(0).getGroupPriceWholesale()));
					maxGroupPriceRecommend = DoubleUtil.add(maxGroupPriceRecommend, StringUtil.nullToDouble(list.get(0).getGroupPriceRecommend()));
					minGroupPriceRecommend = DoubleUtil.add(minGroupPriceRecommend, StringUtil.nullToDouble(list.get(0).getGroupPriceRecommend()));
					minGroupProfit = DoubleUtil.add(StringUtil.nullToDouble(minGroupProfit), DoubleUtil.sub(StringUtil.nullToDouble(list.get(0).getGroupPriceRecommend()), StringUtil.nullToDouble(list.get(0).getGroupPriceWholesale()))).intValue();
					maxGroupProfit = DoubleUtil.add(StringUtil.nullToDouble(minGroupProfit), DoubleUtil.sub(StringUtil.nullToDouble(list.get(0).getGroupPriceRecommend()), StringUtil.nullToDouble(list.get(0).getGroupPriceWholesale()))).intValue();
				}else {
					List<Double> groupPriceCostList = new ArrayList<Double> ();
					List<Double> groupPriceRecommendList = new ArrayList<Double> ();
					List<Double> groupPriceWholesaleList = new ArrayList<Double> ();
					List<Integer> profitList = new ArrayList<Integer>();
					for (ProductGroup group : list) {
						groupPriceCostList.add(StringUtil.nullToDouble(group.getGroupPriceCost()));
						groupPriceRecommendList.add(StringUtil.nullToDouble(group.getGroupPriceRecommend()));
						groupPriceWholesaleList.add(StringUtil.nullToDouble(group.getGroupPriceWholesale()));
						profitList.add(StringUtil.nullToDouble(DoubleUtil.sub(StringUtil.nullToDouble(group.getGroupPriceRecommend()), StringUtil.nullToDouble(group.getGroupPriceWholesale()))).intValue());
					}
					
					// 正排序
					Collections.sort(groupPriceCostList);
					Collections.sort(groupPriceRecommendList);
					Collections.sort(groupPriceWholesaleList);
					Collections.sort(profitList);
					minGroupPriceCost = groupPriceCostList.get(0);
					maxGroupPriceWholesale = DoubleUtil.add(maxGroupPriceWholesale, groupPriceWholesaleList.get(groupPriceWholesaleList.size() - 1));
					minGroupPriceWholesale = DoubleUtil.add(minGroupPriceWholesale, groupPriceWholesaleList.get(0));
					maxGroupPriceRecommend = DoubleUtil.add(maxGroupPriceRecommend, groupPriceRecommendList.get(groupPriceRecommendList.size() - 1));
					minGroupPriceRecommend = DoubleUtil.add(minGroupPriceRecommend, groupPriceRecommendList.get(0));

					minGroupProfit += profitList.get(0);
					maxGroupProfit += profitList.get(profitList.size() - 1);
				}
			}
			
			//市场价税费区间
			Double minGroupTaxWholesale= 0d;
			Double maxGroupTaxWholesale = 0d;
			//推荐价税费区间
			Double minGroupTaxRecommend = 0d;
			Double maxGroupTaxRecommend = 0d;
			
			//最低市场价税费
			MsgModel<Double> minGroupTaxWholesaleModel = getProductTax(minGroupPriceWholesale, product.getProductType(), product.getIsFreeTax());
			if(StringUtil.nullToBoolean(minGroupTaxWholesaleModel.getIsSucc())) {
				minGroupTaxWholesale = StringUtil.nullToDoubleFormat(minGroupTaxWholesaleModel.getData());
			}
			
			//最高市场价税费
			MsgModel<Double> maxGroupTaxWholesaleModel = getProductTax(maxGroupPriceWholesale, product.getProductType(), product.getIsFreeTax());
			if(StringUtil.nullToBoolean(maxGroupTaxWholesaleModel.getIsSucc())) {
				maxGroupTaxWholesale = StringUtil.nullToDoubleFormat(maxGroupTaxWholesaleModel.getData());
			}
			
			//最低推荐价税费
			MsgModel<Double> minGroupTaxRecommendModel = getProductTax(minGroupPriceRecommend, product.getProductType(), product.getIsFreeTax());
			if(StringUtil.nullToBoolean(maxGroupTaxWholesaleModel.getIsSucc())) {
				minGroupTaxRecommend = StringUtil.nullToDoubleFormat(minGroupTaxRecommendModel.getData());
			}
			
			//最高推荐价税费
			MsgModel<Double> maxGroupTaxRecommendModel = getProductTax(maxGroupPriceRecommend, product.getProductType(), product.getIsFreeTax());
			if(StringUtil.nullToBoolean(maxGroupTaxRecommendModel.getIsSucc())) {
				maxGroupTaxRecommend = StringUtil.nullToDoubleFormat(maxGroupTaxRecommendModel.getData());
			}
			
			//售价区间
			Double minSellPrice = DoubleUtil.add(minGroupTaxRecommend, minGroupPriceRecommend);
			Double maxSellPrice = DoubleUtil.add(maxGroupTaxRecommend, maxGroupPriceRecommend);

			// 推荐价格区间
			product.setGroupPriceRecommend(String.format("%s~%s", StringUtil.nullToDoubleFormat(minGroupPriceRecommend), StringUtil.nullToDoubleFormat(maxGroupPriceRecommend)));
			product.setGroupTaxRecommend(String.format("%s~%s", minGroupTaxRecommend,maxGroupTaxRecommend));
			product.setSellPriceRegion(String.format("%s~%s", minSellPrice,maxSellPrice));
			if(StringUtil.nullToDoubleFormat(minGroupPriceRecommend).compareTo(StringUtil.nullToDoubleFormat(maxGroupPriceRecommend)) == 0){
				product.setGroupPriceRecommend(StringUtil.nullToDoubleFormatStr(minGroupPriceRecommend));
			    product.setGroupTaxRecommend(StringUtil.nullToDoubleFormatStr(minGroupTaxRecommend));
				product.setSellPriceRegion(StringUtil.nullToDoubleFormatStr(minSellPrice));
			}
			
			// 市场价格区间
			product.setGroupPriceWholesale(String.format("%s~%s", StringUtil.nullToDoubleFormat(minGroupPriceWholesale), StringUtil.nullToDoubleFormat(maxGroupPriceWholesale)));
			product.setGroupTaxWholesale(String.format("%s~%s", StringUtil.nullToDoubleFormat(minGroupTaxWholesale),StringUtil.nullToDoubleFormat(maxGroupTaxWholesale)));
			if(StringUtil.nullToDoubleFormat(minGroupPriceWholesale).compareTo(StringUtil.nullToDoubleFormat(maxGroupPriceWholesale)) == 0){
				product.setGroupPriceWholesale(StringUtil.nullToDoubleFormatStr(minGroupPriceWholesale));
			    product.setGroupTaxWholesale(StringUtil.nullToDoubleFormatStr(minGroupTaxWholesale));
			}
			
			//利润区间
			product.setProductProfit(minGroupProfit);
			product.setProfitRegion(String.format("%s~%s", minGroupProfit,maxGroupProfit));
			if(minGroupProfit.compareTo(maxGroupProfit) == 0) {
				product.setProfitRegion(StringUtil.null2Str(minGroupProfit));
			}
			
			// 组合商品子商品记录信息
			product.setProductGroupListMap(productGroupListMap);
			product.setRealSellPrice(StringUtil.nullToDoubleFormat(minGroupPriceWholesale));
			product.setPriceRecommend(StringUtil.nullToDoubleFormat(minGroupPriceRecommend));
			product.setPriceCost(StringUtil.nullToDoubleFormat(minGroupPriceCost));
			product.setTagName("组合");
		}
	}
	
	/**
	 * 获取多规格商品信息
	 * @param product
	 */
	private void getSpecProdutInfo(Product product){
		List<ProductSpec> productSpecList = this.productSpecManager.getProductSpecListByProductId(product.getProductId());
		if(productSpecList != null && productSpecList.size() > 0){
			this.productSpecManager.detach(productSpecList);
			List<ProductSpecType> productSpecTypeList = this.productSpecTypeManager.getProductSpecTypeListByProductId(product.getProductId());
			if(productSpecTypeList != null && productSpecTypeList.size() > 0){
				// 商品规格标签
				List<String> productTagNameList = new ArrayList<String> ();
				Map<Long, ProductSpecType> productSpecTypeMap = new HashMap<Long, ProductSpecType> ();
				Map<String,String> tagMap = new HashMap<String,String>();
				for(ProductSpecType productSpecType : productSpecTypeList){
					String typeName = productSpecType.getSpecTypeName();
					productTagNameList.add(productSpecType.getSpecTypeName());
					productSpecTypeMap.put(productSpecType.getSpecTypeId(), productSpecType);
					if(!tagMap.containsKey(productSpecType.getSpecModelName())) {
						tagMap.put(productSpecType.getSpecModelName(), typeName);
					}else {
						tagMap.put(productSpecType.getSpecModelName(),tagMap.get(productSpecType.getSpecModelName()) + "/" + typeName);
					}
				}
				product.setTagName(StringUtil.stringArrayToString(productTagNameList));
				product.setTagMap(tagMap);
				// 整合商品规格信息
				for(ProductSpec productSpec : productSpecList){
					productSpec.setSeckillLockNumber(0);
					
					// 主规格类型
					if(productSpecTypeMap.containsKey(StringUtil.nullToLong(productSpec.getPrimarySpecId()))){
						ProductSpecType productSpecType = productSpecTypeMap.get(StringUtil.nullToLong(productSpec.getPrimarySpecId()));
						productSpec.setPrimarySpecName(productSpecType.getSpecTypeName());
						productSpec.setPrimarySpecModelName(productSpecType.getSpecModelName());
					}

					// 次规格类型
					if(productSpecTypeMap.containsKey(StringUtil.nullToLong(productSpec.getSecondarySpecId()))){
						ProductSpecType productSpecType = productSpecTypeMap.get(StringUtil.nullToLong(productSpec.getSecondarySpecId()));
						productSpec.setSecondarySpecName(productSpecType.getSpecTypeName());
						productSpec.setSecondarySpecModelName(productSpecType.getSpecModelName());
					}
					
				}
			}
		}
		product.setProductSpecList(productSpecList);
	}
	
	/**
	 * 根据仓库查询商品类型、仓库模板
	 * @param warehouseId
	 * @return
	 */
	public void getProductType(Product product){
		try{
			ProductWarehouse productWarehouse = getProductWarehouse(StringUtil.nullToLong(product.getWareHouseId()));
			if(productWarehouse != null && productWarehouse.getWarehouseId() != null){
				product.setProductType(StringUtil.nullToInteger(productWarehouse.getProductType()));
				product.setWareHouseTemplateId(StringUtil.nullToLong(productWarehouse.getTemplateId()));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据仓库查询商品类型
	 * @param warehouseId
	 * @return
	 */
	public  ProductWarehouse getProductWarehouse(Long warehouseId){
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
	 * 计算商品税费
	 * @param product
	 * @return
	 */
	public static MsgModel<Double> getProductTax(Double paymentPrice,Integer productType,Boolean isFreeTax){
		MsgModel<Double> msgModel = new MsgModel<Double>();
		try {
			List<Integer> productTypeList = new ArrayList<Integer>();
			productTypeList.add(GoodsType.GOODS_TYPE_CROSS);   //跨境
			productTypeList.add(GoodsType.GOODS_TYPE_DIRECT);  //直邮

			Double tax = new Double(0D);
			//检查商品是否包税
			if(!StringUtil.nullToBoolean(isFreeTax)) {
				if(productTypeList.contains(StringUtil.nullToInteger(productType))) {
					//跨境商品税费
					tax = DoubleUtil.mul(paymentPrice,Product.TAXRATE);
				}
//				else if(StringUtil.compareObject(productType, GoodsType.GOODS_TYPE_DIRECT_GO)) {
//					//行邮
//					Double goTax = StringUtil.nullToDouble(DoubleUtil.mul(0.13D, paymentPrice));
//					if(goTax.compareTo(50D) > 0) {
//						tax = DoubleUtil.sub(goTax, 50D);
//					}
//				}
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
}

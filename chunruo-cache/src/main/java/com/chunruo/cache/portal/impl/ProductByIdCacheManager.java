package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.chunruo.core.model.ProductImage;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.ProductSpecType;
import com.chunruo.core.model.ProductWarehouse;
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
	private ProductSpecTypeManager productSpecTypeManager;
	@Autowired
	private ProductImageListByIdCacheManger productImageListByIdCacheManger;
	@Autowired
	private ProductListByCatIdCacheManager productListByCatIdCacheManager;
	@Autowired
	private ProductListByBrandIdCacheManager productListByBrandIdCacheManager;
	@Autowired
	private ProductListBySeckillIdCacheManager productListBySeckillIdCacheManager;

	@Cacheable(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productById_'+#productId")
	public Product getSession(Long productId){
		Product product = this.productManager.getProductByProductIdAndIsDelete(productId, false);
		if(product != null && product.getProductId() != null){
			//??????????????????????????????????????????????????? -> ?????????
			this.productManager.detach(product);
			product.setSeckillLockNumber(0);
			
			//??????????????????????????????
			List<ProductImage> productImageList = this.productImageListByIdCacheManger.getSession(StringUtil.nullToLong(product.getProductId()), ProductImage.IMAGE_TYPE_MATERIAL);
            if(productImageList != null && productImageList.size() > 0) {
            	product.setIsHaveProductMaterial(true);
            }
            
            //????????????????????????
            product.setCategoryFidList(StringUtil.stringToLongArray(StringUtil.null2Str(product.getCategoryFids())));
            product.setCategoryIdList(StringUtil.stringToLongArray(StringUtil.null2Str(product.getCategoryIds())));
            product.setPaymentPrice(StringUtil.nullToDoubleFormat(product.getPriceRecommend()));
            
            // ?????????????????????????????????
           this.getProductType(product);
		   
			if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
				// ????????????
				this.getSpecProdutInfo(product);
			}
		}
		return product;
	}

	@CacheEvict(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productById_'+#productId")
	public void removeSession(Long productId) {
		//???????????????????????????????????????????????????
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}

	@CachePut(value="sessionEhRedisCache", cacheManager="sessionEhRedisCacheManager", key="'productById_'+#productId")
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
				
				// ???????????????????????????
				if(product.getSeckillId() != null && !seckillIdList.contains(product.getSeckillId())){
					seckillIdList.add(product.getSeckillId());
				}

				BaseThreadPool.getThreadPoolExecutor().execute(new Runnable(){
					@Override
					public void run() {
						try{
							// ????????????????????????ID
							productByIdCacheManager.removeSession(product.getProductId());
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				});
			}

			// ????????????????????????
			if(!CollectionUtils.isEmpty(seckillIdList)){
				for(Long seckillId : seckillIdList){
					try{
						productListBySeckillIdCacheManager.removeSession(seckillId);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
			// ????????????????????????
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

			// ????????????????????????
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
	 * ???????????????????????????
	 * @param product
	 */
	private void getSpecProdutInfo(Product product){
		List<ProductSpec> productSpecList = this.productSpecManager.getProductSpecListByProductId(product.getProductId());
		if(productSpecList != null && productSpecList.size() > 0){
			this.productSpecManager.detach(productSpecList);
			List<ProductSpecType> productSpecTypeList = this.productSpecTypeManager.getProductSpecTypeListByProductId(product.getProductId());
			if(productSpecTypeList != null && productSpecTypeList.size() > 0){
				// ??????????????????
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
				// ????????????????????????
				for(ProductSpec productSpec : productSpecList){
					productSpec.setSeckillLockNumber(0);
					
					// ???????????????
					if(productSpecTypeMap.containsKey(StringUtil.nullToLong(productSpec.getPrimarySpecId()))){
						ProductSpecType productSpecType = productSpecTypeMap.get(StringUtil.nullToLong(productSpec.getPrimarySpecId()));
						productSpec.setPrimarySpecName(productSpecType.getSpecTypeName());
						productSpec.setPrimarySpecModelName(productSpecType.getSpecModelName());
					}

					// ???????????????
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
	 * ?????????????????????????????????????????????
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
	 * ??????????????????????????????
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
	 * ??????????????????
	 * @param product
	 * @return
	 */
	public static MsgModel<Double> getProductTax(Double paymentPrice,Integer productType,Boolean isFreeTax){
		MsgModel<Double> msgModel = new MsgModel<Double>();
		try {
			List<Integer> productTypeList = new ArrayList<Integer>();
			productTypeList.add(GoodsType.GOODS_TYPE_CROSS);   //??????
			productTypeList.add(GoodsType.GOODS_TYPE_DIRECT);  //??????

			Double tax = new Double(0D);
			if(!StringUtil.nullToBoolean(isFreeTax)) {
				if(productTypeList.contains(StringUtil.nullToInteger(productType))) {
					tax = DoubleUtil.mul(paymentPrice,Product.TAXRATE);
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
}

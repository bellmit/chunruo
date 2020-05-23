package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductCollection;
import com.chunruo.core.model.ProductCollectionProfit;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.repository.ProductCollectionRepository;
import com.chunruo.core.service.ProductCollectionManager;
import com.chunruo.core.service.ProductCollectionProfitManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("productCollectionManager")
public class ProductCollectionManagerImpl extends GenericManagerImpl<ProductCollection, Long> implements ProductCollectionManager{
	private ProductCollectionRepository productCollectionRepository;
	
	@Autowired
	private ProductCollectionProfitManager productCollectionProfitManager;
	
	@Autowired
	public ProductCollectionManagerImpl(ProductCollectionRepository productCollectionRepository) {
		super(productCollectionRepository);
		this.productCollectionRepository = productCollectionRepository;
	}

	@Override
	public List<ProductCollection> getProductCollectionListByUserId(Long userId, boolean status) {
		return this.productCollectionRepository.getProductCollectionListByUserId(userId, status);
	}

	@Override
	public List<ProductCollection> getProductCollectionListByUpdateTime(Date updateTime) {
		return this.productCollectionRepository.getProductCollectionListByUpdateTime(updateTime);
	}

	@Override
	public ProductCollection getProductCollectionByProductId(Long productId, Long userId) {
		List<ProductCollection> productCollectionList = this.productCollectionRepository.getProductCollectionByProductId(productId,userId);
		return (productCollectionList != null && productCollectionList.size() > 0) ? productCollectionList.get(0) : null;
	}

	@Override
	public void saveProductCollection(Product product, Long userId, Boolean status) {
		if(product != null && product.getProductId() != null) {
			Long productId = StringUtil.nullToLong(product.getProductId());
			// 查找数据库
			ProductCollection productCollection = this.getProductCollectionByProductId(productId, userId);
			if(productCollection != null && productCollection.getCollectionId() != null){
				// 已存在更新代理状态
				productCollection.setStatus(status); 
				productCollection.setUpdateTime(DateUtil.getCurrentDate());
				this.save(productCollection);
			}else{
				// 代理商品不存在,新增代理商品
				if(StringUtil.nullToBoolean(status)){
					productCollection = new ProductCollection ();
					productCollection.setUserId(userId);
					productCollection.setProductId(productId);
					productCollection.setStatus(true);
					productCollection.setCreateTime(DateUtil.getCurrentDate());
					productCollection.setUpdateTime(productCollection.getCreateTime());
					this.save(productCollection);
				}
			}
			
			//非组合商品并且是收藏操作
			if(!StringUtil.nullToBoolean(product.getIsGroupProduct()) && StringUtil.nullToBoolean(status)) {
				//收藏时，默认代理利润为推荐价 - 市场价
				List<ProductCollectionProfit> collectionProfitList = this.productCollectionProfitManager.getCollectionProfitListByUserIdAndProductId(userId, productId);
				if(collectionProfitList == null || collectionProfitList.size() <= 0) {
					//新建利润记录
					collectionProfitList = new ArrayList<ProductCollectionProfit>();
					if(!StringUtil.nullToBoolean(product.getIsSpceProduct())) {
						//非规格商品
						Double priceRecommend = StringUtil.nullToDoubleFormat(product.getPriceRecommend()); 	//商品推荐价格
						Double priceWholesale = StringUtil.nullToDoubleFormat(product.getRealSellPrice()); 	//商品市场价格
						if(StringUtil.nullToBoolean(product.getIsSeckillProduct())
								&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())) {
							//秒杀商品
							priceWholesale = StringUtil.nullToDoubleFormat(product.getSeckillPrice());          //秒杀价
						}
						ProductCollectionProfit collectionProfit = new ProductCollectionProfit();
						collectionProfit.setProductId(product.getProductId());
						collectionProfit.setProfit( StringUtil.nullToDoubleFormat((priceRecommend - priceWholesale)).intValue());
						collectionProfit.setUserId(userId);
						collectionProfit.setCreateTime(DateUtil.getCurrentDate());
						collectionProfit.setUpdateTime(collectionProfit.getCreateTime());
						collectionProfitList.add(collectionProfit);
						product.setProductProfit(collectionProfit.getProfit());
					}else {
						//规格商品
						List<ProductSpec> productSpecList = product.getProductSpecList();
						if(productSpecList != null && productSpecList.size() > 0) {
							for(ProductSpec productSpec : productSpecList) {
								Double priceRecommend = StringUtil.nullToDoubleFormat(productSpec.getPriceRecommend()); 	//商品推荐价格
								Double priceWholesale = StringUtil.nullToDoubleFormat(productSpec.getRealSellPrice()); 	//商品市场价格
								if(StringUtil.nullToBoolean(product.getIsSeckillProduct())
										&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())) {
									//秒杀商品
									priceWholesale = StringUtil.nullToDoubleFormat(productSpec.getSeckillPrice());          //秒杀价
								}
								ProductCollectionProfit collectionProfit = new ProductCollectionProfit();
								collectionProfit.setProductId(product.getProductId());
								collectionProfit.setProductSpecId(productSpec.getProductSpecId());
								collectionProfit.setProfit( StringUtil.nullToDoubleFormat((priceRecommend - priceWholesale)).intValue());
								collectionProfit.setUserId(userId);
								collectionProfit.setCreateTime(DateUtil.getCurrentDate());
								collectionProfit.setUpdateTime(collectionProfit.getCreateTime());
								collectionProfitList.add(collectionProfit);
								productSpec.setProductProfit(collectionProfit.getProfit());
							}
						}
					}
				}else {
					//更新利润记录
					if(!StringUtil.nullToBoolean(product.getIsSpceProduct())) {
						ProductCollectionProfit productCollectionProfit = collectionProfitList.get(0);
						//非规格商品
						Double priceRecommend = StringUtil.nullToDoubleFormat(product.getPriceRecommend()); 	//商品推荐价格
						Double priceWholesale = StringUtil.nullToDoubleFormat(product.getRealSellPrice()); 	//商品市场价格
						if(StringUtil.nullToBoolean(product.getIsSeckillProduct())
								&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())) {
							//秒杀商品
							priceWholesale = StringUtil.nullToDoubleFormat(product.getSeckillPrice());          //秒杀价
						}
						productCollectionProfit.setProfit( StringUtil.nullToDoubleFormat((priceRecommend - priceWholesale)).intValue());
						productCollectionProfit.setUpdateTime(DateUtil.getCurrentDate());
						product.setProductProfit(productCollectionProfit.getProfit());
					}else {
						//规格商品
						Map<Long,ProductCollectionProfit>  profitMap = new HashMap<Long,ProductCollectionProfit>();
						for (ProductCollectionProfit collectionProfit : collectionProfitList) {
							 profitMap.put(StringUtil.nullToLong(collectionProfit.getProductSpecId()), collectionProfit);
						}
						
						List<ProductSpec> productSpecList = product.getProductSpecList();
						if(productSpecList != null && productSpecList.size() > 0) {
							for(ProductSpec productSpec : productSpecList) {
								Double priceRecommend = StringUtil.nullToDoubleFormat(productSpec.getPriceRecommend()); 	//商品推荐价格
								Double priceWholesale = StringUtil.nullToDoubleFormat(productSpec.getRealSellPrice()); 	//商品市场价格
								if(StringUtil.nullToBoolean(product.getIsSeckillProduct())
										&& !StringUtil.nullToBoolean(product.getIsSeckillReadStatus())) {
									//秒杀商品
									priceWholesale = StringUtil.nullToDoubleFormat(productSpec.getSeckillPrice());          //秒杀价
								}
								
								ProductCollectionProfit collectionProfit = profitMap.get(StringUtil.nullToLong(productSpec.getProductSpecId()));
							    if(collectionProfit == null || collectionProfit.getProfitId() == null) {
							    	collectionProfit = new ProductCollectionProfit();
									collectionProfit.setProductId(product.getProductId());
									collectionProfit.setProductSpecId(productSpec.getProductSpecId());
									collectionProfit.setProfit( StringUtil.nullToDoubleFormat((priceRecommend - priceWholesale)).intValue());
									collectionProfit.setUserId(userId);
									collectionProfit.setCreateTime(DateUtil.getCurrentDate());
									collectionProfit.setUpdateTime(collectionProfit.getCreateTime());
									collectionProfitList.add(collectionProfit);
							    }else {
							    	collectionProfit.setProfit(StringUtil.nullToInteger(collectionProfit.getProfit()));
							    	collectionProfit.setUpdateTime(DateUtil.getCurrentDate());
							    }
							    productSpec.setProductProfit(collectionProfit.getProfit());
							}
						}
					}
				}
				//批量保存
				this.productCollectionProfitManager.batchInsert(collectionProfitList, collectionProfitList.size());
			}
		}
	}
}

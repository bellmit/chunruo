package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductSeckill;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.repository.ProductSeckillRepository;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.ProductSeckillManager;
import com.chunruo.core.service.ProductSpecManager;
import com.chunruo.core.util.DateUtil;

@Transactional
@Component("productSeckillManager")
public class ProductSeckillManagerImpl extends GenericManagerImpl<ProductSeckill, Long> implements ProductSeckillManager{
	private ProductSeckillRepository productSeckillRepository;
	@Autowired
	private ProductManager productManager;
	@Autowired
	private ProductSpecManager productSpecManager;

	@Autowired
	public ProductSeckillManagerImpl(ProductSeckillRepository productSeckillRepository) {
		super(productSeckillRepository);
		this.productSeckillRepository = productSeckillRepository;
	}

	@Override
	public List<ProductSeckill> getProductSeckillListByStatus(boolean status) {
		return this.productSeckillRepository.getProductSeckillListByStatus(status);
	}

	@Override
	public List<ProductSeckill> getProductSeckillByUpdateTime(Date updateTime) {
		return this.productSeckillRepository.getProductSeckillByUpdateTime(updateTime);
	}

	@Override
	public void saveSeckillProduct(Product product, ProductSeckill seckill, List<ProductSpec> productSpecList) {
		//秒杀场次
		seckill.setUpdateTime(DateUtil.getCurrentDate());
		this.save(seckill);
		
		//秒杀商品
		product.setUpdateTime(DateUtil.getCurrentDate());
		this.productManager.save(product);
		
		// 更新规格信息
		if(productSpecList != null && productSpecList.size() > 0){
			this.productSpecManager.batchInsert(productSpecList, productSpecList.size());
		}
	}

	@Override
	public void deleteSeckillProduct(List<Product> productList, List<Long> productIdList, List<Long> seckillIdList) {
		// 更新商品
		this.productManager.batchInsert(productList, productList.size());
		
		// 更新规格商品
		if(productIdList != null && productIdList.size() > 0){
			List<ProductSpec> productSpecList = this.productSpecManager.getProductSpecListByProductIdList(productIdList);
			if(productSpecList != null && productSpecList.size() > 0){
				for(ProductSpec productSpec : productSpecList){
					productSpec.setSeckillPrice(null);
					productSpec.setSeckillProfit(null);
					productSpec.setSeckillTotalStock(null);
					productSpec.setUpdateTime(DateUtil.getCurrentDate());
				}
				this.productSpecManager.batchInsert(productSpecList, productSpecList.size());
			}
		}
		
		// 更新秒杀场次
		if(seckillIdList != null && seckillIdList.size() > 0){
			List<ProductSeckill> seckillList = this.getByIdList(seckillIdList);
			if(seckillList != null && seckillList.size() > 0){
				for(ProductSeckill productSeckill : seckillList){
					productSeckill.setUpdateTime(DateUtil.getCurrentDate());
				}
				this.batchInsert(seckillList, seckillList.size());
			}
		}
	}
}

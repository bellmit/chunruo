package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductGroup;
import com.chunruo.core.model.ProductImage;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.ProductSpecType;

public interface ProductManager extends GenericManager<Product, Long>{
	
	public List<Product> getProductList(boolean status);
	
	public List<Product> getProductListByCategoryId(Long categoryId);
	
	public List<Product> getProductListByCategoryId(Long categoryId, boolean status);
	
	public List<Product> getProductListByCategoryIdList(List<Long> categoryId, boolean status);
	
	public List<Product> getProductListByCategoryFid(Long categoryFid);
	
	public List<Product> getProductListByCategoryFid(Long categoryFid, boolean status);
	
	public void updateProductNumber(Long productId, Integer number, Boolean isSoldout);
	
	public void updateProductStatus(List<Long> idList, boolean status);
	
	public void updateProductSoldoutStatus(List<Long> idList, Boolean isSoldout);
	
	public void updateProductSoldoutStatus(Long productId, Boolean isSoldout);
	
	public List<Product> getProductByUpdateTime(Date updateTime);
	
	public void setProductToPackage(Long productId);

	public Product getProductByIsPackage();
	
	public List<Product> getProductListByProudctIdList(List<Long> productIdList);
	
	public List<Product> getProductListByStatusAndIsSoldout(boolean status, boolean isSoldout);
	
	public Product saveProduct(Product product, List<ProductSpec> productSpecList, List<ProductSpecType> primarySpecList, List<ProductSpecType> secondarySpecList, List<ProductGroup> productGroupList, List<ProductImage> imageList,List<Product> aggrProductList);

	public List<Product> getProductListByBrandId(Long brandId, boolean status);
	
	public List<Product> getProductListByBrandId(Long brandId);
	
	public List<Product> getProductListBySeckillId(Long seckillId);
	
	public void deleteProduct(List<Long> idList, Boolean isDelete);
	
	public Product getProductByAssemblyModelId(Long assemblyModelId);

	public void updateProductSeckillTotalNumber(Long productId, Integer seckillTotalNumber);

	public Product getProductByIsGuideProduct(Boolean isGuideProduct);

	public List<Product> getProductListByIsOpenV2Price(Boolean isOpenV2Price);
	
	public List<Product> getProductListByIsOpenV3Price(Boolean isOpenV3Price);

	public List<Product> getProductListByIsOpenVPrice();

	public List<Product> getProductListByTagIdInField(Long tagId);

	public Product getProductByProductIdAndIsDelete(Long productId,Boolean isDelete);

	public List<Object[]> getProductStockNumberByProductIdList(List<Long> productIdList);

	public void updateProductShowStatus(List<Long> idList, Boolean isEnabled);

	public void updateProductRepStatus(List<Long> idList);
}

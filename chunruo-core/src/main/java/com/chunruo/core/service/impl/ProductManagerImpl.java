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
import com.chunruo.core.model.ProductGroup;
import com.chunruo.core.model.ProductImage;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.ProductSpecType;
import com.chunruo.core.repository.ProductRepository;
import com.chunruo.core.service.ProductGroupManager;
import com.chunruo.core.service.ProductImageManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.ProductSpecManager;
import com.chunruo.core.service.ProductSpecTypeManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("productManager")
public class ProductManagerImpl extends GenericManagerImpl<Product, Long> implements ProductManager {
	private ProductRepository productRepository;
	@Autowired
	private ProductImageManager productImageManager;
	@Autowired
	private ProductSpecTypeManager productSpecTypeManager;
	@Autowired
	private ProductSpecManager productSpecManager;
	@Autowired
	private ProductGroupManager productGroupManager;

	@Autowired
	public ProductManagerImpl(ProductRepository productRepository) {
		super(productRepository);
		this.productRepository = productRepository;
	}

	@Override
	public List<Product> getProductList(boolean status) {
		return this.productRepository.getProductList(status);
	}

	@Override
	public List<Product> getProductByUpdateTime(Date updateTime) {
		return this.productRepository.getProductByUpdateTime(updateTime);
	}

	@Override
	public List<Product> getProductListByCategoryId(Long categoryId) {
		return this.productRepository.getProductListByCategoryId(categoryId);
	}

	@Override
	public List<Product> getProductListByCategoryId(Long categoryId, boolean status) {
		String hql = String.format("from Product where FIND_IN_SET(%s,categoryIds) > 0 and status = %s and isDelete=false and isShow=true", categoryId,status);
		return this.query(hql);
	}
	
	@Override
	public List<Product> getProductListByCategoryIdList(List<Long> categoryId, boolean status) {
		return this.productRepository.getProductListByCategoryIdList(categoryId, status);
	}

	@Override
	public List<Product> getProductListByCategoryFid(Long categoryFid) {
		return this.productRepository.getProductListByCategoryFid(categoryFid);
	}

	@Override
	public List<Product> getProductListByCategoryFid(Long categoryFid, boolean status) {
		String hql = String.format("from Product where FIND_IN_SET(%s,categoryFids) > 0 and status = %s and isDelete=false and isShow=true", categoryFid,status);
		return this.query(hql);
	}

	@Override
	public void updateProductNumber(Long productId, Integer number, Boolean isSoldout) {
		this.productRepository.updateProductNumber(number, isSoldout, productId, DateUtil.getCurrentDate());
	}

	@Override
	public void updateProductStatus(List<Long> idList, boolean status) {
		if (idList != null && idList.size() > 0) {
			this.productRepository.updateProductStatus(idList, status, DateUtil.getCurrentDate());
		}
	}

	@Override
	public void updateProductSoldoutStatus(List<Long> idList, Boolean isSoldout) {
		if (idList != null && idList.size() > 0) {
			this.productRepository.updateProductSoldoutStatus(idList, isSoldout, DateUtil.getCurrentDate());
		}
		
		Map<Long,Product> productMap = new HashMap<Long,Product>();
		List<Product> productList = this.getByIdList(idList);
		if(productList != null && !productList.isEmpty()) {
			for(Product product : productList) {
				if(!StringUtil.nullToBoolean(product.getIsSpceProduct())) {
					product.setPaymentStockNumber(StringUtil.nullToInteger(product.getStockNumber()));
				}
				productMap.put(StringUtil.nullToLong(product.getProductId()), product);
			}
		}
		
		Map<Long,ProductSpec> productSpecMap = new HashMap<Long,ProductSpec>();
		List<ProductSpec> productSpecList = this.productSpecManager.getProductSpecListByProductIdList(idList);
		if(productSpecList != null && !productSpecList.isEmpty()) {
			for(ProductSpec productSpec : productSpecList) {
				Product product = productMap.get(StringUtil.nullToLong(productSpec.getProductId()));
				if(product != null && product.getProductId() != null) {
					product.setPaymentStockNumber(StringUtil.nullToInteger(product.getPaymentStockNumber()) + StringUtil.nullToInteger(productSpec.getStockNumber()));
				}
				productSpecMap.put(StringUtil.nullToLong(productSpec.getProductSpecId()), productSpec);
			}
		}

	}
	
	@Override
	public void updateProductSoldoutStatus(Long productId, Boolean isSoldout){
		this.productRepository.updateProductSoldoutStatus(productId, isSoldout, DateUtil.getCurrentDate());
	}
	
	@Override
	public List<Product> getProductListBySeckillId(Long seckillId) {
		return this.productRepository.getProductListBySeckillId(seckillId);
	}

	@Override
	public void setProductToPackage(Long productId) {
		// 将原来的大礼包设置为普通商品
		this.productRepository.updateProductPackageToFalse();
		// 将当前的商品设置为大礼包
		this.productRepository.updateProductPackageToTrue(productId);
	}

	@Override
	public Product getProductByIsPackage() {
		List<Product> productList = this.productRepository.getProductListByIsPackage();
		if (productList != null & productList.size() > 0) {
			return productList.get(0);
		}
		return null;
	}

	@Override
	public List<Product> getProductListByStatusAndIsSoldout(boolean status, boolean isSoldout) {
		return this.productRepository.getProductListByStatusAndIsSoldout(status, isSoldout);
	}
	
	@Override
	public Product saveProduct(Product product, List<ProductSpec> productSpecList, List<ProductSpecType> primarySpecList, List<ProductSpecType> secondarySpecList, List<ProductGroup> productGroupList, List<ProductImage> imageList,List<Product> aggrProductList){
		boolean isNewProduct = (product.getProductId() == null);
		boolean isMoreSpecProduct = StringUtil.nullToBoolean(product.getIsMoreSpecProduct());
		product.setUpdateTime(DateUtil.getCurrentDate());
		product = this.save(product);
		
		// 图片列表保存
		imageList = this.productImageManager.saveAndDelProductImage(product.getProductId(), ProductImage.IMAGE_TYPE_HEADER, imageList);
		if(imageList != null && imageList.size() > 0){
			product.setImage(imageList.get(0).getImagePath());
			this.save(product);
		}
		
		if(aggrProductList != null && !aggrProductList.isEmpty()) {
			this.batchInsert(aggrProductList, aggrProductList.size());
		}
		
		if(StringUtil.nullToBoolean(product.getIsGroupProduct())){
			// 商品规格信息保存
			if(isNewProduct){
				// 初始化信息
				for(ProductGroup productGroup : productGroupList){
					productGroup.setProductGroupId(product.getProductId());
					productGroup.setCreateTime(DateUtil.getCurrentDate());
					productGroup.setUpdateTime(productGroup.getCreateTime());
				}
				this.productGroupManager.batchInsert(productGroupList, productGroupList.size());
			}else{
				Map<Long, List<ProductGroup>> productGroupListMap = new HashMap<Long, List<ProductGroup>> ();
				for(ProductGroup productGroup : productGroupList){
					// 初始化信息
					productGroup.setProductGroupId(product.getProductId());
					productGroup.setUpdateTime(DateUtil.getCurrentDate());
					
					// 按商品的归类
					if(productGroupListMap.containsKey(productGroup.getProductId())){
						productGroupListMap.get(productGroup.getProductId()).add(productGroup);
					}else{
						List<ProductGroup> list = new ArrayList<ProductGroup> ();
						list.add(productGroup);
						productGroupListMap.put(productGroup.getProductId(), list);
					}
				}
				
				// 查询数据库已存在的组合商品记录
				List<Long> deleteGroupIdList = new ArrayList<Long> ();
				List<ProductGroup> existGroupList = this.productGroupManager.getProductGroupListByProductGroupId(product.getProductId());
				if(existGroupList != null && existGroupList.size() > 0){
					for(ProductGroup tmpProductGroup : existGroupList){
						boolean isExistGroupProduct = false;
						if(productGroupListMap.containsKey(tmpProductGroup.getProductId())){
							List<ProductGroup> list = productGroupListMap.get(tmpProductGroup.getProductId());
							for(ProductGroup productGroup : list){
								if(StringUtil.nullToBoolean(productGroup.getIsSpceProduct())){
									// 规格商品
									if(StringUtil.compareObject(productGroup.getProductSpecId(), tmpProductGroup.getProductSpecId())){
										isExistGroupProduct = true;
										productGroup.setGroupId(tmpProductGroup.getGroupId());
										productGroup.setCreateTime(tmpProductGroup.getCreateTime());
									}
								}else{
									isExistGroupProduct = true;
									productGroup.setGroupId(tmpProductGroup.getGroupId());
									productGroup.setCreateTime(tmpProductGroup.getCreateTime());
								}
							}
						}
						
						// 删除的组合商品记录
						if(!isExistGroupProduct){
							deleteGroupIdList.add(tmpProductGroup.getGroupId());
						}
					}
				}
				
				this.productGroupManager.batchInsert(productGroupList, productGroupList.size());
				this.productGroupManager.deleteByIdList(deleteGroupIdList);
			}
		}else if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
			// 商品规格信息保存
			if(isNewProduct){
				// 新建商品规格
				if(primarySpecList != null && primarySpecList.size() > 0){
					primarySpecList.addAll(secondarySpecList);
					for(ProductSpecType productSpecType : primarySpecList){
						productSpecType.setProductId(product.getProductId());
					}
					
					// 规格类型批量保存
					List<ProductSpecType> productSpecTypeList = this.productSpecTypeManager.batchInsert(primarySpecList, primarySpecList.size());
					if(productSpecTypeList != null && productSpecTypeList.size() > 0){
						Map<String, ProductSpecType> productSpecTypeIdMap = new HashMap<String, ProductSpecType> ();
						for(ProductSpecType productSpecType : productSpecTypeList){
							productSpecTypeIdMap.put(productSpecType.getTmpSpecTypeId(), productSpecType);
						}
						
						// 规格批量保存
						if(productSpecList != null && productSpecList.size() > 0){
							Date currentDate = DateUtil.getCurrentDate();
							List<ProductSpec> saveProductSpecList = new ArrayList<ProductSpec> ();
							for(ProductSpec productSpec : productSpecList){
								productSpec.setProductId(product.getProductId());
								productSpec.setCreateTime(currentDate);
								productSpec.setUpdateTime(currentDate);
								
								// 商品主规格
								if(StringUtil.isNull(productSpec.getTmpPrimarySpecId())){
									continue;
								}else if(!productSpecTypeIdMap.containsKey(productSpec.getTmpPrimarySpecId())){
									continue;
								}
								
								ProductSpecType primaryProductSpecType = productSpecTypeIdMap.get(productSpec.getTmpPrimarySpecId());
								productSpec.setPrimarySpecId(primaryProductSpecType.getSpecTypeId());
								productSpec.setPrimarySpecName(primaryProductSpecType.getSpecTypeName());
								productSpec.setPrimarySpecModelName(primaryProductSpecType.getSpecModelName());
								productSpec.setSpecImagePath(StringUtil.null2Str(primaryProductSpecType.getImagePath()));
								productSpec.setProductTags(primaryProductSpecType.getSpecTypeName());
								
								// 商品次规格
								if(isMoreSpecProduct){
									if(StringUtil.isNull(productSpec.getTmpSecondarySpecId())){
										continue;
									}else if(!productSpecTypeIdMap.containsKey(productSpec.getTmpSecondarySpecId())){
										continue;
									}
									
									ProductSpecType secondaryProductSpecType = productSpecTypeIdMap.get(productSpec.getTmpSecondarySpecId());
									productSpec.setSecondarySpecId(secondaryProductSpecType.getSpecTypeId());
									productSpec.setSecondarySpecName(secondaryProductSpecType.getSpecTypeName());
									productSpec.setSecondarySpecModelName(secondaryProductSpecType.getSpecModelName());
									productSpec.setProductTags(String.format("%s、%s", primaryProductSpecType.getSpecTypeName(), secondaryProductSpecType.getSpecTypeName()));
								}
								saveProductSpecList.add(productSpec);
							}
							this.productSpecManager.batchInsert(saveProductSpecList, saveProductSpecList.size());
							product.setProductSpecList(saveProductSpecList);
						}
					}
				}
			}else{
				// 编辑商品规格
				Map<Long, ProductSpecType> allProductSpecTypeMap = new HashMap<Long, ProductSpecType> ();
				List<ProductSpecType> dbProductSpecTypeList = this.productSpecTypeManager.getProductSpecTypeListByProductId(product.getProductId());
				if(dbProductSpecTypeList != null && dbProductSpecTypeList.size() > 0){
					for(ProductSpecType productSpecType : dbProductSpecTypeList){
						allProductSpecTypeMap.put(productSpecType.getSpecTypeId(), productSpecType);
					}
				}
				
				// 新增和编辑商品规格类型数据合并
				Map<String, ProductSpecType> saveProductSpecTypeMap = new HashMap<String, ProductSpecType> ();
				if(primarySpecList != null && primarySpecList.size() > 0){
					primarySpecList.addAll(secondarySpecList);
					Date currentDate = DateUtil.getCurrentDate();
					for(ProductSpecType productSpecType : primarySpecList){
						productSpecType.setProductId(product.getProductId());
						if(productSpecType.getSpecTypeId() != null && allProductSpecTypeMap.containsKey(productSpecType.getSpecTypeId())){
							ProductSpecType dbProductSpecType = allProductSpecTypeMap.get(productSpecType.getSpecTypeId());
							productSpecType.setTmpSpecTypeId(StringUtil.null2Str(productSpecType.getSpecTypeId()));
							productSpecType.setCreateTime(dbProductSpecType.getCreateTime());
							productSpecType.setUpdateTime(currentDate);
						}else{
							productSpecType.setSpecTypeId(null);
							productSpecType.setCreateTime(currentDate);
							productSpecType.setUpdateTime(currentDate);
						}
					}
					
					// 规格类型批量保存
					List<ProductSpecType> productSpecTypeList = this.productSpecTypeManager.batchInsert(primarySpecList, primarySpecList.size());
					if(productSpecTypeList != null && productSpecTypeList.size() > 0){
						for(ProductSpecType productSpecType : productSpecTypeList){
							allProductSpecTypeMap.remove(productSpecType.getSpecTypeId());
							saveProductSpecTypeMap.put(StringUtil.null2Str(productSpecType.getTmpSpecTypeId()), productSpecType);
						}
					}
				}
				
				// 商品规格信息列表
				Map<Long, ProductSpec> allProductSpecMap = new HashMap<Long, ProductSpec> ();
				List<ProductSpec> dbProductSpecList = this.productSpecManager.getProductSpecListByProductId(product.getProductId());
				if(dbProductSpecList != null && dbProductSpecList.size() > 0){
					for(ProductSpec productSpec : dbProductSpecList){
						allProductSpecMap.put(productSpec.getProductSpecId(), productSpec);
					}
				}
				
				// 规格信息批量保存
				if(productSpecList != null && productSpecList.size() > 0){
					Date currentDate = DateUtil.getCurrentDate();
					List<ProductSpec> saveProductSpecList = new ArrayList<ProductSpec> ();
					for(ProductSpec productSpec : productSpecList){
						productSpec.setProductId(product.getProductId());
						if(productSpec.getProductSpecId() != null && allProductSpecMap.containsKey(productSpec.getProductSpecId())){
							ProductSpec dbProductSpec = allProductSpecMap.get(productSpec.getProductSpecId());
							productSpec.setCreateTime(dbProductSpec.getCreateTime());
							productSpec.setUpdateTime(currentDate);
						}else{
							productSpec.setProductSpecId(null);
							productSpec.setCreateTime(currentDate);
							productSpec.setUpdateTime(currentDate);
						}
						
						// 商品主规格
						if(StringUtil.isNull(productSpec.getTmpPrimarySpecId())){
							continue;
						}else if(!saveProductSpecTypeMap.containsKey(productSpec.getTmpPrimarySpecId())){
							continue;
						}
						
						ProductSpecType primaryProductSpecType = saveProductSpecTypeMap.get(productSpec.getTmpPrimarySpecId());
						productSpec.setPrimarySpecId(primaryProductSpecType.getSpecTypeId());
						productSpec.setPrimarySpecName(primaryProductSpecType.getSpecTypeName());
						productSpec.setPrimarySpecModelName(primaryProductSpecType.getSpecModelName());
						productSpec.setSpecImagePath(StringUtil.null2Str(primaryProductSpecType.getImagePath()));
						productSpec.setProductTags(StringUtil.null2Str(primaryProductSpecType.getSpecTypeName()));
						
						// 商品次规格
						if(isMoreSpecProduct){
							if(StringUtil.isNull(productSpec.getTmpSecondarySpecId())){
								continue;
							}else if(!saveProductSpecTypeMap.containsKey(productSpec.getTmpSecondarySpecId())){
								continue;
							}
							
							ProductSpecType secondaryProductSpecType = saveProductSpecTypeMap.get(productSpec.getTmpSecondarySpecId());
							productSpec.setSecondarySpecId(secondaryProductSpecType.getSpecTypeId());
							productSpec.setSecondarySpecName(secondaryProductSpecType.getSpecTypeName());
							productSpec.setSecondarySpecModelName(secondaryProductSpecType.getSpecModelName());
							productSpec.setProductTags(String.format("%s、%s", primaryProductSpecType.getSpecTypeName(), secondaryProductSpecType.getSpecTypeName()));
						}
						saveProductSpecList.add(productSpec);
					}
					
					// 批量保存和更新
					saveProductSpecList = this.productSpecManager.batchInsert(saveProductSpecList, saveProductSpecList.size());
					if(saveProductSpecList != null && saveProductSpecList.size() > 0){
						for(ProductSpec productSpec : saveProductSpecList){
							allProductSpecMap.remove(productSpec.getProductSpecId());
						}
						product.setProductSpecList(saveProductSpecList);
					}
				}
				
				// 删除商品规格类型信息
				if(allProductSpecTypeMap != null && allProductSpecTypeMap.size() > 0){
					this.productSpecTypeManager.deleteByIdList(StringUtil.longSetToList(allProductSpecTypeMap.keySet()));
				}
				
				// 删除商品规格信息
				if(allProductSpecMap != null && allProductSpecMap.size() > 0){
					this.productSpecManager.deleteByIdList(StringUtil.longSetToList(allProductSpecMap.keySet()));
				}
			}
		}
		
		return product;
	}

	@Override
	public List<Product> getProductListByBrandId(Long brandId, boolean status) {
		return this.productRepository.getProductListByBrandId(brandId, status);
	}

	@Override
	public List<Product> getProductListByBrandId(Long brandId) {
		return this.productRepository.getProductListByBrandId(brandId);
	}
	
	@Override
	public List<Product> getProductListByProudctIdList(List<Long> productIdList){
		if(productIdList != null && productIdList.size() > 0){
			List<Product> list = this.getByIdList(productIdList);
			if(list != null && list.size() > 0){
				Map<Long, Product> productMap = new HashMap<Long, Product> ();
				for(Product product : list){
					if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
						productMap.put(product.getProductId(), product);
					}
				}
				
				// 规格商品补规格信息
				if(productMap != null && productMap.size() > 0){
					List<ProductSpec> productSpecList = this.productSpecManager.getProductSpecListByProductIdList(StringUtil.longSetToList(productMap.keySet()));
					if(productSpecList != null && productSpecList.size() > 0){
						for(ProductSpec productSpec : productSpecList){
							if(productMap.containsKey(productSpec.getProductId())){
								Product product = productMap.get(productSpec.getProductId());
								if(product.getProductSpecList() == null || product.getProductSpecList().size() <= 0){
									List<ProductSpec> xlist = new ArrayList<ProductSpec> ();
									xlist.add(productSpec);
									product.setProductSpecList(xlist);
								}else{
									product.getProductSpecList().add(productSpec);
								}
							}
						}
					}
				}
			}
			return list;
		}
		return null;
	}

	@Override
	public void deleteProduct(List<Long> idList, Boolean isDelete) {
		if (idList != null && idList.size() > 0) {
			this.productRepository.deleteProduct(idList, isDelete, DateUtil.getCurrentDate());
		}
	}

	@Override
	public Product getProductByAssemblyModelId(Long assemblyModelId) {
		List<Product> productList = this.productRepository.getProductByAssemblyModelId(assemblyModelId);
		if (productList != null && productList.size() > 0) {
			return productList.get(0);
		}
		return null;
	}

	@Override
	public void updateProductSeckillTotalNumber(Long productId, Integer seckillTotalNumber) {
		this.productRepository.updateProductSeckillTotalNumber(productId,seckillTotalNumber);
	}

	@Override
	public Product getProductByIsGuideProduct(Boolean isGuideProduct) {
		List<Product> productList = this.productRepository.getProductListByIsGuideProduct(isGuideProduct);
		if(productList != null && productList.size() > 0) {
			return productList.get(0);
		}
		return null;
	}

	@Override
	public List<Product> getProductListByIsOpenV2Price(Boolean isOpenV2Price) {
		return this.productRepository.getProductListByIsOpenV2Price(isOpenV2Price);
	}

	@Override
	public List<Product> getProductListByIsOpenV3Price(Boolean isOpenV3Price) {
		return this.productRepository.getProductListByIsOpenV2Price(isOpenV3Price);

	}

	@Override
	public List<Product> getProductListByIsOpenVPrice() {
		return this.productRepository.getProductListByIsOpenVPrice();
	}

	@Override
	public List<Product> getProductListByTagIdInField(Long tagId) {
		String hql = String.format("from Product where FIND_IN_SET(%s,tag_ids) > 0 and status = true", tagId);
		return this.query(hql);
	}

	@Override
	public Product getProductByProductIdAndIsDelete(Long productId,Boolean isDelete) {
		return this.productRepository.getProductByProductIdAndIsDelete(productId, isDelete);
	}

	@Override
	public List<Object[]> getProductStockNumberByProductIdList(List<Long> productIdList) {
		if(productIdList == null || productIdList.isEmpty()) {
			return null;
		}
		String idList = StringUtil.longListToStr(productIdList);
		StringBuilder strBulSql = new StringBuilder();
		strBulSql.append("select jp.`product_id`,jp.`name`,jp.`stock_number` from jkd_product jp ");
		strBulSql.append("where jp.is_delete <> true and jp.`is_spce_product` = false and jp.`is_group_product` = false and jp.product_id in(%s) ");
		strBulSql.append("union all ");
		strBulSql.append("select jp.`product_id`,jp.`name`,sum(jps.`stock_number`) from jkd_product jp ,jkd_product_spec jps ");
		strBulSql.append("where jp.is_delete <> true and jp.`is_spce_product` = true and jp.`is_group_product` = false and jp.`product_id` = jps.product_id and jp.product_id in(%s) ");
		strBulSql.append("group by jps.product_id ");
		strBulSql.append("union all ");
		strBulSql.append("select ee.product_id,ee.name,ee.sn stock_number from ( ");
		strBulSql.append("select aa.product_group_id,aa.product_id,aa.product_spec_id,aa.`name`,aa.sale_times,dd.stock_number,floor((dd.stock_number / aa.sale_times)) as sn from ");
		strBulSql.append("( select jp.`product_id` product_group_id,jpg.product_id ,jpg.product_spec_id,jp.`name`,jpg.sale_times from jkd_product jp ,jkd_product_group jpg ");
		strBulSql.append("where jp.is_delete <> true  and jp.`is_group_product` = true and  ");
		strBulSql.append("jp.`product_id` = jpg.product_group_id ) aa ");
		strBulSql.append("inner join  ");
		strBulSql.append("( select jp.product_id,null as product_spec_id,jp.`stock_number` from jkd_product jp where jp.`is_spce_product` <> true and jp.`is_delete` = false and jp.`is_group_product` = false ");
		strBulSql.append("union all ");
		strBulSql.append("select jp.`product_id`,jps.`product_spec_id`,jps.`stock_number` from jkd_product jp ,jkd_product_spec jps ");
		strBulSql.append("where jp.is_delete <> true and jp.`is_spce_product` = true and jp.`is_group_product` = false and jp.`product_id` = jps.product_id ");
		strBulSql.append(") dd on (aa.product_id,aa.product_spec_id) = (dd.product_id,dd.product_spec_id) where  aa.product_group_id in(%s) ");
		strBulSql.append(") ee ");
		return this.querySql(String.format(strBulSql.toString(), idList,idList,idList));
	}

	@Override
	public void updateProductShowStatus(List<Long> idList, Boolean isEnabled) {
		if (idList != null && idList.size() > 0) {
			this.productRepository.updateProductShowStatus(idList, isEnabled, DateUtil.getCurrentDate());
		}
	}

	@Override
	public void updateProductRepStatus(List<Long> idList) {
		this.productRepository.updateProductRepStatus(idList);
	}
}
package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.model.PostageTemplate;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductBrand;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.ProductGroup;
import com.chunruo.core.model.ProductImage;
import com.chunruo.core.model.ProductSpec;
import com.chunruo.core.model.ProductSpecModel;
import com.chunruo.core.model.ProductSpecType;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.model.TagModel;
import com.chunruo.core.model.UserSaleStandard;
import com.chunruo.core.service.PostageTemplateManager;
import com.chunruo.core.service.ProductImageManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.ProductSpecManager;
import com.chunruo.core.service.ProductSpecModelManager;
import com.chunruo.core.service.ProductSpecTypeManager;
import com.chunruo.core.service.ProductWarehouseManager;
import com.chunruo.core.service.UserSaleStandardManager;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.webapp.BaseController;
import com.chunruo.webapp.vo.ImageVo;

@Controller
@RequestMapping("/product/")
public class ProductController extends BaseController {
	@Autowired
	private ProductManager productManager;
	@Autowired
	private ProductWarehouseManager productWarehouseManager;
	@Autowired
	private ProductImageManager productImageManager;
	@Autowired
	private PostageTemplateManager postageTemplateManager;
	@Autowired
	private ProductSpecManager productSpecManager;
	@Autowired
	private ProductSpecTypeManager productSpecTypeManager;
	@Autowired
	private ProductSpecModelManager productSpecModelManager;
	
	/**
	 * ??????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/wholesaleList")
	public @ResponseBody Map<String, Object> wholesaleList(final HttpServletRequest request) {
		Boolean isNotGroup = StringUtil.nullToBoolean(request.getParameter("isNotGroup"));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> paramOrMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<Product> productList = new ArrayList<>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), Product.class);
			
			// filter??????????????????
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					String objectKey = StringUtil.null2Str(entry.getKey());
					if(StringUtil.compareObject("productCode", objectKey) && entry.getValue() != null){
						Map<String, Object> childParamMap = new HashMap<String, Object> ();
						childParamMap.put(objectKey, entry.getValue());
						List<ProductSpec> list = this.productSpecManager.getHqlPages(childParamMap);
						if(list != null && list.size() > 0){
							Set<Long> productIdSet = new HashSet<Long> ();
							for(ProductSpec productSpec : list){
								productIdSet.add(productSpec.getProductId());
							}
							paramOrMap.put("productId", StringUtil.longSetToList(productIdSet));
						}
					}
					
					boolean isMatch = false;
					if(StringUtil.compareObject("categoryIdName", objectKey) && entry.getValue() != null) {
						for(Map.Entry<Long, ProductCategory> categoryEntry : Constants.PRODUCT_CATEGORY_MAP.entrySet()) {
							String categoryName = categoryEntry.getValue().getName();
							if(categoryName.contains(StringUtil.null2Str(entry.getValue()).replace("%", ""))) {
								paramMap.put("categoryId", categoryEntry.getKey());
								isMatch = true;
							}
						}
					}
					if(StringUtil.compareObject("brandId", objectKey) && entry.getValue() != null) {
						for(Map.Entry<Long, ProductBrand> brandEntry : Constants.PRODUCT_BRAND_MAP.entrySet()) {
							String categoryName = brandEntry.getValue().getName();
							if(categoryName.contains(StringUtil.null2Str(entry.getValue()))) {
								paramMap.put("brandId", brandEntry.getKey());
								isMatch = true;
							}
						}
					}
					if(!isMatch) {
						paramMap.put(objectKey, entry.getValue());
					}
				}
			}
			
			if(sortMap != null ) {
				sort = "status desc,o.createTime desc ";
				if(!sortMap.isEmpty()) {
					sort += " ,o."+sortMap.get("sort");
				}
				sortMap.put("sort", sort);
				sortMap.put("dir", sortMap.get("dir"));
			}

			// ????????????????????????
			paramMap.put("isDelete", false);
			if(StringUtil.nullToBoolean(isNotGroup)){
				// ???????????????
				paramMap.put("isGroupProduct", false);
				paramMap.put("isSoldout", false);
				paramMap.put("status", true);
			}
			
			count = this.productManager.countHql(paramMap, paramOrMap);
			if (count != null && count.longValue() > 0L) {
				productList = this.productManager.getHqlPages(paramMap, paramOrMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if(productList != null && productList.size() > 0){
					for(Product product : productList){
						String imagePath = StringUtil.null2Str(product.getImage());
						if(imagePath.startsWith("/")){
							imagePath = imagePath.substring(1);
						}
						
						product.setImage(imagePath);
					}
					
					// ???????????????\????????????\????????????\??????
					for(Product product : productList){
						
						List<Long> categoryFidList = StringUtil.stringToLongArray(product.getCategoryFids());
						if(categoryFidList != null && !categoryFidList.isEmpty()) {
							StringBuilder categoryFidName = new StringBuilder();
							for(Long categoryFid : categoryFidList) {
								ProductCategory productCategory = Constants.PRODUCT_CATEGORY_MAP.get(categoryFid);
								if(productCategory != null && productCategory.getCategoryId() != null) {
									categoryFidName.append(StringUtil.null2Str(productCategory.getName()));
									categoryFidName.append(",");
								}
							}
							product.setCategoryFidName(categoryFidName.toString());
						}
						
						List<Long> categoryIdList = StringUtil.stringToLongArray(product.getCategoryIds());
						if(categoryIdList != null && !categoryIdList.isEmpty()) {
							StringBuilder categoryIdName = new StringBuilder();
							for(Long categoryId : categoryIdList) {
								ProductCategory productCategory = Constants.PRODUCT_CATEGORY_MAP.get(categoryId);
								if(productCategory != null && productCategory.getCategoryId() != null) {
									categoryIdName.append(StringUtil.null2Str(productCategory.getName()));
									categoryIdName.append(",");
								}
							}
							product.setCategoryIdName(categoryIdName.toString());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", productList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	@RequestMapping(value = "/getProductById")
	public @ResponseBody Map<String, Object> getProductById(final HttpServletRequest request) {
		long start = System.currentTimeMillis();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String productId = StringUtil.null2Str(request.getParameter("productId"));
		
		boolean isAggrProduct = false;
		boolean isPrimarySpec = false;
		boolean isSecondarySpec = false;
		String primarySpecModelName = "";
		String secondarySpecModelName = "";
		Long primarySpecModelId = null;
		Long secondarySpecModelId = null;
		Product product = null;
		List<ImageVo> imageList = new ArrayList<ImageVo> ();
		List<ImageVo> materialImageList = new ArrayList<ImageVo> ();
		List<ProductSpec> productSpecList = new ArrayList<ProductSpec> ();
		List<ProductSpecType> primarySpecTypeList = new ArrayList<ProductSpecType> ();
		List<ProductSpecType> secondarySpecTypeList = new ArrayList<ProductSpecType> ();
		List<TagModel> tagModelList = new ArrayList<TagModel>();
		List<Product> aggrProductList = new ArrayList<Product> ();

		try{
			if (StringUtil.isNumber(productId) && (product = this.productManager.get(StringUtil.nullToLong(productId))) != null) {
				StringBuffer categoryPathNameBuffer = new StringBuffer ();
			    List<Long> categoryIdList = StringUtil.stringToLongArray(product.getCategoryIds());
				if(categoryIdList != null && !categoryIdList.isEmpty()) {
					for(Long categoryId : categoryIdList) {
						ProductCategory productCategory = Constants.PRODUCT_CATEGORY_MAP.get(categoryId);
						if(productCategory != null && productCategory.getCategoryId() != null) {
							ProductCategory parentCategory = Constants.PRODUCT_CATEGORY_MAP.get(productCategory.getParentId());
						    if(parentCategory != null && parentCategory.getCategoryId() != null) {
						    	categoryPathNameBuffer.append(parentCategory.getName());
						    	categoryPathNameBuffer.append("->");
						    	categoryPathNameBuffer.append(productCategory.getName());
						    	categoryPathNameBuffer.append(",");
						    }
						}
					}
				}
				product.setCategoryPathName(categoryPathNameBuffer.toString());
				
				//????????????
				if(Constants.PRODUCT_BRAND_MAP !=null && Constants.PRODUCT_BRAND_MAP.containsKey(product.getBrandId())){
					String brandName = StringUtil.null2Str(Constants.PRODUCT_BRAND_MAP.get(product.getBrandId()).getName());
					product.setBrandName(brandName);
				}
				
				
				// ??????????????????
				if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
					// ????????????????????????
					List<ProductSpecType> productSpecTypeList = this.productSpecTypeManager.getProductSpecTypeListByProductId(product.getProductId());
					if(productSpecTypeList != null && productSpecTypeList.size() > 0){
						Map<Long, ProductSpecType> productSpecTypeMap = new HashMap<Long, ProductSpecType> ();
						for(ProductSpecType productSpecType : productSpecTypeList){
							productSpecTypeMap.put(productSpecType.getSpecTypeId(), productSpecType);
						}
						
						// ????????????????????????ID
						Set<Long> primarySpecTypeIdSet = new HashSet<Long> ();
						Set<Long> secondarySpecTypeIdSet = new HashSet<Long> ();
						
						// ????????????
						List<ProductSpec> specList = this.productSpecManager.getProductSpecListByProductId(product.getProductId());
						if(specList != null && specList.size() > 0){
							ProductSpec spec = new ProductSpec();
							spec.setProductId(StringUtil.nullToLong(productId));
							spec.setProductSpecId(-1L);
							spec.setPrimarySpecName("????????????");
							spec.setSecondarySpecName("????????????");
							productSpecList.add(spec);
							for(ProductSpec productSpec : specList){
								
								// ?????????????????????ID
								if(productSpec.getPrimarySpecId() != null){
									if(productSpecTypeMap.containsKey(productSpec.getPrimarySpecId())){
										ProductSpecType productSpecType = productSpecTypeMap.get(productSpec.getPrimarySpecId());
										productSpec.setPrimarySpecName(productSpecType.getSpecTypeName());
										primarySpecTypeIdSet.add(productSpec.getPrimarySpecId());
									}else{
										continue;
									}
								}
								
								// ?????????????????????ID
								if(productSpec.getSecondarySpecId() != null){
									if(productSpecTypeMap.containsKey(productSpec.getSecondarySpecId())){
										ProductSpecType productSpecType = productSpecTypeMap.get(productSpec.getSecondarySpecId());
										productSpec.setSecondarySpecName(productSpecType.getSpecTypeName());
										secondarySpecTypeIdSet.add(productSpec.getSecondarySpecId());
									}else{
										continue;
									}
								}
								
								productSpec.setTmpPrimarySpecId(StringUtil.null2Str(productSpec.getProductSpecId()));
								productSpec.setTmpSecondarySpecId(StringUtil.null2Str(productSpec.getTmpSecondarySpecId()));
								productSpecList.add(productSpec);
							}
						}
						
						// ?????????????????????????????????
						if(primarySpecTypeIdSet != null && primarySpecTypeIdSet.size() > 0){
							// ??????????????????????????????
							Map<Long, ProductSpecModel> productSpecModelMap = new HashMap<Long, ProductSpecModel> ();
							List<ProductSpecModel> productSpecModelList = this.productSpecModelManager.getAll();
							if(productSpecModelList != null && productSpecModelList.size() > 0){
								for(ProductSpecModel productSpecModel : productSpecModelList){
									productSpecModelMap.put(productSpecModel.getSpecModelId(), productSpecModel);
								}
							}
							
							isPrimarySpec = true;
							for(ProductSpecType productSpecType : productSpecTypeList){
								if(primarySpecTypeIdSet.contains(productSpecType.getSpecTypeId())){
									primarySpecModelId = productSpecType.getSpecModelId();
									primarySpecTypeList.add(productSpecType);
								}
							}
							
							// ???????????????????????????
							if(primarySpecModelId != null 
									&& productSpecModelMap != null
									&& productSpecModelMap.containsKey(primarySpecModelId)){
								primarySpecModelName = productSpecModelMap.get(primarySpecModelId).getName();
							}
							
							// ?????????????????????????????????
							if(primarySpecTypeList != null
									&& primarySpecTypeList.size() > 0
									&& secondarySpecTypeIdSet != null
									&& secondarySpecTypeIdSet.size() >0){
								isSecondarySpec = true;
								for(ProductSpecType productSpecType : productSpecTypeList){
									if(secondarySpecTypeIdSet.contains(productSpecType.getSpecTypeId())){
										secondarySpecModelId = productSpecType.getSpecModelId();
										secondarySpecTypeList.add(productSpecType);
									}
								}
								
								// ???????????????????????????
								if(primarySpecModelId != null 
										&& productSpecModelMap != null
										&& productSpecModelMap.containsKey(secondarySpecModelId)){
									secondarySpecModelName = productSpecModelMap.get(secondarySpecModelId).getName();
								}
							}
						}
					}
				}
				
				//??????????????????
				List<ProductImage> productImageList = this.productImageManager.getProductImageListByProductId(product.getProductId(), ProductImage.IMAGE_TYPE_HEADER);
				if(productImageList != null && productImageList.size() > 0){
					for(ProductImage productImage : productImageList){
						ImageVo image = new ImageVo ();
						image.setFileId(StringUtil.null2Str(productImage.getImageId()));
						if(!StringUtil.isNull(productImage.getImagePath())){
							String imagePath = StringUtil.null2Str(productImage.getImagePath());
							if(imagePath.startsWith("/")){
								imagePath = imagePath.substring(1);
							}
							
							image.setFileName(imagePath.substring(imagePath.lastIndexOf("/")));
							image.setFileType(FileUtil.getSuffixByFilename(imagePath));
							image.setFilePath(imagePath);
						}
						imageList.add(image);
					}
				}
				
				//??????????????????
				List<ProductImage> productMaterialImageList = this.productImageManager.getProductImageListByProductId(product.getProductId(), ProductImage.IMAGE_TYPE_MATERIAL);
				if(productMaterialImageList != null && productMaterialImageList.size() > 0){
					for(ProductImage productImage : productMaterialImageList){
						ImageVo image = new ImageVo ();
						image.setFileId(StringUtil.null2Str(productImage.getImageId()));
						if(!StringUtil.isNull(productImage.getImagePath())){
							String imagePath = StringUtil.null2Str(productImage.getImagePath());
							if(imagePath.startsWith("/")){
								imagePath = imagePath.substring(1);
							}
							
							image.setFileName(imagePath.substring(imagePath.lastIndexOf("/")));
							image.setFileType(FileUtil.getSuffixByFilename(imagePath));
							image.setFilePath(imagePath);
						}
						materialImageList.add(image);
					}
				}
			    
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.debug(String.format("??????????????????????????????===>>>%s???", StringUtil.nullToDoubleFormat((System.currentTimeMillis() - start)/1000d)));
		
		resultMap.put("success", true);
		resultMap.put("data", product);
		resultMap.put("isAggrProduct", isAggrProduct);
		resultMap.put("aggrProductList", aggrProductList);
		resultMap.put("productType", product.getProductType());
		resultMap.put("tagModelList", tagModelList);
		resultMap.put("isPrimarySpec", isPrimarySpec);
		resultMap.put("isSecondarySpec", isSecondarySpec);
		resultMap.put("primarySpecModelId", primarySpecModelId);
		resultMap.put("secondarySpecModelId", secondarySpecModelId);
		resultMap.put("primarySpecModelName", StringUtil.null2Str(primarySpecModelName));
		resultMap.put("secondarySpecModelName", StringUtil.null2Str(secondarySpecModelName));
		resultMap.put("productSpecList", productSpecList);
		resultMap.put("primarySpecTypeList", primarySpecTypeList);
		resultMap.put("secondarySpecTypeList", secondarySpecTypeList);
		resultMap.put("imageList", imageList);
		resultMap.put("materialImageList", materialImageList);
		resultMap.put("isGroupProduct", StringUtil.nullToBoolean(product.getIsGroupProduct()));
		return resultMap;
	}
	
	/**
	 * ??????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/searchProductById")
	public @ResponseBody Map<String, Object> searchProductById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long productId = StringUtil.nullToLong(request.getParameter("productId"));
		try {
			Product product = this.productManager.get(productId);
			if(product != null && product.getProductId() != null) {
				if(Constants.PRODUCT_WAREHOUSE_MAP.containsKey(product.getWareHouseId())){
					product.setWareHouseName(Constants.PRODUCT_WAREHOUSE_MAP.get(product.getWareHouseId()).getName());
				}
				resultMap.put("data", product);
			}
		}catch(Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		resultMap.put("success", true);
		return resultMap;
	}
	
	/**
	 * ??????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/saveProduct")
	public @ResponseBody Map<String, Object> saveProduct(@ModelAttribute("product")Product product, final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String recodeGridJson = StringUtil.null2Str(request.getParameter("recodeGridJson"));
		String productSpecGridJson = StringUtil.null2Str(request.getParameter("productSpecGridJson"));
		String primarySpecGridJson = StringUtil.null2Str(request.getParameter("primarySpecGridJson"));
		String secondarySpecGridJson = StringUtil.null2Str(request.getParameter("secondarySpecGridJson"));
		Boolean isGroupProduct = StringUtil.nullToBoolean(request.getParameter("isGroupProduct"));

		try{
			// ??????????????????????????????
			boolean isNews = (product.getProductId() == null);
			if(isNews){
				product.setIsDelete(false);
				product.setStatus(true);
				product.setIsSoldout(false);
				product.setIsPackage(false);
				product.setIsShow(true);
				product.setIsGroupProduct(isGroupProduct);
				product.setCreateTime(DateUtil.getCurrentDate());
			}else{
				Product dbProduct = this.productManager.get(product.getProductId());
				if(dbProduct == null || dbProduct.getProductId() == null){
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("errors.nuKnow"));
					return resultMap;
				}else if(dbProduct.getCreateTime() == null){
					dbProduct.setCreateTime(DateUtil.getCurrentDate());
				}
				
				//????????????\????????????
				product.setIsDelete(false);
				product.setIsGuideProduct(dbProduct.getIsGuideProduct());
				product.setIsGroupProduct(dbProduct.getIsGroupProduct());
				product.setCreateTime(dbProduct.getCreateTime());
				product.setStatus(dbProduct.getStatus());
				product.setIsSoldout(dbProduct.getIsSoldout());
				product.setIsShow(dbProduct.getIsShow());
				product.setIsTest(dbProduct.getIsTest());
				product.setIsPackage(dbProduct.getIsPackage());
				product.setProductDesc(StringUtil.null2Str(dbProduct.getProductDesc()));
				product.setProductCopywriter(StringUtil.null2Str(dbProduct.getProductCopywriter()));
				product.setAggrProductIds(dbProduct.getAggrProductIds());
			}
			// ????????????
			boolean isMoreSpecProduct = false;
			List<Product> aggrProductList = null;
			List<ProductSpec> productSpecList = new ArrayList<ProductSpec> ();
			List<ProductSpecType> primarySpecList = new ArrayList<ProductSpecType> ();
			List<ProductSpecType> secondarySpecList = new ArrayList<ProductSpecType> ();
			List<ProductGroup> productGroupList = new ArrayList<ProductGroup> ();
			
			// ??????????????????
			List<Double> priceList = new ArrayList<Double> ();
			if(StringUtil.nullToBoolean(product.getIsSpceProduct())){
				// ????????????????????????
				MsgModel<List<ProductSpec>> productSpecModel = this.getProductSpecList(productSpecGridJson, isNews, product);
				if(!StringUtil.nullToBoolean(productSpecModel.getIsSucc())){
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", productSpecModel.getMessage());
					return resultMap;
				}
				
				// ????????????
				productSpecList = productSpecModel.getData();
				// ?????????????????????
				isMoreSpecProduct = StringUtil.nullToBoolean(productSpecModel.getIsMoreSpecProduct());
				
				// ????????????????????????????????????
				if(productSpecModel.getData() != null && productSpecModel.getData().size() > 0){
					for(ProductSpec productSpec : productSpecModel.getData()){
						priceList.add(productSpec.getPriceRecommend());
					}
					
					// ???????????????????????????
					MsgModel<List<ProductSpecType>> primarySpecModel = this.getProductSpecTypeList(primarySpecGridJson, isNews, product.getProductId());
					if(!StringUtil.nullToBoolean(primarySpecModel.getIsSucc())){
						resultMap.put("error", true);
						resultMap.put("success", true);
						resultMap.put("message", primarySpecModel.getMessage());
						return resultMap;
					}else if(primarySpecModel.getData() == null || primarySpecModel.getData().size() <= 0){
						resultMap.put("error", true);
						resultMap.put("success", true);
						resultMap.put("message", "?????????????????????????????????");
						return resultMap;
					}
					
					primarySpecList = primarySpecModel.getData();
					
					// ??????????????????
					if(isMoreSpecProduct){
						// ???????????????????????????
						MsgModel<List<ProductSpecType>> secondarySpecModel = this.getProductSpecTypeList(secondarySpecGridJson, isNews, product.getProductId());
						if(!StringUtil.nullToBoolean(secondarySpecModel.getIsSucc())){
							resultMap.put("error", true);
							resultMap.put("success", true);
							resultMap.put("message", secondarySpecModel.getMessage());
							return resultMap;
						}else if(StringUtil.nullToBoolean(productSpecModel.getIsMoreSpecProduct())
								&& (secondarySpecModel.getData() == null || secondarySpecModel.getData().size() <= 0)){
							resultMap.put("error", true);
							resultMap.put("success", true);
							resultMap.put("message", "?????????????????????????????????");
							return resultMap;
						}
						
						secondarySpecList = secondarySpecModel.getData();
					}
				}
			}else{
				// ??????????????????????????????
				if(product.getPriceCost() == null){
					// ????????????????????????
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("product.wholesale.priceCost.empty"));
					return resultMap;
				}else if(StringUtil.nullToDouble(product.getPriceRecommend()).compareTo(StringUtil.nullToDouble(product.getPriceCost())) <= 0){
					// ????????????????????????????????????
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("?????????????????????????????????"));
					return resultMap;
				}else if(product.getTemplateId() == null) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("????????????????????????"));
					return resultMap;
				}
				
				// ????????????
				priceList.add(product.getPriceRecommend());
				
				//????????????
				Integer quantity = StringUtil.nullToInteger(product.getStockNumber());
				product.setIsSoldout(true);
				if(quantity > 0){
					product.setIsSoldout(false);
				}	
			}
			
			Collections.sort(priceList, new Comparator<Double>() {
				public int compare(Double obj1, Double obj2) {
					return StringUtil.nullToDouble(obj2).compareTo(StringUtil.nullToDouble(obj1));
				}
			});
			
			
			
			// ????????????
			List<Long> categoryIdList = StringUtil.stringToLongArray(product.getCategoryIds());
			if(categoryIdList == null || categoryIdList.isEmpty()) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message","??????????????????");
				return resultMap;
			}
			List<Long> categoryFidList = new ArrayList<Long>();
			for(Long categoryId : categoryIdList) {
				ProductCategory productCategory = Constants.PRODUCT_CATEGORY_MAP.get(categoryId);
				if(productCategory == null || productCategory.getCategoryId() == null) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message","???????????????????????????");
					return resultMap;
				}
				categoryFidList.add(StringUtil.nullToLong(productCategory.getParentId()));
			}
			product.setCategoryFids(StringUtil.longArrayToString(categoryFidList));
			
			
			// ??????
			Set<String> filePathMap = new HashSet<String> ();
			List<Object> saveMap = StringUtil.jsonDeserialize(recodeGridJson);
			List<ProductImage> imageList = new ArrayList<ProductImage> ();
			if (saveMap != null && saveMap.size() > 0) {
				for (int i = 0; i < saveMap.size(); i++) {
					@SuppressWarnings("unchecked")
					ProductImage productImage = createImage((Map<String, Object>)saveMap.get(i), ProductImage.IMAGE_TYPE_HEADER);
					if (StringUtil.isNull(productImage.getImageType())) {
						resultMap.put("success", Boolean.valueOf(false));
						resultMap.put("message", getText("image.saveAndDel.dataEexption"));
						return resultMap;
					}else if (productImage.getImageId() == null && !StringUtil.isNullStr(productImage.getImagePath())){
						if (filePathMap.contains(productImage.getImagePath())){
							continue;
						}
						filePathMap.add(productImage.getImagePath());
					}
					imageList.add(productImage);
				}
			}else {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("????????????????????????"));
				return resultMap;
			}
			
			product.setAdminUserName(this.getUserName(request));
			product.setIsMoreSpecProduct(isMoreSpecProduct);
			product.setUpdateTime(DateUtil.getCurrentDate());
			product = this.productManager.saveProduct(product, productSpecList, primarySpecList, secondarySpecList, productGroupList, imageList, aggrProductList);
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", getText("errors.nuKnow"));
		return resultMap;
	}
	
	/**
	 * ??????????????????
	 * @param product
	 * @param aggrProductIds
	 * @param oldAggrProductIds
	 * @return
	 */
	public MsgModel<List<Product>> getProductAggregatedList(Product product, List<Long> aggrProductIdList, String oldAggrProductIds){
		MsgModel<List<Product>> msgModel = new MsgModel<List<Product>> ();
		List<Product> aggrProductList = new ArrayList<Product> ();
		try {
			Set<Long> deleteAggrProductIdSet = StringUtil.stringToIntegerSet(oldAggrProductIds);
			// ??????????????????
			if(aggrProductIdList != null && aggrProductIdList.size() > 0) {
				ProductWarehouse productWarehouse = this.productWarehouseManager.get(StringUtil.nullToLong(product.getWareHouseId()));
				if(productWarehouse == null 
						|| productWarehouse.getWarehouseId() == null
						|| StringUtil.compareObject(GoodsType.GOODS_TYPE_COMMON, productWarehouse.getProductType())) {
					msgModel.setIsSucc(false);
					msgModel.setMessage("???????????????????????????????????????");
					return msgModel;
				}
			
				aggrProductList = this.productManager.getByIdList(aggrProductIdList);
				if(aggrProductList != null && !aggrProductList.isEmpty()) {
					Set<Long> productIdSet = new HashSet<Long> ();
					List<Long> productWareHouseIdList = new ArrayList<Long>();
					for(Product aggrProduct : aggrProductList) {
						productIdSet.add(aggrProduct.getProductId());
						productWareHouseIdList.add(StringUtil.nullToLong(aggrProduct.getWareHouseId()));
						if(!StringUtil.nullToBoolean(aggrProduct.getStatus())) {
							msgModel.setIsSucc(false);
							msgModel.setMessage("?????????->" + StringUtil.null2Str(aggrProduct.getName()));
							return msgModel;
						}else if(StringUtil.nullToBoolean(aggrProduct.getIsGroupProduct())) {
							msgModel.setIsSucc(false);
							msgModel.setMessage("??????????????????????????????->" + StringUtil.null2Str(aggrProduct.getName()));
							return msgModel;
						}else if(StringUtil.compareObject(product.getProductId(), aggrProduct.getProductId())) {
							msgModel.setIsSucc(false);
							msgModel.setMessage("??????????????????????????????????????????");
							return msgModel;
						}
					}
					
					// ??????????????????
					List<ProductWarehouse> productWarehouseList = this.productWarehouseManager.getByIdList(productWareHouseIdList);
					if(productWarehouseList != null && !productWarehouseList.isEmpty()) {
						for(ProductWarehouse warehouse : productWarehouseList) {
							if(StringUtil.compareObject(warehouse.getProductType(), GoodsType.GOODS_TYPE_COMMON)) {
								msgModel.setIsSucc(false);
								msgModel.setMessage("?????????????????????????????????????????????????????????");
								return msgModel;
							}
						}
					}
					
					String productIds = StringUtil.longSetToStr(productIdSet);
					product.setAggrProductIds(productIds);
					
					// ????????????????????????????????????
					String tmpProductId = StringUtil.null2Str(product.getProductId());
					for(Product aggrProduct : aggrProductList) {
						// ??????????????????ID
						deleteAggrProductIdSet.remove(aggrProduct.getProductId());
						
						String aggrProudctId = productIds.replaceAll(StringUtil.null2Str(aggrProduct.getProductId()), tmpProductId);
						aggrProduct.setAggrProductIds(aggrProudctId);
						aggrProduct.setUpdateTime(DateUtil.getCurrentDate());
						
					}
				}else {
					// ???????????????????????????????????????
					product.setAggrProductIds("");
				}
				
				// ????????????????????????????????????
				if(deleteAggrProductIdSet != null && deleteAggrProductIdSet.size() > 0) {
					List<Product> clearProductList = this.productManager.getByIdList(StringUtil.longSetToList(deleteAggrProductIdSet));
					if(clearProductList != null && clearProductList.size() > 0) {
						if(aggrProductList == null || aggrProductList.size() <= 0) {
							aggrProductList = new ArrayList<Product> ();
						}
						
						// ??????????????????????????????
						for(Product aggrProduct : clearProductList) {
							aggrProduct.setAggrProductIds("");
							aggrProduct.setUpdateTime(DateUtil.getCurrentDate());
							aggrProductList.add(aggrProduct);
						}
					}
				}
			}else {
				// ???????????????????????????????????????
				product.setAggrProductIds("");
				if(deleteAggrProductIdSet != null && deleteAggrProductIdSet.size() > 0) {
					aggrProductList = this.productManager.getByIdList(aggrProductIdList);
					if(aggrProductList != null && aggrProductList.size() > 0) {
						for(Product aggrProduct : aggrProductList) {
							aggrProduct.setAggrProductIds("");
							aggrProduct.setUpdateTime(DateUtil.getCurrentDate());
						}
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(true);
		msgModel.setData(aggrProductList);
		return msgModel;
	}
	
	/**
	 * ????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/saveProductDesc")
	public @ResponseBody Map<String, Object> saveProductDesc(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long productId = StringUtil.nullToLong(request.getParameter("productId"));
		String productDesc = StringUtil.null2Str(request.getParameter("productDesc"));
		try{
			Product product = this.productManager.get(productId);
			if(product == null || product.getProductId() == null){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("errors.nuKnow"));
				return resultMap;
			}
			
			product.setIsDelete(false);
			product.setProductDesc(productDesc);
			product.setUpdateTime(DateUtil.getCurrentDate());
			this.productManager.save(product);
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", getText("errors.nuKnow"));
		return resultMap;
	}
	
	/**
	 * ??????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/saveProductMaterialImage")
	public @ResponseBody Map<String, Object> saveProductMaterialImage(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long productId = StringUtil.nullToLong(request.getParameter("productId"));
		String recodeGridJson = StringUtil.null2Str(request.getParameter("recodeGridJson"));
		String productCopywriter = StringUtil.null2Str(request.getParameter("productCopywriter"));
		try{
			if(productId == null){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("errors.nuKnow"));
				return resultMap;
			}
			
			Product dbProduct = this.productManager.get(productId);
			if(dbProduct == null || dbProduct.getProductId() == null){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("errors.nuKnow"));
				return resultMap;
			}
			
			if(StringUtil.isNull(productCopywriter)){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "??????????????????");
				return resultMap;
			}
			
			String productCopywriterBak = dbProduct.getProductCopywriter();
			if(!StringUtil.isNull(productCopywriter) && !StringUtil.compareObject(productCopywriterBak, productCopywriter)){
				dbProduct.setProductCopywriter(productCopywriter);
				dbProduct.setUpdateTime(new Date());
				this.productManager.save(dbProduct);
			}
			
			// ??????
			Set<String> filePathMap = new HashSet<String> ();
			List<ProductImage> saveImages = new ArrayList<ProductImage>();
			List<Object> saveMap = StringUtil.jsonDeserialize(recodeGridJson);
			if (saveMap != null && saveMap.size() > 0) {
				for (int i = 0; i < saveMap.size(); i++) {
					@SuppressWarnings("unchecked")
					ProductImage productImage = createImage((Map<String, Object>)saveMap.get(i), ProductImage.IMAGE_TYPE_MATERIAL);
					if (StringUtil.isNull(productImage.getImageType())) {
						resultMap.put("success", Boolean.valueOf(false));
						resultMap.put("message", getText("image.saveAndDel.dataEexption"));
						return resultMap;
					}else if (productImage.getImageId() == null && !StringUtil.isNullStr(productImage.getImagePath())){
						if (filePathMap.contains(productImage.getImagePath())){
							continue;
						}
						filePathMap.add(productImage.getImagePath());
					}
					saveImages.add(productImage);
				}
			}
			
			this.productImageManager.saveAndDelProductImage(productId, ProductImage.IMAGE_TYPE_MATERIAL, saveImages);
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", getText("errors.nuKnow"));
		return resultMap;
	}
	
	/**
	 * ??????????????????
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateFxStatus", method=RequestMethod.POST)
   	public @ResponseBody Map<String, Object> updateProductFxStatus(@RequestParam(value = "idListGridJson") String record, final HttpServletRequest request) {
    	Map<String, Object> resultMap = new HashMap<String, Object> ();
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if(idList == null || idList.size() == 0){
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("errors.nuKnow"));
			return resultMap;
		}
		
		try {
			Boolean isEnabled = StringUtil.nullToBoolean(request.getParameter("isEnabled"));
			this.productManager.updateProductStatus(idList, isEnabled);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("success", false);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}
	
	/**
	 * ??????????????????
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateShowStatus", method=RequestMethod.POST)
   	public @ResponseBody Map<String, Object> updateProductShowStatus(@RequestParam(value = "idListGridJson") String record, final HttpServletRequest request) {
    	Map<String, Object> resultMap = new HashMap<String, Object> ();
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if(idList == null || idList.size() == 0){
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("errors.nuKnow"));
			return resultMap;
		}
		
		try {
			Boolean isEnabled = StringUtil.nullToBoolean(request.getParameter("isEnabled"));
			this.productManager.updateProductShowStatus(idList, isEnabled);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("success", false);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}
	
	/**
	 * ??????????????????
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateSoldoutStatus", method=RequestMethod.POST)
   	public @ResponseBody Map<String, Object> updateProductSoldoutStatus(@RequestParam(value = "idListGridJson") String record, final HttpServletRequest request) {
    	Map<String, Object> resultMap = new HashMap<String, Object> ();
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if(idList == null || idList.size() == 0){
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("errors.nuKnow"));
			return resultMap;
		}
		
		try {
			Boolean isEnabled = StringUtil.nullToBoolean(request.getParameter("isEnabled"));
			this.productManager.updateProductSoldoutStatus(idList, isEnabled);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("success", false);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}
	
	
	/**
	 * ??????????????????
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/deleteProduct", method=RequestMethod.POST)
   	public @ResponseBody Map<String, Object> deleteProduct(@RequestParam(value = "idListGridJson") String record, final HttpServletRequest request) {
    	Map<String, Object> resultMap = new HashMap<String, Object> ();
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if(idList == null || idList.size() == 0){
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
            List<Product> productList = this.productManager.getProductListByProudctIdList(idList);
            if(productList != null && productList.size() > 0) {
            	for(Product product : productList ) {
            		if(StringUtil.nullToBoolean(product.getStatus())) {
            			resultMap.put("success", false);
            			resultMap.put("message", getText(String.format("\"%s\"?????????????????????,????????????", product.getName())));
            			return resultMap;
            		}
            	}
            }
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("errors.nuKnow"));
			return resultMap;
		}
		
		try {
			Boolean isDelete = StringUtil.nullToBoolean(request.getParameter("isDelete"));
			this.productManager.deleteProduct(idList, isDelete);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("success", false);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}


	
	/**
	 * ??????????????????
	 * @param record
	 * @return
	 */
	private ProductImage createImage(Map<String, Object> record, Integer imageType){
		ProductImage productImage = new ProductImage();
		if ((record.get("fileId") != null) && (StringUtil.isNumber(StringUtil.null2Str(record.get("fileId"))))) {
			productImage.setImageId(StringUtil.nullToLong(record.get("fileId")));
		}
		
		// ????????????
		String fileType = FileUtil.getSuffixByFilename(StringUtil.nullToString(record.get("filePath")));
		if(StringUtil.isNull(fileType)){
			fileType = StringUtil.nullToString(record.get("fileType"));
		}
		
		productImage.setImageType(imageType);
		productImage.setImageName(StringUtil.nullToString(record.get("fileName")));
		productImage.setImagePath(StringUtil.nullToString(record.get("filePath")));
		return productImage;
	}
	
	@InitBinder("product")  
    public void initBinderUser(WebDataBinder binder) {  
        binder.setFieldDefaultPrefix("product.");  
    }
	
	/**
	 * ??????????????????????????????ext ComboBox??????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getPostageTemplate")
	public @ResponseBody Map<String, Object> getPostageTemplate(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long wareHouseId = StringUtil.nullToLong(request.getParameter("wareHouseId"));
		
		try {
			ProductWarehouse warehouse = this.productWarehouseManager.get(wareHouseId);
			if(warehouse != null && warehouse.getWarehouseId() != null){
				List<PostageTemplate> postageTemplateList = this.postageTemplateManager.getTemplateListByWarehouseId(warehouse.getWarehouseId(), false);
				if(postageTemplateList != null && postageTemplateList.size() > 0){
					resultMap.put("postageTemplateList", postageTemplateList);
					resultMap.put("productType", StringUtil.nullToInteger(warehouse.getProductType()));
					resultMap.put("success", true);
					return resultMap;
				}
			}
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		
		resultMap.put("success", false);
		resultMap.put("message", "??????,??????????????????????????????");
		return resultMap;
	}
	
	
	
	/**
	 * ??????????????????
	 * @param imageGridJson
	 * @param imageType
	 * @return
	 */
	private MsgModel<List<ProductSpec>> getProductSpecList(String productSpecGridJson, boolean isNews, Product product){
		MsgModel<List<ProductSpec>> msgModel = new MsgModel<List<ProductSpec>> ();
		try{
			if(product == null) {
				msgModel.setIsSucc(false);
				msgModel.setMessage("??????????????????");
				return msgModel;
			}
			Long productId = StringUtil.nullToLong(product.getProductId());
			List<Object> objectMapList = new ArrayList<Object> ();
			try{
				objectMapList = StringUtil.jsonDeserialize(productSpecGridJson);
			}catch(Exception e){
				log.debug(e.getMessage());
			}
			
			if(objectMapList != null && objectMapList.size() > 0){
				// ??????????????????????????????????????????
				boolean isMoreSpecProduct = false;
				Map<Long, ProductSpec> productSpecMap = new HashMap<Long, ProductSpec> ();
				Map<Long, ProductSpecType> productSpecTypeMap = new HashMap<Long, ProductSpecType> ();
				if(!isNews){
					List<ProductSpec> list = this.productSpecManager.getProductSpecListByProductId(productId);
					if(list != null && list.size() > 0){
						for(ProductSpec productSpec : list){
							productSpecMap.put(productSpec.getProductSpecId(), productSpec);
						}
					}
					
					List<ProductSpecType> productSpecTypeList = this.productSpecTypeManager.getProductSpecTypeListByProductId(productId);
					if(productSpecTypeList != null && productSpecTypeList.size() > 0){
						for(ProductSpecType productSpecType : productSpecTypeList){
							productSpecTypeMap.put(productSpecType.getSpecTypeId(), productSpecType);
						}
					}
				}
				
				int index = 0;
				Long packageProductId = StringUtil.nullToLong(Constants.conf.getProperty("jkd.invite.product.id"));
				List<String> productSkuList = new ArrayList<String> ();
				List<ProductSpec> productSpecList = new ArrayList<ProductSpec> ();
				for(Object object : objectMapList){
					@SuppressWarnings("unchecked")
					Map<String, Object> objectMap = (Map<String, Object>)object;
					String tmpProductSpecId = StringUtil.nullToString(objectMap.get("productSpecId"));
					String tmpPrimarySpecId = StringUtil.nullToString(objectMap.get("primarySpecId"));
					String tmpSecondarySpecId = StringUtil.nullToString(objectMap.get("secondarySpecId"));
					Double priceWholesale = StringUtil.nullToDoubleFormat(objectMap.get("priceWholesale"));
					Double priceRecommend = StringUtil.nullToDoubleFormat(objectMap.get("priceRecommend"));
					Double priceCost = StringUtil.nullToDoubleFormat(objectMap.get("priceCost"));
					Double v2Price = StringUtil.nullToDoubleFormat(objectMap.get("v2Price"));
					Double v3Price = StringUtil.nullToDoubleFormat(objectMap.get("v3Price"));
					Integer stockNumber = StringUtil.nullToInteger(objectMap.get("stockNumber"));
					String productCode = StringUtil.null2Str(objectMap.get("productCode"));
					String productSku = StringUtil.null2Str(objectMap.get("productSku"));
					Double weigth = StringUtil.nullToDoubleFormat(objectMap.get("weigth"));
					
					if(StringUtil.compareObject(tmpProductSpecId, "-1")) {
						continue;
					}
					
					
					Long productSpecId = null;
					Long primarySpecId = null;
					Long secondarySpecId = null;
					
					// ??????????????????????????????
					if(priceCost == null){
						// ????????????????????????
						msgModel.setIsSucc(false);
						msgModel.setMessage(getText("product.wholesale.priceCost.empty"));
						return msgModel;
					}else if(StringUtil.nullToDouble(priceWholesale).compareTo(StringUtil.nullToDouble(priceCost)) < 0){
						// ????????????????????????????????????
						msgModel.setIsSucc(false);
						msgModel.setMessage(getText("product.wholesale.priceWholesale.error"));
						return msgModel;
					}else if(StringUtil.nullToDouble(priceRecommend).compareTo(StringUtil.nullToDouble(priceWholesale)) <= 0){
						// ????????????????????????????????????
						msgModel.setIsSucc(false);
						msgModel.setMessage(getText("product.wholesale.priceRecommend.error"));
						return msgModel;
					}else if(StringUtil.isNull(productSku)){
						//???????????????????????????
						msgModel.setIsSucc(false);
						msgModel.setMessage("???????????????????????????");
						return msgModel;
					}else if(StringUtil.isNull(productCode)){
						//???????????????????????????
						msgModel.setIsSucc(false);
						msgModel.setMessage("????????????????????????");
						return msgModel;
					}else if(StringUtil.nullToBoolean(product.getIsOpenV2Price())) {
						if(!StringUtil.nullToBoolean(product.getIsOpenV3Price())) {
							msgModel.setIsSucc(false);
							msgModel.setMessage("??????v2?????????v3?????????????????????");
							return msgModel;
						}else if(StringUtil.nullToDouble(v2Price) <= 0
								|| StringUtil.nullToDouble(v3Price) <= 0) {
							msgModel.setIsSucc(false);
							msgModel.setMessage("v2??????v3???????????????0");
							return msgModel;
						}else if(StringUtil.nullToDouble(v2Price).compareTo(StringUtil.nullToDouble(priceWholesale)) > 0 ) {
							//v2????????????????????????
							msgModel.setIsSucc(false);
							msgModel.setMessage(getText("v2????????????????????????"));
							return msgModel;
						}
					}else if(StringUtil.nullToBoolean(product.getIsOpenV3Price())) {
						if(StringUtil.nullToDouble(v3Price) <= 0) {
							msgModel.setIsSucc(false);
							msgModel.setMessage("v3???????????????0");
							return msgModel;
						}else if(StringUtil.nullToDouble(v3Price).compareTo(StringUtil.nullToDouble(priceWholesale)) > 0 ) {
							//v3????????????????????????
							msgModel.setIsSucc(false);
							msgModel.setMessage(getText("v3????????????????????????"));
							return msgModel;
						}
					}
					
					// ?????????????????????productSku
					if(productSkuList != null && productSkuList.contains(productCode + productSku)
							&& !StringUtil.compareObject(packageProductId, productId)){
						//???????????????????????????
						msgModel.setIsSucc(false);
						msgModel.setMessage(String.format("\"%s,%s\"?????????????????????", productCode, productSku));
						return msgModel;
					}
					productSkuList.add(productCode + productSku);
					
					// ????????????????????????
					if(StringUtil.isNumber(tmpProductSpecId)){
						productSpecId = StringUtil.nullToLong(tmpProductSpecId);
						if(productSpecMap != null 
								&& productSpecMap.size() > 0
								&& !productSpecMap.containsKey(productSpecId)){
							// ???????????????????????????????????????
							msgModel.setIsSucc(false);
							msgModel.setMessage("????????????????????????????????????");
							return msgModel;
						}
					}
					
					// ?????????????????????????????????
					if(StringUtil.isNumber(tmpPrimarySpecId)){
						primarySpecId = StringUtil.nullToLong(tmpPrimarySpecId);
						if(productSpecTypeMap != null 
								&& productSpecTypeMap.size() > 0
								&& !productSpecTypeMap.containsKey(primarySpecId)){
							// ???????????????????????????????????????
							msgModel.setIsSucc(false);
							msgModel.setMessage("????????????????????????????????????");
							return msgModel;
						}
					}
					
					// ?????????????????????????????????
					if(StringUtil.isNumber(tmpSecondarySpecId)){
						secondarySpecId = StringUtil.nullToLong(tmpSecondarySpecId);
						if(productSpecTypeMap != null && productSpecTypeMap.size() > 0){
							if(productSpecTypeMap.containsKey(secondarySpecId)){
								isMoreSpecProduct = true;
							}else{
								// ???????????????????????????????????????
								msgModel.setIsSucc(false);
								msgModel.setMessage("????????????????????????????????????");
								return msgModel;
							}
						}
					}
					
					// ???????????????????????????
					if(StringUtil.null2Str(tmpSecondarySpecId).startsWith("tmp_")){
						isMoreSpecProduct = true;
					}
					
					ProductSpec productSpec = new ProductSpec ();
					productSpec.setTmpPrimarySpecId(tmpPrimarySpecId);
					productSpec.setTmpSecondarySpecId(tmpSecondarySpecId);
					productSpec.setProductSpecId(productSpecId);
					productSpec.setWeigth(weigth);
					productSpec.setPriceCost(priceCost);
					productSpec.setPriceRecommend(priceRecommend);
					productSpec.setPriceWholesale(priceWholesale);
					productSpec.setV2Price(v2Price);
					productSpec.setV3Price(v3Price);
					productSpec.setStockNumber(stockNumber);
					productSpec.setProductCode(productCode);
					productSpec.setProductSku(productSku);
					productSpec.setPrimarySpecId(primarySpecId);
					productSpec.setSecondarySpecId(secondarySpecId);
					productSpec.setSort(index++);
					if(!isNews) {
						//?????????????????????
						ProductSpec originProductSpec = productSpecMap.get(productSpec.getProductSpecId());
						if(originProductSpec != null && originProductSpec.getProductSpecId() != null) {
							productSpec.setSeckillPrice(originProductSpec.getSeckillPrice());
							productSpec.setSeckillProfit(originProductSpec.getSeckillProfit());
							productSpec.setSeckillTotalStock(originProductSpec.getSeckillTotalStock());
							productSpec.setSeckillSalesNumber(originProductSpec.getSeckillSalesNumber());
							productSpec.setSeckillLimitNumber(originProductSpec.getSeckillLimitNumber());
							productSpec.setSalesNumber(originProductSpec.getSalesNumber());
						}
					}
					productSpec.setCreateTime(DateUtil.getCurrentDate());
					productSpec.setUpdateTime(productSpec.getCreateTime());
					productSpecList.add(productSpec);
				}
				
				msgModel.setIsMoreSpecProduct(isMoreSpecProduct);
				msgModel.setData(productSpecList);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(true);
		return msgModel;
	}
	
	/**
	 * ????????????????????????
	 * @param imageGridJson
	 * @param imageType
	 * @return
	 */
	private MsgModel<List<ProductSpecType>> getProductSpecTypeList(String productSpecTypeGridJson, boolean isNews, Long productId){
		MsgModel<List<ProductSpecType>> msgModel = new MsgModel<List<ProductSpecType>> ();
		try{
			List<Object> objectMapList = new ArrayList<Object> ();
			try{
				objectMapList = StringUtil.jsonDeserialize(productSpecTypeGridJson);
			}catch(Exception e){
				log.debug(e.getMessage());
			}
			
			if(objectMapList != null && objectMapList.size() > 0){
				// ??????????????????????????????????????????
				Map<Long, ProductSpecType> productSpecTypeMap = new HashMap<Long, ProductSpecType> ();
				if(!isNews){
					List<ProductSpecType> list = this.productSpecTypeManager.getProductSpecTypeListByProductId(productId);
					if(list != null && list.size() > 0){
						for(ProductSpecType productSpecType : list){
							productSpecTypeMap.put(productSpecType.getSpecTypeId(), productSpecType);
						}
					}
				}
				
				// ??????????????????????????????
				Map<Long, ProductSpecModel> productSpecModelMap = new HashMap<Long, ProductSpecModel> ();
				List<ProductSpecModel> productSpecModelList = this.productSpecModelManager.getAll();
				if(productSpecModelList != null && productSpecModelList.size() > 0){
					for(ProductSpecModel productSpecModel : productSpecModelList){
						productSpecModelMap.put(productSpecModel.getSpecModelId(), productSpecModel);
					}
				}
				
				int index = 0;
				List<ProductSpecType> productSpecTypeList = new ArrayList<ProductSpecType> ();
				for(Object object : objectMapList){
					@SuppressWarnings("unchecked")
					Map<String, Object> objectMap = (Map<String, Object>)object;
					Long specModelId = StringUtil.nullToLong(objectMap.get("specModelId"));
					String specTypeName = StringUtil.nullToString(objectMap.get("name"));
					String filePath = StringUtil.nullToString(objectMap.get("filePath"));
					String tmpSpecTypeId = StringUtil.nullToString(objectMap.get("strId"));
					Long specTypeId = null;
					
					// ??????????????????????????????
					if(StringUtil.isNull(specTypeName)){
						msgModel.setIsSucc(false);
						msgModel.setMessage("????????????????????????");
						return msgModel;
					}
					
					// ??????????????????????????????
					if(productSpecModelMap == null || !productSpecModelMap.containsKey(specModelId)){
						msgModel.setIsSucc(false);
						msgModel.setMessage(String.format("'%s'???????????????????????????", specTypeName));
						return msgModel;
					}
					
					// ???????????????????????????????????????
					if(StringUtil.isNumber(tmpSpecTypeId)){
						specTypeId = StringUtil.nullToLong(tmpSpecTypeId);
						if(productSpecTypeMap != null && productSpecTypeMap.size() > 0){
							if(!productSpecTypeMap.containsKey(specTypeId)){
								// ???????????????????????????????????????
								msgModel.setIsSucc(false);
								msgModel.setMessage(String.format("'%s'?????????????????????????????????", specTypeName));
								return msgModel;
							}
						}
					}
					
					// ??????????????????
					if(StringUtil.null2Str(filePath).contains(Constants.DEPOSITORY)){
						String srcFilePath = Constants.DEPOSITORY_PATH + StringUtil.null2Str(filePath).replace(Constants.DEPOSITORY, "");
						if(FileUtil.checkFileExists(srcFilePath)){
							String realFilePath = CoreUtil.dateToPath("/images", filePath);
							String fullFilePath = Constants.EXTERNAL_IMAGE_PATH + "/upload" + realFilePath;
							boolean result = FileUploadUtil.moveFile(srcFilePath, fullFilePath);
							if (result == true && FileUtil.checkFileExists(fullFilePath)){
								filePath = realFilePath;
							}
							
						}
					}
					
					// ??????????????????
					String specModelName = null;
					ProductSpecModel productSpecModel = productSpecModelMap.get(specModelId);
					if(productSpecModel != null && productSpecModel.getSpecModelId() != null){
						specModelName = StringUtil.null2Str(productSpecModel.getName());
					}
					
					ProductSpecType productSpecType = new ProductSpecType ();
					productSpecType.setTmpSpecTypeId(tmpSpecTypeId);
					productSpecType.setSpecTypeId(specTypeId);
					productSpecType.setSpecTypeName(specTypeName);
					productSpecType.setSpecModelId(specModelId);
					productSpecType.setSpecModelName(specModelName);
					productSpecType.setImagePath(filePath);
					productSpecType.setSort(index++);
					productSpecType.setCreateTime(DateUtil.getCurrentDate());
					productSpecType.setUpdateTime(productSpecType.getCreateTime());
					productSpecTypeList.add(productSpecType);
				}
				
				msgModel.setData(productSpecTypeList);
				msgModel.setIsSucc(true);
				return msgModel;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		msgModel.setMessage("??????????????????");
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	
	@Autowired
	private UserSaleStandardManager userSaleStandardManager;
	
	/**
	 * ????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/limitProductList")
	public @ResponseBody Map<String, Object> limitProductList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> paramOrMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<Product> productList = new ArrayList<>();
		Long count = 0L;
		try {
			
			
			List<UserSaleStandard> list = userSaleStandardManager.getAll();
			if(list == null || list.isEmpty()) {
				resultMap.put("data", productList);
				resultMap.put("totalCount", count);
				resultMap.put("filters", filtersMap);
				return resultMap;
			}
			
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), Product.class);
			
			// filter??????????????????
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					String objectKey = StringUtil.null2Str(entry.getKey());
					paramMap.put(objectKey, entry.getValue());
				}
			}
			
			if(sortMap != null ) {
				sort = "status desc,o.createTime desc ";
				if(!sortMap.isEmpty()) {
					sort += " ,o."+sortMap.get("sort");
				}
				sortMap.put("sort", sort);
				sortMap.put("dir", sortMap.get("dir"));
			}

			// ????????????????????????
			Date date = DateUtil.getDateHourBefore(DateUtil.getCurrentDate(), list.get(0).getHours());
			paramMap.put("isDelete", false);
			paramOrMap.put("maxLimitNumber>=",list.get(0).getSalesNum());
			paramOrMap.put("lastRepTime<=",  date);

			count = this.productManager.countHql(paramMap, paramOrMap);
			if (count != null && count.longValue() > 0L) {
				productList = this.productManager.getHqlPages(paramMap, paramOrMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if(productList != null && productList.size() > 0){
					for(Product product : productList){
						String imagePath = StringUtil.null2Str(product.getImage());
						if(imagePath.startsWith("/")){
							imagePath = imagePath.substring(1);
						}
						
						product.setImage(imagePath);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", productList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	
	
	/**
	 * ????????????
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/updateRepStatus", method=RequestMethod.POST)
   	public @ResponseBody Map<String, Object> updateRepStatus(@RequestParam(value = "idListGridJson") String record, final HttpServletRequest request) {
    	Map<String, Object> resultMap = new HashMap<String, Object> ();
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if(idList == null || idList.size() == 0){
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("success", false);
			resultMap.put("message", getText("errors.nuKnow"));
			return resultMap;
		}
		
		try {
			this.productManager.updateProductRepStatus(idList);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("success", false);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}
	
	
	
}

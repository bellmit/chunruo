package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.chunruo.core.Constants;
import com.chunruo.core.model.FxPage;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductBrand;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.ProductImage;
import com.chunruo.core.model.TagModel;
import com.chunruo.core.service.FxPageManager;
import com.chunruo.core.service.ProductCategoryManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.TagModelManager;
import com.chunruo.core.util.CoreInitUtil;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;
import com.chunruo.webapp.util.TagUtil;
import com.chunruo.webapp.vo.ImageVo;

@Controller
@RequestMapping("/category/")
public class ProductCategoryController extends BaseController {
	@Autowired
	private ProductCategoryManager productCategoryManager;
	@Autowired
	private ProductManager productManager;
	@Autowired
	private TagModelManager tagModelManager;
	@Autowired
	private FxPageManager fxPageManager;
	
	@RequestMapping(value = "/categoryTreeList")
	public @ResponseBody Map<String, Object> categoryTreeList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String categoryId = StringUtil.null2Str(request.getParameter("node"));
		List<ProductCategory> categoryTreeList = new ArrayList<ProductCategory> ();
		try {
			ProductCategory productCategory = null;
			if(StringUtil.isNumber(categoryId) && (productCategory = this.productCategoryManager.get(StringUtil.nullToLong(categoryId))) != null){
				// ??????????????????ID??????????????????
				categoryTreeList = this.productCategoryManager.getProductCategory(productCategory.getCategoryId(), 1);
				if(categoryTreeList != null && categoryTreeList.size() > 0){
					for(ProductCategory category : categoryTreeList){
						category.setLeaf(true);
						category.setDescription(null);
						category.setProfit(null);
						category.setPathName(String.format("%s->%s", productCategory.getName(), category.getName()));
						
					}
				}
				resultMap.put("children", categoryTreeList);
				resultMap.put("total_count", categoryTreeList.size());
			}else{
				// ??????????????????
				categoryTreeList = this.productCategoryManager.getProductCategoryByLevel(1, 1);
				if(categoryTreeList != null && categoryTreeList.size() > 0){
					for(ProductCategory category : categoryTreeList){
						category.setLeaf(false);
						category.setDescription(null);
						category.setProfit(null);
					}
				}
				resultMap.put("children", categoryTreeList);
				resultMap.put("total_count", categoryTreeList.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * ????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getCategoryById")
	public @ResponseBody Map<String, Object> getCategoryById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long categoryId = StringUtil.nullToLong(request.getParameter("categoryId"));
		List<ImageVo> categoryImageList = new ArrayList<ImageVo> ();
		List<ImageVo> adImageList = new ArrayList<ImageVo> ();
		List<ImageVo> microImageList = new ArrayList<ImageVo> ();

		List<TagModel> tagModelList = new ArrayList<TagModel>();
		ProductCategory productCategory = null;
		List<ProductBrand> brandList = new ArrayList<ProductBrand>();
		String name = "";
		try{
			
			productCategory = productCategoryManager.get(categoryId);
			if(productCategory != null && productCategory.getCategoryId() != null) {
				
				
				String imagePath = productCategory.getImagePath();
				ImageVo image = new ImageVo ();
				if(!StringUtil.isNull(imagePath)){
					if(imagePath.startsWith("/")){
						imagePath = imagePath.substring(1);
					}
					image.setFileName(imagePath.substring(imagePath.lastIndexOf("/")));
					image.setFileType(FileUtil.getSuffixByFilename(imagePath));
					image.setFilePath(imagePath);
				}
				categoryImageList.add(image);
			}
			}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put("name", name);
		resultMap.put("success", true);
		resultMap.put("data", productCategory);
		resultMap.put("brandList", brandList);
		resultMap.put("categoryImageList", categoryImageList);
		resultMap.put("adImageList", adImageList);
		resultMap.put("microImageList", microImageList);
		resultMap.put("tagModelList", tagModelList);
		return resultMap;
	}


	/**
	 * ????????????
	 * ??????????????????????????????
	 * @param record
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteByIdList", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> deleteByIdList(@RequestParam(value = "idGridJson") String record,
			final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
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

		// ????????????
		List<ProductCategory> list = this.productCategoryManager.getByIdList(idList);
		if (list != null && list.size() > 0) {
			this.productCategoryManager.deleteByIdList(idList);
		}

		resultMap.put("success", true);
		resultMap.put("message", getText("delete.success"));
		return resultMap;
	}
	
	@RequestMapping(value = "/getCategoryList")
	public ModelAndView getCategoryList(final HttpServletRequest request){
		Long categoryId = StringUtil.nullToLong(request.getParameter("categoryId"));

		Map<ProductCategory, List<ProductCategory>> productCategoryMap = new HashMap<ProductCategory, List<ProductCategory>>();
		try {
			// ?????????????????????level??????
			Map<Long, List<ProductCategory>> childCategoryMap = new HashMap<Long, List<ProductCategory>>();
			List<ProductCategory> childCategoryList = this.productCategoryManager.getCategoryByLevel(2);
			if (childCategoryList != null && childCategoryList.size() > 0) {
				for (ProductCategory productCategory : childCategoryList) {
					// ??????????????????????????????
					String tagNames = TagUtil.getTagNamesByIdAndType(productCategory.getCategoryId(), TagModel.CATEGORY_TAG_TYPE);
					productCategory.setTagNames(tagNames);
					
					if (childCategoryMap.containsKey(productCategory.getParentId())) {
						childCategoryMap.get(productCategory.getParentId()).add(productCategory);
					} else {
						List<ProductCategory> list = new ArrayList<ProductCategory>();
						list.add(productCategory);
						childCategoryMap.put(productCategory.getParentId(), list);
					}
				}
			}

			// ?????????????????????????????????
			List<ProductCategory> productCategoryList = this.productCategoryManager.getCategoryByLevel(1);
			if (productCategoryList != null && productCategoryList.size() > 0) {
				for (ProductCategory productCategory : productCategoryList) {
					// ??????????????????????????????
					String tagNames = TagUtil.getTagNamesByIdAndType(productCategory.getCategoryId(), TagModel.CATEGORY_TAG_TYPE);
					productCategory.setTagNames(tagNames);
					productCategory.setExpanded(StringUtil.compareObject(categoryId, productCategory.getCategoryId()));
					productCategoryMap.put(productCategory, childCategoryMap.get(productCategory.getCategoryId()));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		Model model = new ExtendedModelMap();
		model.addAttribute("productCategoryMap", productCategoryMap);
		return new ModelAndView("nodes/productCategorys", model.asMap());
	}
	/**
	 * ???????????????????????????
	 * @param productCategory request
	 * @return
	 */
	@RequestMapping(value = "/saveCategory")
	public @ResponseBody Map<String, Object> saveFirstCategory(@ModelAttribute("productCategory") ProductCategory productCategory,final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String recodeGridJson = StringUtil.null2Str(request.getParameter("recodeGridJson"));
		try {
			List<Object> saveMap = StringUtil.jsonDeserialize(recodeGridJson);
			if (CollectionUtils.isEmpty(saveMap)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "??????????????????");
				return resultMap;
			} else if (StringUtil.isNull(productCategory.getName())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "????????????????????????");
				return resultMap;
			}

			byte[] bytes = productCategory.getName().getBytes("UTF-8");
			if (bytes.length > 15) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "??????????????????????????????5???");
				return resultMap;
			}
			
			// ??????
			Set<String> filePathMap = new HashSet<String>();
			List<ProductImage> saveImages = new ArrayList<ProductImage>();
			if (saveMap != null && saveMap.size() > 0) {
				for (int i = 0; i < saveMap.size(); i++) {
					@SuppressWarnings("unchecked")
					ProductImage productImage = createImage((Map<String, Object>) saveMap.get(i),
							ProductImage.IMAGE_TYPE_MATERIAL);
					if (StringUtil.isNull(productImage.getImageType())) {
						resultMap.put("success", Boolean.valueOf(false));
						resultMap.put("message", getText("image.saveAndDel.dataEexption"));
						return resultMap;
					} else if (productImage.getImageId() == null
							&& !StringUtil.isNullStr(productImage.getImagePath())) {
						if (filePathMap.contains(productImage.getImagePath())) {
							continue;
						}
						filePathMap.add(productImage.getImagePath());
					}
					saveImages.add(productImage);
				}
			} else {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("????????????????????????"));
				return resultMap;
			}
			
			// ????????????
			ProductCategory dbProductCategory = new ProductCategory();

			ProductImage productImage = saveImages.get(0);
			String imagePath = productImage.getImagePath();
			dbProductCategory.setCreateTime(DateUtil.getCurrentDate());
			dbProductCategory.setImagePath(imagePath);
			dbProductCategory.setSort(productCategory.getSort());
			dbProductCategory.setStatus(productCategory.getStatus());
			dbProductCategory.setName(productCategory.getName());
			dbProductCategory.setUpdateTime(DateUtil.getCurrentDate());
			// ??????????????????
			if (productCategory.getCategoryId() == 0) {
				dbProductCategory.setLevel(1);
				dbProductCategory.setParentId(0L);
			} else {
				// ??????????????????
				dbProductCategory.setLevel(2);
				dbProductCategory.setParentId(productCategory.getCategoryId());
			}
			this.productCategoryManager.save(dbProductCategory);
			// ????????????????????????
			CoreInitUtil.initProductCategoryConstantsList();
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", getText("save.failure"));
		return resultMap;
	}
	
	/**
	 * ??????????????????
	 * @param record imageType
	 * @return
	 */
	private ProductImage createImage(Map<String, Object> record, Integer imageType){
		
		
		ProductImage productImage = new ProductImage();
		if ((record.get("fileId") != null) && (StringUtil.isNumber(StringUtil.null2Str(record.get("fileId"))))) {
			productImage.setImageId(StringUtil.nullToLong(record.get("fileId")));
		}

		// ????????????
		String filePath=StringUtil.nullToString(record.get("filePath"));
		String fileType = FileUtil.getSuffixByFilename(filePath);
		if (StringUtil.isNull(fileType)) {
			fileType = StringUtil.nullToString(record.get("fileType"));
		}

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
		productImage.setImageType(imageType);
		productImage.setImageName(StringUtil.nullToString(record.get("fileName")));
		productImage.setImagePath(filePath);
		return productImage;
	}
	
	
	/**
	 * ????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/editProductCategory")
	public @ResponseBody Map<String, Object> editProductCategory(@ModelAttribute("productCategory") ProductCategory category,final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String recodeGridJson = StringUtil.null2Str(request.getParameter("recodeGridJson"));
		String name = StringUtil.nullToString(request.getParameter("name"));
		String scategoryId = StringUtil.nullToString(request.getParameter("categoryId"));
		Integer sort = StringUtil.nullToInteger(request.getParameter("sort"));
		try {
			List<Object> saveMap = StringUtil.jsonDeserialize(recodeGridJson);
			if (StringUtil.isNull(scategoryId)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "ajax no record");
				return resultMap;
			} else if (StringUtil.isNull(name)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "????????????????????????");
				return resultMap;
			}
			

			// ??????
			Set<String> filePathMap = new HashSet<String>();
			List<ProductImage> saveImages = new ArrayList<ProductImage>();
			if (saveMap != null && saveMap.size() > 0) {
				for (int i = 0; i < saveMap.size(); i++) {
					@SuppressWarnings("unchecked")
					ProductImage productImage = createImage((Map<String, Object>) saveMap.get(i),
							ProductImage.IMAGE_TYPE_MATERIAL);
					if (StringUtil.isNull(productImage.getImageType())) {
						resultMap.put("success", Boolean.valueOf(false));
						resultMap.put("message", getText("image.saveAndDel.dataEexption"));
						return resultMap;
					} else if (productImage.getImageId() == null
							&& !StringUtil.isNullStr(productImage.getImagePath())) {
						if (filePathMap.contains(productImage.getImagePath())) {
							continue;
						}
						filePathMap.add(productImage.getImagePath());
					}
					saveImages.add(productImage);
				}
			} else {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("????????????????????????"));
				return resultMap;
			}

			
			ProductCategory productCategory = this.productCategoryManager.get(StringUtil.nullToLong(scategoryId));
			if (productCategory != null && productCategory.getCategoryId() != null) {	

				
				ProductImage productImage = saveImages.get(0);
				String imagePath = productImage.getImagePath();
				productCategory.setImagePath(imagePath);
				productCategory.setName(name);
				productCategory.setSort(sort);
				productCategory.setUpdateTime(DateUtil.getCurrentDate());
				this.productCategoryManager.update(productCategory);

				// ????????????????????????
				CoreInitUtil.initProductCategoryConstantsList();
				
				resultMap.put("error", false);
				resultMap.put("success", true);
				resultMap.put("categoryId", productCategory.getCategoryId());
				resultMap.put("message", getText("????????????"));
				return resultMap;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", "????????????");
		return resultMap;
	}
	
	/**
	 * ?????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/setCategoryDisEnabled")
	public @ResponseBody Map<String, Object> setCategoryDisEnabled(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String scategoryId = StringUtil.nullToString(request.getParameter("categoryId"));
		String sstatus = StringUtil.nullToString(request.getParameter("status"));
		try {
			if (StringUtil.isNull(scategoryId)) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax no record"));
				return resultMap;
			} else if (StringUtil.isNull(sstatus)) {
				resultMap.put("success", false);
				resultMap.put("message", getText("???????????????????????????"));
				return resultMap;
			}
			Integer status = StringUtil.nullToInteger(sstatus);
			ProductCategory productCategory = this.productCategoryManager.get(StringUtil.nullToLong(scategoryId));
			if (productCategory != null && productCategory.getCategoryId() != null) {
				// ???????????????????????????????????????????????????
				if (productCategory.getLevel() == 1 && status == 0) {
					this.productCategoryManager.updateCategoryByParentId(productCategory.getCategoryId(), 0);
				}
				productCategory.setStatus(status);
				productCategory.setUpdateTime(DateUtil.getCurrentDate());
				this.productCategoryManager.update(productCategory);

				//????????????????????????
				CoreInitUtil.initProductCategoryConstantsList();
				resultMap.put("success", true);
				resultMap.put("message", getText("submit.success"));
				return resultMap;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("success", false);
		resultMap.put("message", getText("submit.failure"));
		return resultMap;
	}
	
	/**
	 * ????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteCategory")
	public @ResponseBody Map<String, Object> deleteCategory(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long categoryId = StringUtil.nullToLong(request.getParameter("categoryId"));
		Integer level = StringUtil.nullToInteger(request.getParameter("level"));
		try {
			ProductCategory productCategory = this.productCategoryManager.get(categoryId);
			if (productCategory == null || productCategory.getCategoryId() == null) {
				resultMap.put("success", false);
				resultMap.put("message", "??????,?????????????????????");
				return resultMap;
			}
			// ????????????????????????????????????
			Map<String, Object> paramMap = new HashMap<String, Object>();
			// ??????????????????
			if (level == 1) {
				paramMap.put("categoryFid", productCategory.getCategoryId());
			} else if (level == 2) {
				// ??????????????????
				paramMap.put("categoryId", productCategory.getCategoryId());
			}
			
			// ???????????????????????????????????????????????????
			List<Product> productList = this.productManager.getHqlPages(paramMap);
			if (productList != null && productList.size() > 0) {
				resultMap.put("success", false);
				resultMap.put("message", "??????,???????????????????????????????????????????????????");
				return resultMap;
			}
			this.productCategoryManager.remove(productCategory.getCategoryId());
			
			//????????????????????????????????????????????????
			List<Long> tagModelIdList = new ArrayList<Long>();
			List<TagModel> tagModelList = this.tagModelManager.getTagModelListByObjectId(productCategory.getCategoryId(), TagModel.CATEGORY_TAG_TYPE);
			if (tagModelList != null && !tagModelList.isEmpty()) {
				for (TagModel tagModel : tagModelList) {
					tagModelIdList.add(tagModel.getTagId());
				}
			}
			
			//??????????????????
			this.tagModelManager.deleteByIdList(tagModelIdList);
			
			//????????????????????????
			CoreInitUtil.initProductCategoryConstantsList();
			resultMap.put("success", true);
			resultMap.put("message", getText("????????????"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("success", false);
		resultMap.put("message", getText("submit.failure"));
		return resultMap;
	}
	
	@RequestMapping(value = "/XFxPageList")
	public @ResponseBody Map<String, Object> XFxPageList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<FxPage> fxPageList = new ArrayList<FxPage> ();
		Long count = 0L;
		try {
			// ?????????@??????????????????ID???#????????????
			String keyword = StringUtil.nullToString(request.getParameter("query"));
			StringBuffer sqlBuffer = new StringBuffer ();
    		sqlBuffer.append("select distinct(w.page_id) from jkd_fx_page w where w.is_delete = 0 and category_type in(1,2) ");
    		if(!StringUtil.isNull(keyword)) {
    			sqlBuffer.append("and (upper(w.page_name) like '%" + StringUtil.null2Str(keyword).toUpperCase() + "%' or w.page_id = '"+ StringUtil.null2Str(keyword)+"')");
    		}
    		List<Object[]> objectList = this.fxPageManager.querySql(sqlBuffer.toString());
    		List<Long> fxPageIdList = new ArrayList<Long> ();
    		if(objectList != null && objectList.size() > 0) {
    			for(Object object : objectList) {
    				fxPageIdList.add(StringUtil.nullToLong(object));
    			}
    			
    			if(fxPageIdList != null && fxPageIdList.size() > 0) {
    				fxPageList = this.fxPageManager.getByIdList(fxPageIdList);
        		}
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", fxPageList);
		resultMap.put("totalCount", count);
		return resultMap;
	}
	
	@RequestMapping(value = "/XDiscoveryList")
	public @ResponseBody Map<String, Object> XDiscoveryList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		return resultMap;
	}
	
	
	@RequestMapping(value = "/XDiscoveryCreaterList")
	public @ResponseBody Map<String, Object> XDiscoveryCreaterList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		return resultMap;
	}
	
	@RequestMapping(value = "/XDiscoveryModuleList")
	public @ResponseBody Map<String, Object> XDiscoveryModuleList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		return resultMap;
	}
	
}

package com.chunruo.webapp.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.core.Constants;
import com.chunruo.core.model.Country;
import com.chunruo.core.model.Product;
import com.chunruo.core.model.ProductBrand;
import com.chunruo.core.model.ProductCategory;
import com.chunruo.core.model.TagModel;
import com.chunruo.core.service.CountryManager;
import com.chunruo.core.service.ProductBrandManager;
import com.chunruo.core.service.ProductCategoryManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.service.TagModelManager;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.HtmlDecodeUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.webapp.BaseController;
import com.chunruo.webapp.util.TagUtil;
import com.chunruo.webapp.vo.ImageVo;

@Controller
@RequestMapping("/brand/")
public class ProductBrandController extends BaseController {
	@Autowired
	private ProductManager productManager;
	@Autowired
	private ProductBrandManager productBrandManager;
	@Autowired
	private TagModelManager tagModelManager;
	@Autowired
	private CountryManager countryManager;
	@Autowired
	private ProductCategoryManager productCategoryManager;
	
	/**
	 * 品牌列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/brandList")
	public @ResponseBody Map<String, Object> brandList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> paramOrMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<ProductBrand> brandList = new ArrayList<ProductBrand>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			String query = StringUtil.nullToString(request.getParameter("query"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), ProductBrand.class);

			// 内容、@用户名、用户ID、#手机号码
			String keyword = StringUtil.nullToString(request.getParameter("keyword"));
			if (!StringUtil.isNullStr(keyword)) {
				// 内容
				paramMap.put("title", "%" + keyword + "%");
				paramMap.put("content", "%" + keyword + "%");
			}
			
			if(StringUtil.isNumber(query)) {
				paramMap.put("brandId",	StringUtil.nullToLong(query));
			}else if(!StringUtil.isNull(query)) {
				paramOrMap.put("name","%"+ query + "%");
			}

			// filter过滤字段查询
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			count = this.productBrandManager.countHql(paramMap,paramOrMap);
			if (count != null && count.longValue() > 0L) {
				brandList = this.productBrandManager.getHqlPages(paramMap, paramOrMap,start, limit, sortMap.get("sort"), sortMap.get("dir"));
				if(brandList != null && brandList.size() > 0){
					for (ProductBrand productBrand : brandList) {
						String imagePath = StringUtil.null2Str(productBrand.getImage());
						if(imagePath.startsWith("/")){
							imagePath = imagePath.substring(1);
						}
//						productBrand.setImage("upload/" + imagePath);
						// 获得品牌对应标签名称
						String tagNames = TagUtil.getTagNamesByIdAndType(productBrand.getBrandId(), TagModel.BRAND_TAG_TYPE);
						productBrand.setTagNames(tagNames);
						
						Country country = this.countryManager.get(StringUtil.nullToLong(productBrand.getCountryId()));
		    			if(country != null && country.getCountryId() !=null) {
		    				productBrand.setCountryName(StringUtil.null2Str(country.getCountryName()));
		    			}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		resultMap.put("data", brandList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	@RequestMapping(value = "/XBrandList")
	public @ResponseBody Map<String, Object> XbrandList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<ProductBrand> productSpecModelList = new ArrayList<ProductBrand> ();
		Long count = 0L;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));

			// 内容、@用户名、用户ID、#手机号码
			String keyword = StringUtil.nullToString(request.getParameter("query"));
			if(!StringUtil.isNullStr(keyword)) {
				// 内容
				paramMap.put("name", "%" + keyword + "%");
			}
			
			if(StringUtil.isNumber(keyword)) {
				paramMap.put("brandId",	StringUtil.nullToLong(keyword));
			}

			count = this.productBrandManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				productSpecModelList = this.productBrandManager.getHqlPages(paramMap, sortMap.get("sort"), sortMap.get("dir"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", productSpecModelList);
		resultMap.put("totalCount", count);
		return resultMap;
	}
	
	/**
	 * 保存品牌
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveBrand")
	public @ResponseBody Map<String, Object> saveBrand(@ModelAttribute("productBrand") ProductBrand brand, final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String recodeGridJson = StringUtil.null2Str(request.getParameter("recodeGridJson"));
		String countryImageJson = StringUtil.null2Str(request.getParameter("countryImageJson"));
		String adImageJson = StringUtil.null2Str(request.getParameter("adImageJson"));
		String backImageJson = StringUtil.null2Str(request.getParameter("backImageJson"));
		Long categoryId = StringUtil.nullToLong(request.getParameter("categoryId"));
		String initial = StringUtil.null2Str(brand.getInitial());
		String tagNames = StringUtil.null2Str(brand.getTagNames()).replaceAll("\\s*", "");
		try {
			// 检查是否新建批发商品
			boolean isNews = (brand.getBrandId() == null);
			int index = 0;
			if (isNews) {
				brand.setCreateTime(DateUtil.getCurrentDate());
				index = 1;
			} else {
				ProductBrand dbBrand = this.productBrandManager.get(brand.getBrandId());
				if (dbBrand == null || dbBrand.getBrandId() == null) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("errors.nuKnow"));
					return resultMap;
				} else if (dbBrand.getCreateTime() == null) {
					dbBrand.setCreateTime(DateUtil.getCurrentDate());
				}
				brand.setCreateTime(dbBrand.getCreateTime());
			}
			byte[] bytes = brand.getName().getBytes("UTF-8");
			// 判断品牌名称不得超过十个汉字
			if (bytes.length > 30) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("品牌名称过长!!!"));
				return resultMap;
			} else if (brand.getShortName() == null || StringUtil.isNull(brand.getShortName())) {
				// 品牌名称不能为空
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("品牌简称不能为空"));
				return resultMap;
			}else if (brand.getShortName().length() > 6) {
				// 判断品牌名称不得超过6个字
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("品牌简称过长!!!"));
				return resultMap;
			} else if (brand.getName() == null || StringUtil.isNull(brand.getName())) {
				// 品牌名称不能为空
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("品牌名称不能为空"));
				return resultMap;
			} else if (StringUtil.isNull(initial) || !StringUtil.isOneEnglishCharacters(initial)) {
				// 字母不能为空
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("品牌首字母不合法"));
				return resultMap;
			}

			if (StringUtil.nullToBoolean(brand.getIsHot())) {
				List<ProductBrand> brandList = productBrandManager.getAll();
				if (brandList != null && brandList.size() > 0) {
					for (ProductBrand productBrand : brandList) {
						if (StringUtil.nullToBoolean(productBrand.getIsHot())) {
							index++;
						}
					}
					if (index > 20) {
						// 最多20个热门商品
						resultMap.put("error", true);
						resultMap.put("success", true);
						resultMap.put("message", getText("最多20个热门品牌"));
						return resultMap;
					}
				}
			}
			
			List<Object> saveMap = StringUtil.jsonDeserialize(recodeGridJson);
			if (CollectionUtils.isEmpty(saveMap) || saveMap.size() > 1) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "品牌图片只能传1张！");
				return resultMap;
			}
			
			List<Object> saveMaps = StringUtil.jsonDeserialize(countryImageJson);
			if (CollectionUtils.isEmpty(saveMaps) || saveMaps.size() > 1) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "国旗只能传1张！");
				return resultMap;
			}
			
			
			// 创建的标签名称
			List<String> tagNameList = StringUtil.strToStrList(tagNames, ",");
			// 判断保存的标签是否有重复
			Set<String> tagNameSet = new HashSet<String>();
			for (String string : tagNameList) {
				if (StringUtil.haveEscapeText(string)) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", "保存的标签只能是中英文或数字");
					return resultMap;
				}
				tagNameSet.add(string);
			}
			if (!StringUtil.compareObject(tagNameSet.size(), tagNameList.size())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "保存的标签不能有重复");
				return resultMap;
			}
			
			// 图片
			MsgModel<String> msgModel = this.createImage((Map<String, Object>) saveMap.get(0));
			if(!StringUtil.nullToBoolean(msgModel.getIsSucc())){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("品牌图片不能为空"));
				return resultMap;
			}
			
			// 国旗
			MsgModel<String> xsgModel = this.createImage((Map<String, Object>) saveMaps.get(0));
			if(!StringUtil.nullToBoolean(xsgModel.getIsSucc())){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("国旗不能为空"));
				return resultMap;
			}
			
			
			String adImage = "";
			if(!StringUtil.isNull(adImageJson)) {
				List<Object> adMaps = StringUtil.jsonDeserialize(adImageJson);
				if (adMaps != null && !adMaps.isEmpty()) {
					// 宣传图
					MsgModel<String> adModel = this.createImage((Map<String, Object>) adMaps.get(0));
					if(StringUtil.nullToBoolean(adModel.getIsSucc())){
						adImage = StringUtil.null2Str(adModel.getData());
						
					}
				}
			}
			
			String backImage = "";
			if(!StringUtil.isNull(backImageJson)) {
				List<Object> backMaps = StringUtil.jsonDeserialize(backImageJson);
				if (backMaps != null && !backMaps.isEmpty()) {
					// 背景图
					MsgModel<String> backModel = this.createImage((Map<String, Object>) backMaps.get(0));
					if(StringUtil.nullToBoolean(backModel.getIsSucc())){
						backImage = StringUtil.null2Str(backModel.getData());
					}
				}
			}
			
		
			if(StringUtil.nullToBoolean(brand.getIsHot())) {
				ProductCategory productCategory = this.productCategoryManager.get(categoryId);
			    if(productCategory == null 
			    		|| productCategory.getCategoryId() == null
			    		|| !StringUtil.nullToBoolean(productCategory.getStatus())
			    		|| !StringUtil.compareObject(productCategory.getLevel(), ProductCategory.PRODUCT_CATEGORY_LEVEL_FIRST)) {
			    	resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("请选择正确的所属分类"));
					return resultMap;
			    }
			    brand.setCategroyId(StringUtil.nullToLong(productCategory.getCategoryId()));
			}
			
			// 保存品牌
			brand.setImage(msgModel.getData());
			brand.setCountryImage(xsgModel.getData());
			brand.setAdImage(adImage);
			brand.setBackgroundImage(backImage);
			brand.setInitial(StringUtil.null2Str(brand.getInitial().toUpperCase()));
			brand.setUpdateTime(DateUtil.getCurrentDate());
			brand = this.productBrandManager.save(brand);
			
			// 直接放入品牌集合中，直接生效
			brand.setName(HtmlDecodeUtil.get(brand.getName()));
			Constants.PRODUCT_BRAND_MAP.put(brand.getBrandId(), brand);
			
			// 原标签
			List<TagModel> tagModelBakList = this.tagModelManager.getTagModelListByObjectId(brand.getBrandId(), TagModel.BRAND_TAG_TYPE);
			List<TagModel> saveTagModelList = new ArrayList<TagModel>();
			if (tagModelBakList !=null && !tagModelBakList.isEmpty()) {
				Map<String, TagModel> dbTagModelMap = new HashMap<String, TagModel>();
				List<Long> dbTagIdList = new ArrayList<Long>();
				for (TagModel tagModel : tagModelBakList) {
					dbTagModelMap.put(tagModel.getName(), tagModel);
					dbTagIdList.add(tagModel.getTagId());
				}
				
				for (String tagName : tagNameList) {
					if (dbTagModelMap.containsKey(tagName)) {
						TagModel tagModel = dbTagModelMap.get(tagName);
						dbTagIdList.remove(tagModel.getTagId());
					}else {
						if(StringUtil.nullToBoolean(this.tagModelManager.isExistName(tagName))) {
							resultMap.put("error", true);
							resultMap.put("success", true);
							resultMap.put("message", String.format("\"%s\"此标签已存在", StringUtil.null2Str(tagName)));
							return resultMap;
						}
						TagModel tagModel = new TagModel();
						tagModel.setName(tagName);
						tagModel.setTagType(TagModel.BRAND_TAG_TYPE);
						tagModel.setObjectId(brand.getBrandId());
						tagModel.setCreateTime(DateUtil.getCurrentDate());
						tagModel.setUpdateTime(tagModel.getCreateTime());
						saveTagModelList.add(tagModel);
					}
				}
				this.tagModelManager.deleteByIdList(dbTagIdList);
			}else {
				for (String tagName : tagNameList) {
					if(StringUtil.nullToBoolean(this.tagModelManager.isExistName(tagName))) {
						resultMap.put("error", true);
						resultMap.put("success", true);
						resultMap.put("message", String.format("\"%s\"此标签已存在", StringUtil.null2Str(tagName)));
						return resultMap;
					}
					TagModel tagModel = new TagModel();
					tagModel.setName(tagName);
					tagModel.setTagType(TagModel.BRAND_TAG_TYPE);
					tagModel.setObjectId(brand.getBrandId());
					tagModel.setCreateTime(DateUtil.getCurrentDate());
					tagModel.setUpdateTime(tagModel.getCreateTime());
					saveTagModelList.add(tagModel);
				}
			}
			
			this.tagModelManager.batchInsert(saveTagModelList, saveTagModelList.size());
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", getText("保存失败"));
		return resultMap;
	}
	
	/**
	 * 删除商品品牌
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteBrand")
	public @ResponseBody Map<String, Object> deleteBrand(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long brandId = StringUtil.nullToLong(request.getParameter("brandId"));
		try {
			ProductBrand brand = this.productBrandManager.get(brandId);
			if(brand == null || brand.getBrandId() == null){
				resultMap.put("success", false);
				resultMap.put("message", "错误,删除品牌不存在");
				return resultMap;
			}
			// 查询商品是否配置对应模板
			Map<String, Object> paramMap = new HashMap<String, Object> ();
			paramMap.put("brandId", brand.getBrandId());
			List<Product> wholesaleList = this.productManager.getHqlPages(paramMap);
			if(wholesaleList != null && wholesaleList.size() > 0){
				resultMap.put("success", false);
				resultMap.put("message", "错误,品牌仍有绑定商品，请解绑后再删除");
				return resultMap;
			}
			this.productBrandManager.remove(brand.getBrandId());
			
			// 直接从品牌集合中删除，直接生效
			Constants.PRODUCT_BRAND_MAP.remove(brand.getBrandId());
			
			//查询品牌对应的标签模版及商品标签
			List<Long> tagModelIdList = new ArrayList<Long>();
			List<TagModel> tagModelList = this.tagModelManager.getTagModelListByObjectId(brand.getBrandId(), TagModel.BRAND_TAG_TYPE);
			if (tagModelList != null && !tagModelList.isEmpty()) {
				for (TagModel tagModel : tagModelList) {
					tagModelIdList.add(tagModel.getTagId());
				}
			}
			//删除标签模版
			this.tagModelManager.deleteByIdList(tagModelIdList);
			
			resultMap.put("success", true);
			resultMap.put("message", getText("操作成功"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("success", false);
		resultMap.put("message", getText("操作失败"));
		return resultMap;
	}
	
	/**
	 * 得到品牌详情
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getBrandById")
	public @ResponseBody Map<String, Object> getBrandById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String brandId = StringUtil.null2Str(request.getParameter("brandId"));
		List<ImageVo> imageList = new ArrayList<ImageVo> ();
		List<ImageVo> countryImageList = new ArrayList<ImageVo> ();
		List<ImageVo> adImageList = new ArrayList<ImageVo> ();
		List<ImageVo> backImageList = new ArrayList<ImageVo> ();
		List<TagModel> tagModelList = new ArrayList<TagModel>();
		ProductBrand productBrand = null;
		try {
			if (StringUtil.isNumber(brandId) && (productBrand = this.productBrandManager.get(StringUtil.nullToLong(brandId))) != null) {
            	String imagePath = productBrand.getImage();
    			ImageVo image = new ImageVo ();
    			if(!StringUtil.isNull(imagePath)){
    				if(imagePath.startsWith("/")){
    					imagePath = imagePath.substring(1);
    				}
    				image.setFileName(imagePath.substring(imagePath.lastIndexOf("/")));
    				image.setFileType(FileUtil.getSuffixByFilename(imagePath));
    				image.setFilePath(imagePath);
    			}
    			
    			String countryImagePath = productBrand.getCountryImage();
    			ImageVo countryImage = new ImageVo ();
    			if(!StringUtil.isNull(countryImagePath)){
    				if(countryImagePath.startsWith("/")){
    					countryImagePath = countryImagePath.substring(1);
    				}
    				countryImage.setFileName(countryImagePath.substring(countryImagePath.lastIndexOf("/")));
    				countryImage.setFileType(FileUtil.getSuffixByFilename(countryImagePath));
    				countryImage.setFilePath(countryImagePath);
    			}
    			
    			String adImagePath = productBrand.getAdImage();
    			ImageVo adImage = new ImageVo ();
    			if(!StringUtil.isNull(adImagePath)){
    				if(adImagePath.startsWith("/")){
    					adImagePath = adImagePath.substring(1);
    				}
    				adImage.setFileName(adImagePath.substring(adImagePath.lastIndexOf("/")));
    				adImage.setFileType(FileUtil.getSuffixByFilename(adImagePath));
    				adImage.setFilePath(adImagePath);
    			}
    			
    			String backImagePath = productBrand.getBackgroundImage();
    			ImageVo backImage = new ImageVo ();
    			if(!StringUtil.isNull(backImagePath)){
    				if(backImagePath.startsWith("/")){
    					backImagePath = backImagePath.substring(1);
    				}
    				backImage.setFileName(backImagePath.substring(backImagePath.lastIndexOf("/")));
    				backImage.setFileType(FileUtil.getSuffixByFilename(backImagePath));
    				backImage.setFilePath(backImagePath);
    			}
    			
    			ProductCategory productCategory = this.productCategoryManager.get(StringUtil.nullToLong(productBrand.getCategroyId()));
    			if(productCategory != null && productCategory.getCategoryId() != null) {
    				resultMap.put("productCategory", productCategory);
    			}
    			Country country = this.countryManager.get(StringUtil.nullToLong(productBrand.getCountryId()));
    			if(country != null && country.getCountryId() !=null) {
    				productBrand.setCountryName(StringUtil.null2Str(country.getCountryName()));
    			}
    			tagModelList = this.tagModelManager.getTagModelListByObjectId(productBrand.getBrandId(), TagModel.BRAND_TAG_TYPE);
    			imageList.add(image);
    			countryImageList.add(countryImage);
    			backImageList.add(backImage);
    			adImageList.add(adImage);
            	resultMap.put("success", true);
    			resultMap.put("data", productBrand);
    			resultMap.put("imageList", imageList);
    			resultMap.put("backImageList", backImageList);
    			resultMap.put("adImageList", adImageList);
    			resultMap.put("countryImageList", countryImageList);
    			resultMap.put("tagModelList", tagModelList);
            }
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		return resultMap;
	}
	
	/**
	 * 设置品牌为主页
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/homePageStatus")
	public @ResponseBody Map<String, Object> setHomePage(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String status = StringUtil.nullToString(request.getParameter("status"));
		try {
			if (StringUtil.isNull(status)) {
				resultMap.put("success", false);
				resultMap.put("message", getText("错误！设置出错"));
				return resultMap;
			}
			
			List<ProductBrand> brandList = this.productBrandManager.getAll();
			if (brandList != null && !brandList.isEmpty()) {
				for (ProductBrand productBrand : brandList) {
					productBrand.setIsHomePage(StringUtil.nullToBoolean(status));
					productBrand.setUpdateTime(DateUtil.getCurrentDate());
				}
				this.productBrandManager.batchInsert(brandList, brandList.size());
			}
			resultMap.put("message", getText("设置完成"));
			resultMap.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		return resultMap;
	}
	
	/**
	 * 图片转换工具
	 * @param record
	 * @return
	 */
	private MsgModel<String> createImage(Map<String, Object> record) {
		MsgModel<String> msgModel = new MsgModel<String> ();
		try{
			// 文件类型
			String filePath = StringUtil.nullToString(record.get("filePath"));
			String fileType = FileUtil.getSuffixByFilename(filePath);
			if (StringUtil.isNull(fileType)) {
				fileType = StringUtil.nullToString(record.get("fileType"));
			}
			
			if (StringUtil.null2Str(filePath).contains(Constants.DEPOSITORY)) {
				String srcFilePath = Constants.DEPOSITORY_PATH + StringUtil.null2Str(filePath).replace(Constants.DEPOSITORY, "");
				if (FileUtil.checkFileExists(srcFilePath)) {
					File file = new File(srcFilePath);
					String realFilePath = CoreUtil.dateToPath("/images", file.getName());
					String fullFilePath = Constants.EXTERNAL_IMAGE_PATH + "/upload/" + realFilePath;
					boolean result = FileUploadUtil.moveFile(srcFilePath, fullFilePath);
					if (result == true && FileUtil.checkFileExists(fullFilePath)) {
						filePath = realFilePath;
					}
				}
			}
			
//			if(filePath.startsWith("/upload/")){
//				filePath = filePath.substring("/upload/".length());
//			}else if(filePath.startsWith("upload/")){
//				filePath = filePath.substring("upload/".length());
//			}
			msgModel.setData(filePath);
			msgModel.setIsSucc(true);
			return msgModel;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		msgModel.setIsSucc(false);
		return msgModel;
	}
	
	/**
	 * 单个商品搜索
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/searchBrandById")
	public @ResponseBody Map<String, Object> searchBrandById(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long brandId = StringUtil.nullToLong(request.getParameter("brandId"));
		try {
			ProductBrand productBrand = this.productBrandManager.get(brandId);
			if(productBrand != null && productBrand.getBrandId() != null) {
				resultMap.put("data", productBrand);
			}
		}catch(Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		resultMap.put("success", true);
		return resultMap;
	}
	
	
	@RequestMapping(value = "/XFirstCategoryList")
	public @ResponseBody Map<String, Object> XFirstCategoryList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<ProductCategory> productCategoryList = new ArrayList<ProductCategory> ();
		Long count = 0L;
		try {
			// 内容、@用户名、用户ID、#手机号码
			String keyword = StringUtil.nullToString(request.getParameter("query"));
			
			StringBuffer sqlBuffer = new StringBuffer ();
    		sqlBuffer.append("select distinct(w.category_id) from jkd_product_category w where w.level = 1 and w.status = 1 ");
    		if(!StringUtil.isNull(keyword)) {
    			sqlBuffer.append("and (upper(w.name) like '%" + StringUtil.null2Str(keyword).toUpperCase() + "%' or w.category_id = '"+ StringUtil.null2Str(keyword)+"')");
    		}
    		
    		List<Object[]> objectList = this.productCategoryManager.querySql(sqlBuffer.toString());
    		List<Long> categoryIdList = new ArrayList<Long> ();
    		if(objectList != null && objectList.size() > 0) {
    			for(Object object : objectList) {
    				categoryIdList.add(StringUtil.nullToLong(object));
    			}
    			
    			if(categoryIdList != null && categoryIdList.size() > 0) {
    				productCategoryList = this.productCategoryManager.getByIdList(categoryIdList);
        		}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", productCategoryList);
		resultMap.put("totalCount", count);
		return resultMap;
	}

}

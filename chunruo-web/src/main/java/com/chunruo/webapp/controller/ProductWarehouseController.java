package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chunruo.core.Constants;
import com.chunruo.core.model.ProductImage;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.model.ProductWarehouseTemplate;
import com.chunruo.core.service.ProductWarehouseManager;
import com.chunruo.core.service.ProductWarehouseTemplateManager;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;

@Controller
@RequestMapping("/productWarehouse")
public class ProductWarehouseController extends BaseController {

	@Autowired
	private ProductWarehouseManager productWarehouseManager;
	@Autowired
	private ProductWarehouseTemplateManager productWarehouseTemplateManager;
	
	@RequestMapping(value="/templateList")
	public @ResponseBody Map<String, Object> templateList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Map<String, Object> paramMap = new HashMap<String, Object> ();
		Map<String, Object> filtersMap = new HashMap<String, Object> ();
		List<ProductWarehouseTemplate> productWarehouseTemplateList = new ArrayList<ProductWarehouseTemplate>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), ProductWarehouseTemplate.class);

			// filter??????????????????
			if(filtersMap != null && filtersMap.size() > 0){
				for(Entry<String, Object> entry : filtersMap.entrySet()){
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}
			count = this.productWarehouseTemplateManager.countHql(paramMap);
			if(count != null && count.longValue() > 0L){
				productWarehouseTemplateList = this.productWarehouseTemplateManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", productWarehouseTemplateList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	/**
	 * ??????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveTemplate")
	public @ResponseBody Map<String, Object> saveTemplate(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long templateId = StringUtil.nullToLong(request.getParameter("templateId"));
		String name = StringUtil.null2Str(request.getParameter("name"));
		String recodeGridJson = StringUtil.null2Str(request.getParameter("recodeGridJson"));

		try {
			
			if(StringUtil.isNull(name)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("??????????????????"));
				return resultMap;
			}
			
			List<Object> saveMap = StringUtil.jsonDeserialize(recodeGridJson);	
			if(CollectionUtils.isEmpty(saveMap)){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "??????????????????");
				return resultMap;
			}
			
			// ??????
			Set<String> filePathMap = new HashSet<String> ();
			List<ProductImage> saveImages = new ArrayList<ProductImage>();
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
			ProductImage productImage = saveImages.get(0);
			String imagePath = productImage.getImagePath();
			
			ProductWarehouseTemplate productWarehouseTemplate = this.productWarehouseTemplateManager.get(templateId);
			if (productWarehouseTemplate != null && productWarehouseTemplate.getTemplateId() != null) {
				//??????
				productWarehouseTemplate.setName(name);
				productWarehouseTemplate.setImagePath(imagePath);
				productWarehouseTemplate.setUpdateTime(DateUtil.getCurrentDate());
			}else {
				ProductWarehouseTemplate dbTemplate = this.productWarehouseTemplateManager.getMemberYearsTemplateByName(name);
				if(dbTemplate != null && dbTemplate.getTemplateId() > 0) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("???????????????????????????????????????"));
					return resultMap;
				}
				//??????
				productWarehouseTemplate = new ProductWarehouseTemplate();
				productWarehouseTemplate.setStatus(true);
				productWarehouseTemplate.setName(name);
				productWarehouseTemplate.setImagePath(imagePath);
				productWarehouseTemplate.setCreateTime(DateUtil.getCurrentDate());
				productWarehouseTemplate.setUpdateTime(DateUtil.getCurrentDate());
			}
			this.productWarehouseTemplateManager.save(productWarehouseTemplate);
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", getText("??????, ????????????"));
		return resultMap;
	}
	
	
	/**
	 * ??????????????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/setTemplateStatus")
	public @ResponseBody Map<String, Object> setTemplateStatus(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Boolean isEnabled = StringUtil.nullToBoolean(request.getParameter("isEnabled"));
		String record = StringUtil.null2Str(request.getParameter("idListGridJson"));
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
			
			List<ProductWarehouseTemplate> productWarehouseTemplateList = this.productWarehouseTemplateManager.getByIdList(idList);
			if(productWarehouseTemplateList != null && productWarehouseTemplateList.size() > 0){
				for(ProductWarehouseTemplate productWarehouseTemplate : productWarehouseTemplateList){
					productWarehouseTemplate.setStatus(isEnabled);
					productWarehouseTemplate.setUpdateTime(DateUtil.getCurrentDate());
				}
				this.productWarehouseTemplateManager.batchInsert(productWarehouseTemplateList, productWarehouseTemplateList.size());
			}
			
			resultMap.put("success", true);
           	resultMap.put("message", getText("save.success"));
           	return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		
		resultMap.put("success", false);
		resultMap.put("message", "?????????????????????");
		return resultMap;
	}

	
	@RequestMapping(value="/getProductWarehouseListByTemplateId")
	public @ResponseBody Map<String, Object> getProductWarehouseListByTemplateId(final HttpServletRequest request) {
		Long templateId = StringUtil.nullToLong(request.getParameter("templateId"));
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Map<String, Object> paramMap = new HashMap<String, Object> ();
		Map<String, Object> filtersMap = new HashMap<String, Object> ();
		List<ProductWarehouse> productWarehouseList = new ArrayList<ProductWarehouse>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), ProductWarehouse.class);

			// filter??????????????????
			if(filtersMap != null && filtersMap.size() > 0){
				for(Entry<String, Object> entry : filtersMap.entrySet()){
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}
			paramMap.put("templateId",templateId);
			count = this.productWarehouseManager.countHql(paramMap);
			if(count != null && count.longValue() > 0L){
				productWarehouseList = this.productWarehouseManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", productWarehouseList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	@RequestMapping("list")
	public @ResponseBody Map<String, Object> list(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> filtersMap = new HashMap<String, Object>();
		List<ProductWarehouse> productWarehouseList = new ArrayList<ProductWarehouse>();
		Long count = 0L;
		try {

			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));// ??????
			String filters = StringUtil.nullToString(request.getParameter("filters"));// ??????
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), ProductWarehouse.class);

			// ?????????@??????????????????ID???#????????????
			String keyword = StringUtil.nullToString(request.getParameter("keyword"));
			if (!StringUtil.isNullStr(keyword)) {
				// ??????
				paramMap.put("title", "%" + keyword + "%");
				paramMap.put("content", "%" + keyword + "%");
			}
			// filter??????????????????
			if (filtersMap != null && filtersMap.size() > 0) {
				for (Entry<String, Object> entry : filtersMap.entrySet()) {
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			count = this.productWarehouseManager.countHql(paramMap);
			if (count != null && count.longValue() > 0L) {
				productWarehouseList = this.productWarehouseManager.getHqlPages(paramMap, start, limit,
						sortMap.get("sort"), sortMap.get("dir"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", productWarehouseList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}

	@RequestMapping("/saveOrUpdateWarehouse")
	public @ResponseBody Map<String, Object> addWarehouse(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long templateId = StringUtil.nullToLong(request.getParameter("templateId"));
		String swarehouseId = StringUtil.nullToString(request.getParameter("warehouseId"));
		String name = StringUtil.nullToString(request.getParameter("name"));
		String swarehouseType = StringUtil.nullToString(request.getParameter("warehouseType"));
		String sproductType = StringUtil.nullToString(request.getParameter("productType"));
		Integer status = StringUtil.nullToInteger(request.getParameter("status"));

		try {
			byte[] bytes = name.getBytes("UTF-8"); 

			if ("".equals(name)) {
				resultMap.put("success", false);
				resultMap.put("message", getText("?????????????????????"));
				return resultMap;
			} else if (bytes.length > 30) {
				resultMap.put("success", false);
				resultMap.put("message", getText("??????????????????"));
				return resultMap;
			} else if ("".equals(swarehouseType)) {
				resultMap.put("success", false);
				resultMap.put("message", getText("?????????????????????"));
				return resultMap;
			} else if ("".equals(sproductType)) {
				resultMap.put("success", false);
				resultMap.put("message", getText("?????????????????????"));
				return resultMap;
			}
			
			ProductWarehouseTemplate productWarehouseTemplate = this.productWarehouseTemplateManager.get(templateId);
			if(productWarehouseTemplate == null || productWarehouseTemplate.getTemplateId() == null) {
				resultMap.put("success", false);
				resultMap.put("message", getText("???????????????????????????"));
				return resultMap;
			}
			Integer warehouseType = StringUtil.nullToInteger(swarehouseType);
			Integer productType = StringUtil.nullToInteger(sproductType);
			if (status == 1) {
				ProductWarehouse productWarehouse = new ProductWarehouse();
				productWarehouse.setName(name);
				productWarehouse.setTemplateId(templateId);
				productWarehouse.setWarehouseType(warehouseType);
				productWarehouse.setProductType(productType);
				productWarehouse.setCreateTime(new Date());
				productWarehouse.setUpdateTime(new Date());

				productWarehouseManager.save(productWarehouse);
			} else if (status == 2) {
				if ("".equals(swarehouseId)) {
					resultMap.put("success", false);
					resultMap.put("message", getText("ajax.no.record"));
					return resultMap;
				}
				Long warehouseId = StringUtil.nullToLong(swarehouseId);
				ProductWarehouse productWarehouse = productWarehouseManager.get(warehouseId);
				if (productWarehouse != null) {
					productWarehouse.setName(name);
					productWarehouse.setTemplateId(templateId);
					productWarehouse.setProductType(productType);
					productWarehouse.setWarehouseType(warehouseType);
					productWarehouse.setUpdateTime(new Date());
					productWarehouseManager.update(productWarehouse);
				} else {
					resultMap.put("success", false);
					resultMap.put("message", getText("ajax.no.record"));
					return resultMap;
				}

			}
			resultMap.put("success", true);
			resultMap.put("message", getText("submit.success"));
			return resultMap;

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		resultMap.put("success", false);
		resultMap.put("message", getText("????????????"));
		return resultMap;
	}

	@RequestMapping(value = "/delete")
	public @ResponseBody Map<String, Object> delete(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = request.getParameter("idListGridJson");
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}

			productWarehouseManager.deleteByIdList(idList);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		resultMap.put("success", true);
		resultMap.put("message", getText("submit.success"));
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
		String filePath=StringUtil.nullToString(record.get("filePath")).replace("upload/", "");
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

}

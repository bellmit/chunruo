package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.model.ProductImage;
import com.chunruo.core.Constants;
import com.chunruo.core.Constants.UserLevel;
import com.chunruo.core.model.Coupon;
import com.chunruo.core.model.MemberGift;
import com.chunruo.core.model.MemberYearsTemplate;
import com.chunruo.core.service.CouponManager;
import com.chunruo.core.service.MemberGiftManager;
import com.chunruo.core.service.MemberYearsTemplateManager;
import com.chunruo.core.util.CoreUtil;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.FileUploadUtil;
import com.chunruo.core.util.FileUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;
import com.chunruo.webapp.vo.ImageVo;

@Controller
@RequestMapping("/memberYears/")
public class MemberYearsController extends BaseController {
	@Autowired
	private CouponManager couponManager;
	@Autowired
	private MemberGiftManager memberGiftManager;
	@Autowired
	private MemberYearsTemplateManager memberYearsTemplateManager;
	
	@RequestMapping(value="/getMemberGiftListByTemplateId")
	public @ResponseBody Map<String, Object> getMemberGiftListByTemplateId(final HttpServletRequest request) {
		Long templateId = StringUtil.nullToLong(request.getParameter("templateId"));
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Map<String, Object> paramMap = new HashMap<String, Object> ();
		Map<String, Object> filtersMap = new HashMap<String, Object> ();
		List<MemberGift> memberGiftList = new ArrayList<MemberGift>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), MemberGift.class);

			// filter过滤字段查询
			if(filtersMap != null && filtersMap.size() > 0){
				for(Entry<String, Object> entry : filtersMap.entrySet()){
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}
			paramMap.put("templateId",templateId);
			paramMap.put("isDelete",false);
			count = this.memberGiftManager.countHql(paramMap);
			if(count != null && count.longValue() > 0L){
				memberGiftList = this.memberGiftManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", memberGiftList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	@RequestMapping(value = "/getMemberGiftByGiftId")
	public @ResponseBody Map<String, Object> getMemberGiftByGiftId(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long giftId = StringUtil.nullToLong(request.getParameter("giftId"));
		List<ImageVo> imageList = new ArrayList<ImageVo> ();
		List<ImageVo> detailImageList = new ArrayList<ImageVo> ();
		MemberGift memberGift = null;
		try{
			memberGift = this.memberGiftManager.get(giftId);
			if(memberGift != null && memberGift.getGiftId() != null) {
				String imagePath = memberGift.getImagePath();
				ImageVo image = new ImageVo ();
				if(!StringUtil.isNull(imagePath)){
					if(imagePath.startsWith("/")){
						imagePath = imagePath.substring(1);
					}
					image.setFileName(imagePath.substring(imagePath.lastIndexOf("/")));
					image.setFileType(FileUtil.getSuffixByFilename(imagePath));
					image.setFilePath(imagePath);
				}
				imageList.add(image);
				
				String detailImagePath = StringUtil.null2Str(memberGift.getDetailImagePath());
				if(!StringUtil.isNull(detailImagePath)) {
					List<String> strDetailImageList = StringUtil.strToStrList(detailImagePath, ";");
					if(strDetailImageList != null && strDetailImageList.size() > 0 ) {
						for(String path : strDetailImageList) {
							ImageVo imageVo = new ImageVo ();
							if(!StringUtil.isNull(path)){
								if(path.startsWith("/")){
									path = path.substring(1);
								}
								imageVo.setFileName(path.substring(path.lastIndexOf("/")));
								imageVo.setFileType(FileUtil.getSuffixByFilename(path));
								imageVo.setFilePath(path);
							}
							detailImageList.add(imageVo);
						}
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put("success", true);
		resultMap.put("data", memberGift);
		resultMap.put("imageList", imageList);
		resultMap.put("detailImageList", detailImageList);
		return resultMap;
	}
	
	@RequestMapping(value = "/saveOrUpdateMemberGift")
	public @ResponseBody Map<String, Object> saveOrUpdateStartImage(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long giftId = StringUtil.nullToLong(request.getParameter("giftId"));
		Long templateId = StringUtil.nullToLong(request.getParameter("templateId"));
		Integer type = StringUtil.nullToInteger(request.getParameter("type"));
		String name = StringUtil.null2Str(request.getParameter("name"));
		Double price = StringUtil.nullToDouble(request.getParameter("price"));
		String productCode = StringUtil.null2Str(request.getParameter("productCode"));
		String productSku = StringUtil.null2Str(request.getParameter("productSku"));
		Integer stockNumber = StringUtil.nullToInteger(request.getParameter("stockNumber"));
		Long wareHouseId = StringUtil.nullToLong(request.getParameter("wareHouseId"));
		String couponIds = StringUtil.null2Str(request.getParameter("couponIds"));
		String recodeGridJson = StringUtil.null2Str(request.getParameter("recodeGridJson"));
		String detailImagePathJson = StringUtil.null2Str(request.getParameter("detailImagePath"));
		try {
			
			List<Integer> typeList = new ArrayList<Integer>();
			typeList.add(MemberGift.MEMBER_TPYE_PRODUCT);
			typeList.add(MemberGift.MEMBER_TYPE_COUPON);
			typeList.add(MemberGift.MEMBER_TYPE_PRO_COU);
			if(!typeList.contains(type)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "赠品类型错误");
				return resultMap;
			}
			
			List<Object> saveMap = StringUtil.jsonDeserialize(recodeGridJson);	
			if(CollectionUtils.isEmpty(saveMap)){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "主图不能为空");
				return resultMap;
			}else if(StringUtil.compareObject(templateId, 0)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "会员年限模板错误");
				return resultMap;
			}else if(StringUtil.isNull(name)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "名称不能为空错误");
				return resultMap;
			}else if(price <= 0) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "价格设置错误");
				return resultMap;
			}
			
			if(StringUtil.compareObject(type, MemberGift.MEMBER_TPYE_PRODUCT)
					|| StringUtil.compareObject(type, MemberGift.MEMBER_TYPE_PRO_COU)) {
				if(StringUtil.isNull(productCode) || StringUtil.isNull(productSku)) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", "商品编码或商品sku不能为空");
					return resultMap;
				}else if(stockNumber <= 0) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", "商品库存设置错误");
					return resultMap;
				}else if(Constants.PRODUCT_WAREHOUSE_MAP == null
						|| Constants.PRODUCT_WAREHOUSE_MAP.size() <= 0
						|| !Constants.PRODUCT_WAREHOUSE_MAP.containsKey(wareHouseId)) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", "所属仓库信息错误");
					return resultMap;
				}
			}
			if(StringUtil.compareObject(type, MemberGift.MEMBER_TYPE_COUPON)
					|| StringUtil.compareObject(type, MemberGift.MEMBER_TYPE_PRO_COU)) {
				// 优惠券类型
				couponIds = couponIds.replaceAll("，", ",");
				if (StringUtil.isNull(couponIds)) {
					resultMap.put("message", "请填写优惠券id");
					resultMap.put("success", true);
					resultMap.put("error", true);
					return resultMap;
				}
				List<Long> couponIdList = StringUtil.stringToLongArray(couponIds);
				if (couponIdList == null || couponIdList.isEmpty()) {
					resultMap.put("message", "请填写优惠券id");
					resultMap.put("success", true);
					resultMap.put("error", true);
					return resultMap;
				}

				for (Long id : couponIdList) {
					Coupon coupon = this.couponManager.get(id);
					if (coupon == null || coupon.getCouponId() == null
							|| !StringUtil.nullToBoolean(coupon.getIsEnable())) {
						resultMap.put("message", String.format("\"%s\"此id所对应的优惠券未找到或未启用", id));
						resultMap.put("success", true);
						resultMap.put("error", true);
						return resultMap;
					}
				}
			}
			
			// 主图
			List<ProductImage> imageList = new ArrayList<ProductImage>();
			if (saveMap != null && saveMap.size() > 0) {
				for (int i = 0; i < saveMap.size(); i++) {
					@SuppressWarnings("unchecked")
					ProductImage productImage = createImage((Map<String, Object>)saveMap.get(i), ProductImage.IMAGE_TYPE_MATERIAL);
					if (StringUtil.isNull(productImage.getImageType())) {
						resultMap.put("success", Boolean.valueOf(false));
						resultMap.put("message", getText("image.saveAndDel.dataEexption"));
						return resultMap;
					}
					imageList.add(productImage);
				}
			}
			
			if(imageList != null && imageList.size() > 1) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "主图只能配置一张");
				return resultMap;
			}
			
			// 详情图片
			List<Object> detailSaveMap = StringUtil.jsonDeserialize(detailImagePathJson);
			List<ProductImage> detailImageList = new ArrayList<ProductImage>();
			if (detailSaveMap != null && detailSaveMap.size() > 0) {
				for (int i = 0; i < detailSaveMap.size(); i++) {
					@SuppressWarnings("unchecked")
					ProductImage productImage = createImage((Map<String, Object>)detailSaveMap.get(i), ProductImage.IMAGE_TYPE_MATERIAL);
					if (StringUtil.isNull(productImage.getImageType())) {
						resultMap.put("success", Boolean.valueOf(false));
						resultMap.put("message", getText("image.saveAndDel.dataEexption"));
						return resultMap;
					}
					detailImageList.add(productImage);
				}
			}
			
			MemberGift memberGift = this.memberGiftManager.get(giftId);
	        if(memberGift == null || memberGift.getGiftId() == null) {
	        	memberGift = new MemberGift();
	        	memberGift.setCreateTime(DateUtil.getCurrentDate());
	        }
	        
	        memberGift.setName(name);
	        memberGift.setPrice(price);
	        memberGift.setStatus(true);
	        memberGift.setIsDelete(false);
	        memberGift.setProductCode(productCode);
	        memberGift.setProductSku(productSku);
	        memberGift.setStockNumber(stockNumber);
	        memberGift.setTemplateId(templateId);
	        memberGift.setWareHouseId(wareHouseId);
	        memberGift.setCouponIds(couponIds);
	        memberGift.setType(type);
	        if(imageList != null && imageList.size() > 0) {
	        	ProductImage productImage = imageList.get(0);
	        	memberGift.setImagePath(StringUtil.null2Str(productImage.getImagePath()));
	        }
	        
	        if(detailImageList != null && detailImageList.size() > 0) {
	        	String detailImagePath = "";
	        	for(ProductImage productImage : detailImageList) {
	        		detailImagePath += StringUtil.null2Str(productImage.getImagePath());
	        		detailImagePath += ";";
	        	}
	        	memberGift.setDetailImagePath(detailImagePath);
	        }
	        memberGift.setUpdateTime(DateUtil.getCurrentDate());
	        
	        this.memberGiftManager.save(memberGift);
			
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", "保存失败");
		return resultMap;
	}
	
	/**
	 * 图片转换工具
	 * @param record imageType
	 * @return
	 */
	private ProductImage createImage(Map<String, Object> record, Integer imageType){
		
		
		ProductImage productImage = new ProductImage();
		if ((record.get("fileId") != null) && (StringUtil.isNumber(StringUtil.null2Str(record.get("fileId"))))) {
			productImage.setImageId(StringUtil.nullToLong(record.get("fileId")));
		}

		// 文件类型
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
	
	@RequestMapping(value = "/changeStatus")
	public @ResponseBody Map<String, Object> changerStatus(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = request.getParameter("idListGridJson");
		Boolean status = StringUtil.nullToBoolean(request.getParameter("status"));
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
			
			List<MemberGift> memberGiftList = this.memberGiftManager.getByIdList(idList);
			if(memberGiftList != null && memberGiftList.size() > 0) {
				for(MemberGift memberGift : memberGiftList) {
					memberGift.setStatus(status);
					memberGift.setUpdateTime(DateUtil.getCurrentDate());
				}
				this.memberGiftManager.batchInsert(memberGiftList, memberGiftList.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		resultMap.put("success", true);
		resultMap.put("message", getText("submit.success"));
		return resultMap;
	}
	
	
	
	@RequestMapping(value = "/deleteMemberGift")
	public @ResponseBody Map<String, Object> deleteMemberGift(final HttpServletRequest request) {
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
			
			List<MemberGift> memberGiftList = this.memberGiftManager.getByIdList(idList);
            if(memberGiftList != null && memberGiftList.size() > 0) {
            	for(MemberGift memberGift : memberGiftList) {
            		if(StringUtil.nullToBoolean(memberGift.getStatus())) {
            			resultMap.put("success", false);
						resultMap.put("message", String.format("请禁用\"%s\"赠品后,再删除", StringUtil.null2Str(memberGift.getName())));
						return resultMap;
            		}
            		memberGift.setStatus(false);
            		memberGift.setIsDelete(true);
                    memberGift.setUpdateTime(DateUtil.getCurrentDate());
            	}
            }
            
            this.memberGiftManager.batchInsert(memberGiftList, memberGiftList.size());
            resultMap.put("success", true);
    		resultMap.put("message", getText("删除成功"));
    		return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		resultMap.put("success", false);
		resultMap.put("message", getText("删除失败"));
		return resultMap;
	}
	
	
	@RequestMapping(value="/templateList")
	public @ResponseBody Map<String, Object> templateList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Map<String, Object> paramMap = new HashMap<String, Object> ();
		Map<String, Object> filtersMap = new HashMap<String, Object> ();
		List<MemberYearsTemplate> memberYearsTemplateList = new ArrayList<MemberYearsTemplate>();
		Long count = 0L;
		try {
			int start = StringUtil.nullToInteger(request.getParameter("start"));
			int limit = StringUtil.nullToInteger(request.getParameter("limit"));
			String sort = StringUtil.nullToString(request.getParameter("sort"));
			String filters = StringUtil.nullToString(request.getParameter("filters"));
			Map<String, String> sortMap = StringUtil.getSortMap(StringUtil.null2Str(sort));
			filtersMap = StringUtil.getFiltersMap(StringUtil.null2Str(filters), MemberYearsTemplate.class);

			// filter过滤字段查询
			if(filtersMap != null && filtersMap.size() > 0){
				for(Entry<String, Object> entry : filtersMap.entrySet()){
					paramMap.put(entry.getKey(), entry.getValue());
				}
			}

			paramMap.put("isDelete", false);
			count = this.memberYearsTemplateManager.countHql(paramMap);
			if(count != null && count.longValue() > 0L){
				memberYearsTemplateList = this.memberYearsTemplateManager.getHqlPages(paramMap, start, limit, sortMap.get("sort"), sortMap.get("dir"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("data", memberYearsTemplateList);
		resultMap.put("totalCount", count);
		resultMap.put("filters", filtersMap);
		return resultMap;
	}
	
	/**
	 * 保存会员年限模板
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveTemplate")
	public @ResponseBody Map<String, Object> saveTemplate(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long templateId = StringUtil.nullToLong(request.getParameter("templateId"));
		Double yearsNumber = StringUtil.nullToDouble(request.getParameter("yearsNumber"));
		Double price = StringUtil.nullToDoubleFormat(request.getParameter("price"));
		String yearsName = StringUtil.null2Str(request.getParameter("yearsName"));
		Double profit = StringUtil.nullToDoubleFormat(request.getParameter("profit"));
		Integer level = StringUtil.nullToInteger(request.getParameter("level"));
		Integer sort = StringUtil.nullToInteger(request.getParameter("sort"));

		try {
			
			if(yearsNumber <= 0) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("会员年限设置错误"));
				return resultMap;
			}else if(profit < 0 || price <= 0) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("邀请返利或会员价格设置错误"));
				return resultMap;
			}else if(StringUtil.isNull(yearsName)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("年限名称不能为空"));
				return resultMap;
			}
			
			List<Integer> levelList = new ArrayList<Integer>();
			levelList.add(UserLevel.USER_LEVEL_DEALER);
			levelList.add(UserLevel.USER_LEVEL_V2);
			levelList.add(UserLevel.USER_LEVEL_V3);
			if(!levelList.contains(level)) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("用户等级错误"));
				return resultMap;
			}
			
			MemberYearsTemplate memberYearsTemplate = this.memberYearsTemplateManager.get(templateId);
			if (memberYearsTemplate != null && memberYearsTemplate.getTemplateId() != null) {
				//修改
				memberYearsTemplate.setIsDelete(false);
				memberYearsTemplate.setPrice(price);
				memberYearsTemplate.setProfit(profit);
				memberYearsTemplate.setYearsName(yearsName);
				memberYearsTemplate.setSort(sort);
				memberYearsTemplate.setYearsNumber(yearsNumber);
				memberYearsTemplate.setLevel(level);
				memberYearsTemplate.setUpdateTime(DateUtil.getCurrentDate());
			}else {
				List<MemberYearsTemplate> dbTemplateList = this.memberYearsTemplateManager.getMemberYearsTemplateListByYearsNumber(yearsNumber);
				if(dbTemplateList != null && dbTemplateList.size() > 0) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", getText("该会员年限已经存在，不能重复创建"));
					return resultMap;
				}
				//新建
				memberYearsTemplate = new MemberYearsTemplate();
				memberYearsTemplate.setStatus(true);
				memberYearsTemplate.setIsDelete(false);
				memberYearsTemplate.setPrice(price);
				memberYearsTemplate.setProfit(profit);
				memberYearsTemplate.setSort(sort);
				memberYearsTemplate.setYearsName(yearsName);
				memberYearsTemplate.setYearsNumber(yearsNumber);
				memberYearsTemplate.setLevel(level);
				memberYearsTemplate.setCreateTime(DateUtil.getCurrentDate());
				memberYearsTemplate.setUpdateTime(DateUtil.getCurrentDate());
			}
			this.memberYearsTemplateManager.save(memberYearsTemplate);
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", getText("save.success"));
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", getText("错误, 保存失败"));
		return resultMap;
	}
	
	
	/**
	 * 删除会员年限模板
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteTemplate")
	public @ResponseBody Map<String, Object> deleteTemplate(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String record = StringUtil.null2Str(request.getParameter("idListGridJson"));
		List<Long> idList = null;
		try {
			idList = (List<Long>) StringUtil.getIdLongList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
			
			List<MemberYearsTemplate> memberYearsTemplateList = this.memberYearsTemplateManager.getByIdList(idList);
			if(memberYearsTemplateList != null && memberYearsTemplateList.size() > 0){
				for(MemberYearsTemplate memberYearsTemplate : memberYearsTemplateList){
					if(StringUtil.nullToBoolean(memberYearsTemplate.getStatus())){
						resultMap.put("success", false);
						resultMap.put("message", String.format("请禁用\"%s\"模板后,再删除", StringUtil.null2Str(memberYearsTemplate.getYearsName())));
						return resultMap;
					}
					
					memberYearsTemplate.setStatus(false);
					memberYearsTemplate.setIsDelete(true);
					memberYearsTemplate.setUpdateTime(DateUtil.getCurrentDate());
				}
				this.memberYearsTemplateManager.batchInsert(memberYearsTemplateList, memberYearsTemplateList.size());
			}
			
			resultMap.put("success", true);
           	resultMap.put("message", getText("save.success"));
           	return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		
		resultMap.put("success", false);
		resultMap.put("message", "错误，保存失败");
		return resultMap;
	}
	
	
	/**
	 * 设置会员年限模板状态
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
			
			List<MemberYearsTemplate> memberYearsTemplateList = this.memberYearsTemplateManager.getByIdList(idList);
			if(memberYearsTemplateList != null && memberYearsTemplateList.size() > 0){
				for(MemberYearsTemplate memberYearsTemplate : memberYearsTemplateList){
					memberYearsTemplate.setStatus(isEnabled);
					memberYearsTemplate.setIsDelete(false);
					memberYearsTemplate.setUpdateTime(DateUtil.getCurrentDate());
				}
				this.memberYearsTemplateManager.batchInsert(memberYearsTemplateList, memberYearsTemplateList.size());
			}
			
			resultMap.put("success", true);
           	resultMap.put("message", getText("save.success"));
           	return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
		
		resultMap.put("success", false);
		resultMap.put("message", "错误，保存失败");
		return resultMap;
	}
	
}

package com.chunruo.webapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.chunruo.core.Constants;
import com.chunruo.core.Constants.GoodsType;
import com.chunruo.core.model.Area;
import com.chunruo.core.model.PostageTemplate;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.model.UserInfo;
import com.chunruo.core.model.Product;
import com.chunruo.core.service.AreaManager;
import com.chunruo.core.service.PostageTemplateManager;
import com.chunruo.core.service.ProductWarehouseManager;
import com.chunruo.core.service.ProductManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.webapp.BaseController;
import com.chunruo.webapp.vo.ComboVo;
import com.chunruo.webapp.vo.TemplateRegionVo;

@Controller
@RequestMapping("/postageTpl")
public class PostageTemplateController extends BaseController {
	private Logger log = LoggerFactory.getLogger(PostageTemplateController.class);
	@Autowired
	private PostageTemplateManager postageTemplateManager;
	@Autowired
	private ProductWarehouseManager productWarehouseManager;
	@Autowired
	private AreaManager areaManager;
	@Autowired
	private ProductManager productWholesaleManager;
	
	@InitBinder("postageTemplate")  
    public void initBinderUser(WebDataBinder binder) {  
        binder.setFieldDefaultPrefix("postageTemplate.");  
    }
	
	@InitBinder("templateRegionVo")  
    public void initBinderRegion(WebDataBinder binder) {  
        binder.setFieldDefaultPrefix("templateRegionVo.");  
    }
	
	@RequestMapping(value = "/getPostageTplTree")
	public ModelAndView getPostageTplTree(final HttpServletRequest request){
		Long warehouseId = StringUtil.nullToLong(request.getParameter("warehouseId"));
		
		Long tmpWarehouseId = 0L;
		Map<ProductWarehouse, List<PostageTemplate>> wareHousePostageTplMap = new HashMap<ProductWarehouse, List<PostageTemplate>> ();
		try{
			// ???????????????????????????ID??????
			Map<Long, List<PostageTemplate>> postageTemplateMap = new HashMap<Long, List<PostageTemplate>> ();
			List<PostageTemplate> postageTplList = this.postageTemplateManager.getAll();
			if(postageTplList != null && postageTplList.size() > 0){
				for(PostageTemplate postageTpl : postageTplList){
					if(StringUtil.nullToBoolean(postageTpl.getIsFreeTemplate())){
						if(postageTemplateMap.containsKey(tmpWarehouseId)){
							postageTemplateMap.get(tmpWarehouseId).add(postageTpl);
						}else{
							List<PostageTemplate> list = new ArrayList<PostageTemplate> ();
							list.add(postageTpl);
							postageTemplateMap.put(tmpWarehouseId, list);
						}
					}else{
						if(postageTemplateMap.containsKey(postageTpl.getWarehouseId())){
							postageTemplateMap.get(postageTpl.getWarehouseId()).add(postageTpl);
						}else{
							List<PostageTemplate> list = new ArrayList<PostageTemplate> ();
							list.add(postageTpl);
							postageTemplateMap.put(postageTpl.getWarehouseId(), list);
						}
					}
				}
			}
			
			// ?????????????????????
			ProductWarehouse preePostageWarehouse = new ProductWarehouse ();
			preePostageWarehouse.setWarehouseId(tmpWarehouseId);
			preePostageWarehouse.setName("???????????????");
			preePostageWarehouse.setExpanded(StringUtil.compareObject(warehouseId, preePostageWarehouse.getWarehouseId()));
			wareHousePostageTplMap.put(preePostageWarehouse, postageTemplateMap.get(tmpWarehouseId));
						
			// ???????????????????????????
			List<ProductWarehouse> productWarehouseList = this.productWarehouseManager.getAll();
			if(productWarehouseList != null && productWarehouseList.size() > 0){
				for(ProductWarehouse productWarehouse : productWarehouseList){
					productWarehouse.setExpanded(StringUtil.compareObject(warehouseId, productWarehouse.getWarehouseId()));
					wareHousePostageTplMap.put(productWarehouse, postageTemplateMap.get(productWarehouse.getWarehouseId()));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Model model = new ExtendedModelMap();
		model.addAttribute(Constants.POSTAGE_TPL_MAPS, wareHousePostageTplMap);
		return new ModelAndView("nodes/postageTpls", model.asMap());
	}
	
	/**
	 * ??????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/createPostageTpl")
	public @ResponseBody Map<String, Object> createPostageTpl(@ModelAttribute("postageTemplate")PostageTemplate postageTemplate, final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			// ????????????????????????????????????
			if (StringUtil.isNull(postageTemplate.getName())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "??????,??????????????????????????????");
				return resultMap;
			}else if (!StringUtil.isNumber(postageTemplate.getFirstWeigth())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "????????????????????????");
				return resultMap;
			} else if (!StringUtil.isNumber(postageTemplate.getFirstPrice())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "??????????????????????????????");
				return resultMap;
			} else if (!StringUtil.isNumber(postageTemplate.getAfterWeigth())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "???????????????????????????");
				return resultMap;
			} else if (!StringUtil.isNumber(postageTemplate.getAfterPrice())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "??????????????????????????????");
				return resultMap;
			}
			
			// ?????????????????????????????????
			postageTemplate.setIsFreeTemplate(false);
			if(StringUtil.compareObject(StringUtil.nullToInteger(postageTemplate.getWarehouseId()), 0)){
				if(!StringUtil.isNumber(postageTemplate.getFreePostageAmount())){
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", "?????????????????????????????????");
					return resultMap;
				}
				
				// ???????????????????????????????????????
				List<PostageTemplate> tplList = this.postageTemplateManager.getTemplateListByIsFreeTemplate(true);
				if(tplList != null && tplList.size() > 0){
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", "??????,???????????????????????????????????????");
					return resultMap;
				}
				
				// ???????????????
				postageTemplate.setIsFreeTemplate(true);
			}else {
				ProductWarehouse productWarehouse = this.productWarehouseManager.get(StringUtil.nullToLong(postageTemplate.getWarehouseId()));
				if(productWarehouse == null || productWarehouse.getWarehouseId() == null) {
					resultMap.put("success", false);
					resultMap.put("message", "??????,???????????????");
					return resultMap;
				}
				
				List<Integer> productTypeList = new ArrayList<Integer>();
				productTypeList.add(GoodsType.GOODS_TYPE_DIRECT);
				productTypeList.add(GoodsType.GOODS_TYPE_DIRECT_GO);
				if (productTypeList.contains(StringUtil.nullToInteger(productWarehouse.getProductType()))
						&& !StringUtil.isNumber(postageTemplate.getPackageWeigth())) {
					resultMap.put("error", true);
					resultMap.put("success", true);
					resultMap.put("message", "????????????????????????");
					return resultMap;
				}
			}
			
			// ??????????????????
			List<Area> areaList = this.areaManager.getAreaListByParentId(0L);
			if(areaList != null && areaList.size() > 0){
				StringBuffer postageTplBuffer = new StringBuffer();
				boolean isFirst = true;
				for(Area area : areaList){
					if(!isFirst){
						postageTplBuffer.append("&");
					}
					postageTplBuffer.append(area.getAreaId());
					isFirst = false;
				}
				
				// ????????????????????????
				postageTplBuffer.append("," + StringUtil.nullToInteger(postageTemplate.getFirstWeigth()));
				postageTplBuffer.append("," + StringUtil.nullToInteger(postageTemplate.getFirstPrice()));
				postageTplBuffer.append("," + StringUtil.nullToInteger(postageTemplate.getAfterWeigth()));
				postageTplBuffer.append("," + StringUtil.nullToInteger(postageTemplate.getAfterPrice()));
				postageTplBuffer.append("," + StringUtil.nullToInteger(postageTemplate.getPackageWeigth()));

				postageTemplate.setTplArea(postageTplBuffer.toString());
			}
			
			postageTemplate.setCreateTime(DateUtil.getCurrentDate());
			postageTemplate.setUpdateTime(postageTemplate.getCreateTime());
			postageTemplate = this.postageTemplateManager.save(postageTemplate);
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("warehouseId", postageTemplate.getWarehouseId());
			resultMap.put("message", "????????????");
			return resultMap;
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", "??????,????????????");
		return resultMap;
	}

	/**
	 * ??????????????????
	 * ????????????????????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/editPostageTemplate")
	public @ResponseBody Map<String, Object> editPostageTemplate(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long templateId = StringUtil.nullToLong(request.getParameter("templateId"));
		String name = request.getParameter("name");
		Double freePostageAmount=StringUtil.nullToDouble(request.getParameter("freePostageAmount"));

		try {
			PostageTemplate postageTemplate = this.postageTemplateManager.get(templateId);
			if (postageTemplate == null || postageTemplate.getTemplateId() == null) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", getText("??????,???????????????????????????????????????"));
				return resultMap;
			}else if(!StringUtil.isNumber(freePostageAmount) || freePostageAmount.compareTo(0.0) < 0 ){
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "?????????????????????????????????");
				return resultMap;
			}
			
			List<PostageTemplate> postageTemplateList = new ArrayList<PostageTemplate> ();
			
			if(postageTemplate.getIsFreeTemplate()) {
			postageTemplate.setFreePostageAmount(freePostageAmount);
			}
			postageTemplate.setName(name);
			postageTemplate.setUpdateTime(DateUtil.getCurrentDate());
			postageTemplateList.add(postageTemplate);
			this.postageTemplateManager.batchInsert(postageTemplateList, postageTemplateList.size());
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", "????????????");
			resultMap.put("warehouseId", postageTemplate.getWarehouseId());
			return resultMap;
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", "??????,????????????");
		return resultMap;
	}

	/**
	 * ??????????????????
	 * ????????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deletePostageTemplate")
	public @ResponseBody Map<String, Object> deletePostageTemplate(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long templateId = StringUtil.nullToLong(request.getParameter("templateId"));
		try {
			PostageTemplate postageTpl = this.postageTemplateManager.get(templateId);
			if(postageTpl == null || postageTpl.getTemplateId() == null){
				resultMap.put("success", false);
				resultMap.put("message", "??????,???????????????????????????");
				return resultMap;
			}
			
			// ????????????????????????????????????
			Map<String, Object> paramMap = new HashMap<String, Object> ();
			paramMap.put("templateId", postageTpl.getTemplateId());
			List<Product> wholesaleList = this.productWholesaleManager.getHqlPages(paramMap);
			if(wholesaleList != null && wholesaleList.size() > 0){
				resultMap.put("success", false);
				resultMap.put("message", "??????,????????????????????????????????????????????????");
				return resultMap;
			}
			
			this.postageTemplateManager.remove(postageTpl.getTemplateId());
			
			resultMap.put("success", true);
			resultMap.put("message", getText("????????????"));
			resultMap.put("warehouseId", postageTpl.getWarehouseId());
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultMap.put("success", false);
		resultMap.put("message", getText("????????????"));
		return resultMap;
	}

	/**
	 * ????????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getRegionTplAreas")
	public @ResponseBody Map<String, Object> getRegionTplAreas(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Integer areaId = StringUtil.nullToInteger(request.getParameter("areaId"));
		Long templateId = StringUtil.nullToLong(request.getParameter("templateId"));
		
		List<Area> allAreaList = new ArrayList<Area> ();
		List<Long> selectAreaIdList = new ArrayList<Long> ();
		TemplateRegionVo templateRegion = new TemplateRegionVo ();
		try {
			boolean isNewCreater = false;
			PostageTemplate postageTpl = this.postageTemplateManager.get(templateId);
			if(postageTpl == null || postageTpl.getTemplateId() == null){
				resultMap.put("success", false);
				resultMap.put("message", "??????,?????????????????????");
				return resultMap;
			}
			
			// ????????????
			templateRegion.setTemplateName(postageTpl.getName());
			
			// ???????????????????????????
			Map<Integer, TemplateRegionVo> templateRegionMap = this.getTemplateRegionByTplArea(postageTpl.getTplArea(), postageTpl.getName());
			if(templateRegionMap == null 
					|| templateRegionMap.size() <= 0
					|| !templateRegionMap.containsKey(areaId)){
				isNewCreater = true;
			}
			
			// ????????????????????????
			Map<Long, Area> areaIdMap = new HashMap<Long, Area> ();
			List<Area> areaList = this.areaManager.getAreaListByParentId(0L);
			if(areaList != null && areaList.size() > 0){
				for(Area area : areaList){
					areaIdMap.put(StringUtil.nullToLong(area.getAreaId()), area);
				}
			}
			
			if(isNewCreater){
				// ????????????????????????
				if(templateRegionMap != null && templateRegionMap.size() > 0){
					for(Map.Entry<Integer, TemplateRegionVo> entry : templateRegionMap.entrySet()){
						if(entry.getValue() != null
								&& entry.getValue().getAreaIdList() != null
								&& entry.getValue().getAreaIdList().size() > 0){
							for(Long tmpAreaId : entry.getValue().getAreaIdList()){
								if(!StringUtil.compareObject(StringUtil.nullToInteger(entry.getKey()), 1)) {
									areaIdMap.remove(tmpAreaId);
								}
							}
						}
					}
				}
			}else{
				// ????????????????????????
				if(!StringUtil.compareObject(areaId, 1)) {
					templateRegionMap.remove(1);
				}
				templateRegion = templateRegionMap.remove(areaId);
				if(templateRegion != null 
						&& templateRegion.getAreaIdList() != null
						&& templateRegion.getAreaIdList().size() > 0){
					selectAreaIdList.addAll(templateRegion.getAreaIdList());
				}
				
				// ?????????????????????????????????
				if(templateRegionMap != null && templateRegionMap.size() > 0){
					for(Map.Entry<Integer, TemplateRegionVo> entry : templateRegionMap.entrySet()){
						if(entry.getValue() != null
								&& entry.getValue().getAreaIdList() != null
								&& entry.getValue().getAreaIdList().size() > 0){
							for(Long tmpAreaId : entry.getValue().getAreaIdList()){
								areaIdMap.remove(tmpAreaId);
							}
						}
					}
				}
			}
			
			// ??????????????????????????????
			if(areaIdMap != null && areaIdMap.size() > 0){
				List<Map.Entry<Long, Area>> mappingList = new ArrayList<Map.Entry<Long, Area>> (areaIdMap.entrySet());
				Collections.sort(mappingList, new Comparator<Map.Entry<Long, Area>>(){
					public int compare(Map.Entry<Long, Area> obj1, Map.Entry<Long, Area> obj2){
						return StringUtil.nullToInteger(obj1.getKey()) > StringUtil.nullToInteger(obj2.getKey()) ? 1 : -1;
					}
				});
				
				for(Map.Entry<Long, Area> entry : mappingList){
					allAreaList.add(entry.getValue());
				}
			}
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
	
		resultMap.put("success", true);
        resultMap.put("data", templateRegion);
        resultMap.put("allAreaList", allAreaList);
        resultMap.put("selectAreaIdList", selectAreaIdList);
        return resultMap;
	}
	
	/**
	 * ????????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveRegionTplArea")
	public @ResponseBody Map<String, Object> saveRegionTplArea(@ModelAttribute("templateRegionVo")TemplateRegionVo templateRegionVo, final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			// ???????????????
			if (!StringUtil.isNumber(templateRegionVo.getFirstWeigth())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "????????????????????????");
				return resultMap;
			} else if (!StringUtil.isNumber(templateRegionVo.getFirstPrice())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "??????????????????????????????");
				return resultMap;
			} else if (!StringUtil.isNumber(templateRegionVo.getAfterWeigth())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "???????????????????????????");
				return resultMap;
			} else if (!StringUtil.isNumber(templateRegionVo.getAfterPrice())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "??????????????????????????????");
				return resultMap;
			}
			
			PostageTemplate postageTpl = this.postageTemplateManager.get(StringUtil.nullToLong(templateRegionVo.getTemplateId()));
			if(postageTpl == null || postageTpl.getTemplateId() == null){
				resultMap.put("success", false);
				resultMap.put("message", "??????,?????????????????????");
				return resultMap;
			}
			
			ProductWarehouse productWarehouse = this.productWarehouseManager.get(StringUtil.nullToLong(postageTpl.getWarehouseId()));
			if(!StringUtil.nullToBoolean(postageTpl.getIsFreeTemplate()) && (productWarehouse == null || productWarehouse.getWarehouseId() == null)) {
				resultMap.put("success", false);
				resultMap.put("message", "??????,???????????????");
				return resultMap;
			}
			
			List<Integer> productTypeList = new ArrayList<Integer>();
			productTypeList.add(GoodsType.GOODS_TYPE_DIRECT);
			productTypeList.add(GoodsType.GOODS_TYPE_DIRECT_GO);
			if ((StringUtil.nullToBoolean(postageTpl.getIsFreeTemplate()) || 
					productTypeList.contains(StringUtil.nullToInteger(productWarehouse.getProductType())))
					&& !StringUtil.isNumber(templateRegionVo.getPackageWeigth())) {
				resultMap.put("error", true);
				resultMap.put("success", true);
				resultMap.put("message", "????????????????????????");
				return resultMap;
			}
			
			// ???????????????????????????
			Map<Integer, TemplateRegionVo> templateRegionMap = this.getTemplateRegionByTplArea(postageTpl.getTplArea(), postageTpl.getName());
			
			if(templateRegionMap == null || templateRegionMap.size() <= 0) {
				resultMap.put("success", false);
				resultMap.put("message", "??????,?????????????????????");
				return resultMap;
			}
			
			
			List<Long> areaIdList = new ArrayList<Long>();
			TemplateRegionVo  defalultRegionVo = null;  //????????????
			if(templateRegionMap != null && templateRegionMap.size() > 0) {
				//??????????????????
				defalultRegionVo = templateRegionMap.get(1);
				if(defalultRegionVo == null ) {
					resultMap.put("success", false);
					resultMap.put("message", "??????,?????????????????????");
					return resultMap;
				}
				
				for(Long tmpAreaId : defalultRegionVo.getAreaIdList()) {
					areaIdList.add(tmpAreaId);
				}
				
				//??????id
				TemplateRegionVo currentRegionVo = templateRegionMap.get(StringUtil.nullToInteger(templateRegionVo.getAreaId()));
		        if(currentRegionVo != null) {
		        	for(Long tmpAreaId : currentRegionVo.getAreaIdList()) {
		        		areaIdList.add(tmpAreaId);
		        	}
		        }
			}
			
			// ????????????????????????
			Map<Long, Area> areaIdMap = new HashMap<Long, Area> ();
			List<Area> areaList = new ArrayList<Area>();
			if(areaIdList != null && areaIdList.size() > 0) {
				areaList = this.areaManager.getByIdList(areaIdList);
			}else {
				//?????????????????????
				areaList = this.areaManager.getAreaListByParentId(0L);
			}
			
			if(areaList != null && areaList.size() > 0){
				for(Area area : areaList){
					areaIdMap.put(StringUtil.nullToLong(area.getAreaId()), area);
				}
			}
			
			//????????????????????????????????????
			List<Long> newAreaIdList = StringUtil.stringToLongArray(StringUtil.null2Str(templateRegionVo.getAreaIds()));
			List<Long> defaultAreaIdList = new ArrayList<Long>();
			//????????????????????????????????????
			areaIdList.addAll(newAreaIdList);
			if(newAreaIdList != null && newAreaIdList.size() > 0){
				for(Long areaId : newAreaIdList){
					if(!areaIdList.contains(areaId)){
						resultMap.put("error", true);
						resultMap.put("success", true);
						resultMap.put("message", String.format("'%s'??????????????????????????????", areaIdMap.get(areaId)));
						return resultMap;
					}
				}
				templateRegionVo.setAreaIdList(newAreaIdList);
				
				for(Long areaId : areaIdList) {
					if(!newAreaIdList.contains(areaId)) {
						defaultAreaIdList.add(areaId);
					}
				}
				defalultRegionVo.setAreaIdList(defaultAreaIdList);
			}
			
			
			// ??????Map????????????????????????????????????
			int tmpAreaId = StringUtil.nullToInteger(templateRegionVo.getAreaId());
			if(StringUtil.compareObject(tmpAreaId, 0)) {
				tmpAreaId = Integer.MAX_VALUE;    //?????????????????????
			}
			templateRegionMap.put(1,defalultRegionVo);                                       //??????????????????
			templateRegionMap.put(StringUtil.nullToInteger(tmpAreaId), templateRegionVo);    //??????????????????/????????????
			List<Map.Entry<Integer, TemplateRegionVo>> mappingList = new ArrayList<Map.Entry<Integer, TemplateRegionVo>> (templateRegionMap.entrySet());
			Collections.sort(mappingList, new Comparator<Map.Entry<Integer, TemplateRegionVo>>(){
				public int compare(Map.Entry<Integer, TemplateRegionVo> obj1, Map.Entry<Integer, TemplateRegionVo> obj2){
					return StringUtil.nullToInteger(obj1.getKey()) > StringUtil.nullToInteger(obj2.getKey()) ? 1 : -1;
				}
			});
			
			boolean isFirstTplArea = true;
			StringBuffer tplAreaBuffer = new StringBuffer ();
			for(Map.Entry<Integer, TemplateRegionVo> entry : mappingList){
				TemplateRegionVo postageTemplate = entry.getValue();
				if(postageTemplate.getAreaIdList() == null || postageTemplate.getAreaIdList().size() <= 0) {
					continue;
				}
				// ????????????
				if(!isFirstTplArea){
					tplAreaBuffer.append(";");
				}
				
				
				StringBuffer postageTplBuffer = new StringBuffer();
				boolean isFirst = true;
				for(Long areaId : postageTemplate.getAreaIdList()){
					if(!isFirst){
						postageTplBuffer.append("&");
					}
					postageTplBuffer.append(areaId);
					isFirst = false;
				}
				
				// ????????????????????????
				postageTplBuffer.append("," + StringUtil.nullToDouble(postageTemplate.getFirstWeigth()));
				postageTplBuffer.append("," + StringUtil.nullToDouble(postageTemplate.getFirstPrice()));
				postageTplBuffer.append("," + StringUtil.nullToDouble(postageTemplate.getAfterWeigth()));
				postageTplBuffer.append("," + StringUtil.nullToDouble(postageTemplate.getAfterPrice()));
				postageTplBuffer.append("," + StringUtil.nullToDouble(postageTemplate.getPackageWeigth()));
				tplAreaBuffer.append(postageTplBuffer.toString());
				isFirstTplArea = false;
			}
			
			postageTpl.setTplArea(tplAreaBuffer.toString());
			postageTpl.setUpdateTime(DateUtil.getCurrentDate());
			this.postageTemplateManager.save(postageTpl);
			
			resultMap.put("error", false);
			resultMap.put("success", true);
			resultMap.put("message", "????????????");
			return resultMap;
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		resultMap.put("error", true);
		resultMap.put("success", true);
		resultMap.put("message", "??????,????????????");
		return resultMap;
	}
	
	/**
	 * ?????????????????????????????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getRegionListByTemplateId")
	public @ResponseBody Map<String, Object> getRegionListByTemplateId(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long templateId = StringUtil.nullToLong(request.getParameter("templateId"));
		List<TemplateRegionVo> pageList = new ArrayList<TemplateRegionVo>();
		try {
			PostageTemplate postageTemplate = this.postageTemplateManager.get(templateId);
			ProductWarehouse productWarehouse = this.productWarehouseManager.get(StringUtil.nullToLong(postageTemplate.getWarehouseId()));
			if(productWarehouse != null && productWarehouse.getWarehouseId() != null) {
				Map<Integer, TemplateRegionVo> templateRegionMap = this.getTemplateRegionByTplArea(postageTemplate.getTplArea(), postageTemplate.getName());
				if(templateRegionMap != null && templateRegionMap.size() > 0){
					List<Map.Entry<Integer, TemplateRegionVo>> mappingList = new ArrayList<Map.Entry<Integer, TemplateRegionVo>> (templateRegionMap.entrySet());
					Collections.sort(mappingList, new Comparator<Map.Entry<Integer, TemplateRegionVo>>(){
						public int compare(Map.Entry<Integer, TemplateRegionVo> obj1, Map.Entry<Integer, TemplateRegionVo> obj2){
							return StringUtil.nullToInteger(obj1.getKey()) > StringUtil.nullToInteger(obj2.getKey()) ? 1 : -1;
						}
					});
					
					for(Map.Entry<Integer, TemplateRegionVo> entry : mappingList){
						TemplateRegionVo templateRegionVo = entry.getValue();
						templateRegionVo.setProductType(StringUtil.nullToInteger(productWarehouse.getProductType()));
						pageList.add(templateRegionVo);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultMap.put("data", pageList);
		resultMap.put("totalCount", StringUtil.nullToInteger(pageList.size()));
		return resultMap;
	} 
	
	/**
	 * ??????????????????
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/deletePostageRegion")
	public @ResponseBody Map<String, Object> deletePostageRegion(final HttpServletRequest request) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long templateId = StringUtil.nullToLong(request.getParameter("templateId"));
		String record = request.getParameter("idListGridJson");
		
		List<Integer> idList = null;
		try {
			idList = (List<Integer>) StringUtil.getIdIntegerList(record);
			if (idList == null || idList.size() == 0) {
				resultMap.put("success", false);
				resultMap.put("message", getText("ajax.no.record"));
				return resultMap;
			}
			
			PostageTemplate postageTpl = this.postageTemplateManager.get(templateId);
			if(postageTpl == null || postageTpl.getTemplateId() == null){
				resultMap.put("success", false);
				resultMap.put("message", "??????,?????????????????????");
				return resultMap;
			}
			
			// ???????????????????????????
			Map<Integer, TemplateRegionVo> templateRegionMap = this.getTemplateRegionByTplArea(postageTpl.getTplArea(), postageTpl.getName());
			if(templateRegionMap == null || templateRegionMap.size() <= 0){
				resultMap.put("success", false);
				resultMap.put("message", "???????????????????????????");
				return resultMap;
			}
			
			TemplateRegionVo defaultRegionVo = templateRegionMap.get(1);
			if(defaultRegionVo == null) {
				resultMap.put("success", false);
				resultMap.put("message", "???????????????????????????");
				return resultMap;
			}
			for(Integer areaId : idList){
				if(templateRegionMap == null || !templateRegionMap.containsKey(areaId)){
					resultMap.put("success", false);
					resultMap.put("message", "???????????????????????????");
					return resultMap;
				}else if(StringUtil.compareObject(areaId, 1)) {
					resultMap.put("success", false);
					resultMap.put("message", "??????????????????????????????");
					return resultMap;
				}
				TemplateRegionVo templateRegionVo = templateRegionMap.remove(areaId);
				if(templateRegionVo != null ) {
					defaultRegionVo.getAreaIdList().addAll(templateRegionVo.getAreaIdList());   //????????????????????????????????????
				}
			}
			//????????????
			Collections.sort(defaultRegionVo.getAreaIdList(), new Comparator<Long>(){
					@Override
					public int compare(Long o1, Long o2) {
						return o1 > o2 ? 1 : -1;
					}
			});
			
			templateRegionMap.put(1, defaultRegionVo);
			String tplArea = "";
			if(templateRegionMap != null && templateRegionMap.size() > 0){
				List<Map.Entry<Integer, TemplateRegionVo>> mappingList = new ArrayList<Map.Entry<Integer, TemplateRegionVo>> (templateRegionMap.entrySet());
				Collections.sort(mappingList, new Comparator<Map.Entry<Integer, TemplateRegionVo>>(){
					public int compare(Map.Entry<Integer, TemplateRegionVo> obj1, Map.Entry<Integer, TemplateRegionVo> obj2){
						return StringUtil.nullToInteger(obj1.getKey()) > StringUtil.nullToInteger(obj2.getKey()) ? 1 : -1;
					}
				});
				
				StringBuffer tplAreaBuffer = new StringBuffer ();
				for(Map.Entry<Integer, TemplateRegionVo> entry : mappingList){
					TemplateRegionVo templateRegionVo = entry.getValue();
					if(templateRegionVo.getAreaIdList() != null && templateRegionVo.getAreaIdList().size() > 0){
						StringBuffer areaIdBuffer = new StringBuffer();
						boolean isFirst = true;
						for(Long areaId : templateRegionVo.getAreaIdList()){
							if(!isFirst){
								areaIdBuffer.append("&");
							}
							areaIdBuffer.append(areaId);
							isFirst = false;
						}
						
						// ????????????????????????
						areaIdBuffer.append("," + StringUtil.nullToInteger(templateRegionVo.getFirstWeigth()));
						areaIdBuffer.append("," + StringUtil.nullToInteger(templateRegionVo.getFirstPrice()));
						areaIdBuffer.append("," + StringUtil.nullToInteger(templateRegionVo.getAfterWeigth()));
						areaIdBuffer.append("," + StringUtil.nullToInteger(templateRegionVo.getAfterPrice()));
						areaIdBuffer.append("," + StringUtil.nullToInteger(templateRegionVo.getPackageWeigth()) + ";");
						tplAreaBuffer.append(areaIdBuffer.toString());
					}
				}
				tplArea = tplAreaBuffer.toString().substring(0, tplAreaBuffer.toString().length() - 1);
			}
			
			postageTpl.setTplArea(tplArea);
			postageTpl.setUpdateTime(DateUtil.getCurrentDate());
			this.postageTemplateManager.update(postageTpl);
			
			resultMap.put("success", true);
			resultMap.put("message", "????????????");
			return resultMap;
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		resultMap.put("success", false);
		resultMap.put("message", "????????????");
		return resultMap;
	}
	
	/**
	 * ????????????????????????????????????
	 * @param tplArea
	 * @param postageName
	 * @return
	 */
	private Map<Integer, TemplateRegionVo> getTemplateRegionByTplArea(String tplArea, String postageName){
		Map<Integer, TemplateRegionVo> templateRegionMap = new LinkedHashMap<Integer, TemplateRegionVo> ();
		try{
			List<String> strTemplateRegionList = StringUtil.strToStrList(tplArea, ";"); // ?????????????????????????????????
			if (strTemplateRegionList != null && strTemplateRegionList.size() > 0) {
				Map<Long, String> areaIdMap = new HashMap<Long, String> ();
				List<Area> areaList = this.areaManager.getAreaListByParentId(0L);
				if(areaList != null && areaList.size() > 0){
					for(Area area : areaList){
						areaIdMap.put(StringUtil.nullToLong(area.getAreaId()), StringUtil.null2Str(area.getShortName()));
					}
				}
				
				int index = 0;
				for (String strTemplateRegion : strTemplateRegionList) {
					++index;
					TemplateRegionVo templateRegionVo = TemplateRegionVo.getIntance(strTemplateRegion, postageName);
					if(areaIdMap != null
							&& areaIdMap.size() > 0
							&& templateRegionVo != null 
							&& templateRegionVo.getAreaIdList() != null
							&& templateRegionVo.getAreaIdList().size() > 0){
						boolean isFirst = true;
						StringBuffer areaNameBuffer = new StringBuffer();
						for(Long areaId : templateRegionVo.getAreaIdList()){
							if(areaIdMap.containsKey(areaId)){
								if(!isFirst){
									areaNameBuffer.append(",");
								}
								
								areaNameBuffer.append(areaIdMap.get(areaId));
								isFirst = false;
							}
						}
						templateRegionVo.setAreaId(index);
						templateRegionVo.setArea(areaNameBuffer.toString());
					}
					templateRegionMap.put(index, templateRegionVo);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return templateRegionMap;
	}
	
	/**
	 * ????????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getRegionTplArea")
	public @ResponseBody Map<String, Object> getRegionTplArea(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Integer areaId = StringUtil.nullToInteger(request.getParameter("areaId"));
		Long templateId = StringUtil.nullToLong(request.getParameter("templateId"));
		
		List<Area> allAreaList = new ArrayList<Area> ();
		List<Long> selectAreaIdList = new ArrayList<Long> ();
		TemplateRegionVo templateRegion = new TemplateRegionVo ();
		try {
			PostageTemplate postageTpl = this.postageTemplateManager.get(templateId);
			if(postageTpl == null || postageTpl.getTemplateId() == null){
				resultMap.put("success", false);
				resultMap.put("message", "??????,?????????????????????");
				return resultMap;
			}
			
			// ????????????
			templateRegion.setTemplateName(postageTpl.getName());
			
			// ???????????????????????????
			Map<Integer, TemplateRegionVo> templateRegionMap = this.getTemplateRegionByTplArea(postageTpl.getTplArea(), postageTpl.getName());
			List<Long> areaIdList = new ArrayList<Long>();
			if(templateRegionMap != null && templateRegionMap.size() > 0) {
				//??????????????????
				TemplateRegionVo  defalultRegionVo = templateRegionMap.get(1);
				if(defalultRegionVo != null) {
					for(Long tmpAreaId : defalultRegionVo.getAreaIdList()) {
						areaIdList.add(tmpAreaId);
					}
				}
				
				//????????????????????????
				if(templateRegionMap.containsKey(areaId)) {
					templateRegion = templateRegionMap.get(areaId);
					if(templateRegion != null 
							&& templateRegion.getAreaIdList() != null
							&& templateRegion.getAreaIdList().size() > 0){
						selectAreaIdList.addAll(templateRegion.getAreaIdList());
						areaIdList.addAll(selectAreaIdList);
					}
				}
			}
			
			// ????????????????????????
			Map<Long, Area> areaIdMap = new HashMap<Long, Area> ();
			List<Area> areaList = new ArrayList<Area>();
			if(areaIdList != null && areaIdList.size() > 0 && !(StringUtil.nullToBoolean(postageTpl.getIsFreeTemplate())
					&& StringUtil.compareObject(areaId, 1))) {
				areaList = this.areaManager.getByIdList(areaIdList);
			}else {
				//?????????????????????
				areaList = this.areaManager.getAreaListByParentId(0L);
			}
			
			if(areaList != null && areaList.size() > 0){
				for(Area area : areaList){
					areaIdMap.put(StringUtil.nullToLong(area.getAreaId()), area);
				}
			}
			
			// ??????????????????????????????
			if(areaIdMap != null && areaIdMap.size() > 0){
				List<Map.Entry<Long, Area>> mappingList = new ArrayList<Map.Entry<Long, Area>> (areaIdMap.entrySet());
				Collections.sort(mappingList, new Comparator<Map.Entry<Long, Area>>(){
					public int compare(Map.Entry<Long, Area> obj1, Map.Entry<Long, Area> obj2){
						return StringUtil.nullToInteger(obj1.getKey()) > StringUtil.nullToInteger(obj2.getKey()) ? 1 : -1;
					}
				});
				
				for(Map.Entry<Long, Area> entry : mappingList){
					allAreaList.add(entry.getValue());
				}
			}
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
	
		resultMap.put("success", true);
        resultMap.put("data", templateRegion);
        resultMap.put("allAreaList", allAreaList);
        resultMap.put("selectAreaIdList", selectAreaIdList);
        return resultMap;
	}
	
	
	/**
	 * ????????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getPostageTemplate")
	public @ResponseBody Map<String, Object> getPostageTemplate(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		List<ComboVo>  comboList = new ArrayList<ComboVo>(); 
		try {
			List<PostageTemplate> postageTemplateList = this.postageTemplateManager.getAll();
			if (postageTemplateList != null && postageTemplateList.size() > 0){
				for (PostageTemplate postageTemplate : postageTemplateList){
					ComboVo comboVo = new ComboVo();
					comboVo.setId(postageTemplate.getTemplateId());
					comboVo.setName(postageTemplate.getName());
					comboList.add(comboVo);
				}
			}
			resultMap.put("data", comboList);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		return resultMap;
	}
}

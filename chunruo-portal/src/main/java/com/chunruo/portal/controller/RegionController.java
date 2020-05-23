package com.chunruo.portal.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.chunruo.core.Constants;
import com.chunruo.core.model.Area;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.BaseController;
import com.chunruo.portal.PortalConstants;

/**
 * 地区列表
 * @author chunruo
 *
 */
@Controller
@RequestMapping("/wap/")
public class RegionController extends BaseController {

	/**
	 * 省、市、区列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getRegionList")
	public @ResponseBody Map<String, Object> getRegionList(final HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object> ();
		Integer regionType = StringUtil.nullToInteger(request.getParameter("type"));
		Long regionId = StringUtil.nullToLong(request.getParameter("id"));
		
		List<Map<String, String>> regionList = new ArrayList<Map<String, String>> ();
		try{
			if(StringUtil.compareObject(regionType, 1)){
				// 省
				if(Constants.PROVINCE_AREA_LIST != null && Constants.PROVINCE_AREA_LIST.size() > 0){
					for(Area area : Constants.PROVINCE_AREA_LIST){
						Map<String, String> regionMap = new HashMap<String, String> ();
						regionMap.put("areaId", StringUtil.null2Str(area.getAreaId()));
						regionMap.put("areaName", StringUtil.null2Str(area.getAreaName()));
						regionList.add(regionMap);
					}
				}
			}else if(StringUtil.compareObject(regionType, 2)){
				// 市
				if(Constants.CITY_ARE_AMAP != null 
						&& Constants.CITY_ARE_AMAP.size() > 0
						&& Constants.CITY_ARE_AMAP.containsKey(regionId)){
					List<Area> areaList = Constants.CITY_ARE_AMAP.get(regionId);
					for(Area area : areaList){
						Map<String, String> regionMap = new HashMap<String, String> ();
						regionMap.put("areaId", StringUtil.null2Str(area.getAreaId()));
						regionMap.put("areaName", StringUtil.null2Str(area.getAreaName()));
						regionList.add(regionMap);
					}
				}
			}else if(StringUtil.compareObject(regionType, 3)){
				// 区
				if(Constants.COUNTRY_AREA_MAP != null 
						&& Constants.COUNTRY_AREA_MAP.size() > 0
						&& Constants.COUNTRY_AREA_MAP.containsKey(regionId)){
					List<Area> areaList = Constants.COUNTRY_AREA_MAP.get(regionId);
					for(Area area : areaList){
						Map<String, String> regionMap = new HashMap<String, String> ();
						regionMap.put("areaId", StringUtil.null2Str(area.getAreaId()));
						regionMap.put("areaName", StringUtil.null2Str(area.getAreaName()));
						regionList.add(regionMap);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		resultMap.put("list", regionList);
		resultMap.put(PortalConstants.CODE, PortalConstants.CODE_SUCCESS);
		resultMap.put(PortalConstants.MSG, this.getText("请求成功"));
		resultMap.put(PortalConstants.SYSTEMTIME, DateUtil.getCurrentTime());
		return resultMap;
	}
}

package com.chunruo.portal.tag;

import java.util.List;

import com.chunruo.core.Constants;
import com.chunruo.core.model.Area;
import com.chunruo.core.util.StringUtil;
import com.chunruo.portal.PortalConstants;
import com.chunruo.portal.vo.TagModel;

/**
 * 地区列表
 * 省、市、区
 * @author chunruo
 *
 */
public class RegionListTag extends BaseTag {

	public TagModel<List<Area>> getData(Object lastTime_1){
		Long lastTime = StringUtil.nullToLong(lastTime_1);
		TagModel<List<Area>> tagModel = new TagModel<List<Area>> ();
		
		try{
			List<Area> areaTreeList = Constants.AREA_TREE_LIST;
			if(areaTreeList != null && areaTreeList.size() > 0){
				Area area = areaTreeList.get(0);
				if(area == null || StringUtil.nullToLong(area.getLastTime()) > lastTime){
					tagModel.setData(areaTreeList);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		tagModel.setCode(PortalConstants.CODE_SUCCESS);
		return tagModel;
	}
}

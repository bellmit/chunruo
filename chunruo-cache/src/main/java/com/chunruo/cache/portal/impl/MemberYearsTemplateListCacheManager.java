package com.chunruo.cache.portal.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chunruo.cache.portal.BaseCacheManagerImpl;
import com.chunruo.cache.portal.CacheObject;
import com.chunruo.core.Constants;
import com.chunruo.core.model.MemberGift;
import com.chunruo.core.model.MemberYearsTemplate;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.service.MemberGiftManager;
import com.chunruo.core.service.MemberYearsTemplateManager;
import com.chunruo.core.util.StringUtil;

@Service("memberYearsTemplateListCacheManager")
public class MemberYearsTemplateListCacheManager extends BaseCacheManagerImpl{
	@Autowired
	private MemberGiftManager memberGiftManager;
	@Autowired
	private MemberYearsTemplateManager memberYearsTemplateManager;
	
	public Map<Long,MemberYearsTemplate> getSession(){
		Map<Long,MemberYearsTemplate> templateMap = new HashMap<Long,MemberYearsTemplate>();
		List<MemberYearsTemplate> memberYearsTemplateList = this.memberYearsTemplateManager.getMemberYearsTemplateListByStatus(true);
	    if(memberYearsTemplateList != null && memberYearsTemplateList.size() > 0) {
	    	List<MemberGift> memberGiftList = this.memberGiftManager.getMemberGiftListByStatus(true);
	    	if(memberGiftList != null && memberGiftList.size() > 0) {
	    		Map<Long,List<MemberGift>> memberGiftMap = new HashMap<Long,List<MemberGift>>();
	    		for(MemberGift memberGift : memberGiftList) {
	    			Long templteId = StringUtil.nullToLong(memberGift.getTemplateId());
	    			// 图片列表
	    			List<String> imagePathList = StringUtil.strToStrList(memberGift.getDetailImagePath(), ";");
					memberGift.setDetailImagePathList(imagePathList);
					//获取商品类型
					if(StringUtil.compareObject(StringUtil.nullToInteger(memberGift.getType()), MemberGift.MEMBER_TPYE_PRODUCT)) {
						memberGift.setProductType(this.getProductType(StringUtil.nullToLong(memberGift.getWareHouseId())));
					}
					
	    			if(memberGiftMap.containsKey(templteId)) {
	    				memberGiftMap.get(templteId).add(memberGift);
	    			}else {
	    				List<MemberGift> giftList = new ArrayList<MemberGift>();
	    				giftList.add(memberGift);
	    				memberGiftMap.put(templteId, giftList);
	    			}
	    		}
	    		
	    		if(memberGiftMap != null && memberGiftMap.size() > 0) {
	    			for(MemberYearsTemplate templte : memberYearsTemplateList) {
	    				templte.setMemberGiftList(memberGiftMap.get(StringUtil.nullToLong(templte.getTemplateId())));
	    				templateMap.put(StringUtil.nullToLong(templte.getTemplateId()), templte);
	    			}
	    		}
	    	}
	    }
	    return templateMap;
	}
	
	public void removeSession() {
		//如果过期后要做特殊处理，可在此实现
		//log.info("removeSession userId:" + userId + ",userToken:" + userToken);
	}
	
	@Override
	public CacheObject run(Long nextLastTime) {
		CacheObject cacheObject = new CacheObject();
		
		MemberYearsTemplateListCacheManager memberYearsTemplateListCacheManager = Constants.ctx.getBean(MemberYearsTemplateListCacheManager.class);
		List<MemberYearsTemplate> memberYearsTemplateList = this.memberYearsTemplateManager.getMemberYearsTemplateListByUpdateTime(new Date(nextLastTime));
		int size = 0;
		Date lastUpdateTime = null;
		if(memberYearsTemplateList != null && memberYearsTemplateList.size() > 0){
			size = memberYearsTemplateList.size();
			for(MemberYearsTemplate templte : memberYearsTemplateList){
				if(lastUpdateTime == null || lastUpdateTime.before(templte.getUpdateTime())){
					lastUpdateTime = templte.getUpdateTime();
				}
			}
		}else {
			List<MemberGift> memberGiftList = this.memberGiftManager.getMemberGiftListByStatus(new Date(nextLastTime));
            if(memberGiftList != null && memberGiftList.size() > 0) {
            	for(MemberGift memberGift : memberGiftList) {
            		if(lastUpdateTime == null || lastUpdateTime.before(memberGift.getUpdateTime())){
    					lastUpdateTime = memberGift.getUpdateTime();
    				}
            	}
            }
		}
		cacheObject.setSize(size);
		
		try{
			if(lastUpdateTime != null) {
				// 更新渠道缓存列表
				memberYearsTemplateListCacheManager.removeSession();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		cacheObject.setLastUpdateTime(lastUpdateTime);
		
		return cacheObject;
	}
	
	
	/**
	 * 根据仓库查询商品类型
	 * @param warehouseId
	 * @return
	 */
	public Integer getProductType(Long warehouseId){
		try{
			if(warehouseId != null
					&& Constants.PRODUCT_WAREHOUSE_MAP != null
					&& Constants.PRODUCT_WAREHOUSE_MAP.size() > 0
					&& Constants.PRODUCT_WAREHOUSE_MAP.containsKey(warehouseId)){
				ProductWarehouse warehouse = Constants.PRODUCT_WAREHOUSE_MAP.get(warehouseId);
				if(warehouse != null && warehouse.getWarehouseId() != null){
					return StringUtil.nullToInteger(warehouse.getProductType());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}

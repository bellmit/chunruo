package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.Constants;
import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.MemberGift;
import com.chunruo.core.model.MemberYearsTemplate;
import com.chunruo.core.model.ProductWarehouse;
import com.chunruo.core.repository.MemberYearsTemplateRepository;
import com.chunruo.core.service.MemberGiftManager;
import com.chunruo.core.service.MemberYearsTemplateManager;
import com.chunruo.core.util.StringUtil;

@Component("memberYearsTemplateManager")
public class MemberYearsTemplateManagerImpl extends GenericManagerImpl<MemberYearsTemplate, Long> implements MemberYearsTemplateManager{
	private MemberYearsTemplateRepository memberYearsTemplateRepository;
	
	@Autowired
	private MemberGiftManager memberGiftManager;

	@Autowired
	public MemberYearsTemplateManagerImpl(MemberYearsTemplateRepository memberYearsTemplateRepository) {
		super(memberYearsTemplateRepository);
		this.memberYearsTemplateRepository = memberYearsTemplateRepository;
	}

	@Override
	public List<MemberYearsTemplate> getMemberYearsTemplateListByStatus(boolean status) {
		return this.memberYearsTemplateRepository.getMemberYearsTemplateListByStatus(status);
	}

	@Override
	public List<MemberYearsTemplate> getMemberYearsTemplateListByUpdateTime(Date updateTime) {
		return this.memberYearsTemplateRepository.getMemberYearsTemplateListByUpdateTime(updateTime);
	}

	@Override
	public MemberYearsTemplate getMemberYearsTemplateByTemplateId(Long templateId) {
		MemberYearsTemplate template = this.memberYearsTemplateRepository.getMemberYearsTemplateByTemplateId(templateId);
		if(template != null && template.getTemplateId() != null) {
			List<MemberGift> memberGiftList = this.memberGiftManager.getMemberGiftListByTemplateId(StringUtil.nullToLong(template.getTemplateId()));
		    if(memberGiftList != null && memberGiftList.size() > 0) {
		    	for(MemberGift memberGift : memberGiftList) {
		    		//获取商品类型
					if(StringUtil.compareObject(StringUtil.nullToInteger(memberGift.getType()), MemberGift.MEMBER_TPYE_PRODUCT)) {
						memberGift.setProductType(this.getProductType(StringUtil.nullToLong(memberGift.getWareHouseId())));
					}
		    	}
		    	template.setMemberGiftList(memberGiftList);
		    }
		}
		return template;
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

	@Override
	public List<MemberYearsTemplate> getMemberYearsTemplateListByYearsNumber(Double yearsNumber) {
		return this.memberYearsTemplateRepository.getMemberYearsTemplateListByYearsNumber(yearsNumber);
	}

}

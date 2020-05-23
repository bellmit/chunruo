package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.FxChildren;
import com.chunruo.core.model.FxPage;
import com.chunruo.core.repository.FxChildrenRepository;
import com.chunruo.core.service.FxChannelManager;
import com.chunruo.core.service.FxChildrenManager;
import com.chunruo.core.service.FxPageManager;

@Transactional
@Component("fxChildrenManager")
public class FxChildrenManagerImpl extends GenericManagerImpl<FxChildren, Long> implements FxChildrenManager {
	private FxChildrenRepository fxChildrenRepository;
	@Autowired
	private FxPageManager fxPageManager;
	@Autowired
	private FxChannelManager fxChannelManager;

	@Autowired
	public FxChildrenManagerImpl(FxChildrenRepository fxChildrenRepository) {
		super(fxChildrenRepository);
		this.fxChildrenRepository = fxChildrenRepository;
	}

	@Override
	public List<FxChildren> getFxChildrenListByPageId(Long pageId) {
		return this.fxChildrenRepository.getFxChildrenListByPageId(pageId);
	}
	
	@Override
	public void saveNewChildList(List<FxChildren> childrens, Long pageId) {
		FxPage fxPage = this.fxPageManager.get(pageId);
		if(fxPage != null && fxPage.getPageId() != null){
			//清空原来的child
			this.fxChildrenRepository.deleteFxChildrenByPageId(pageId);
			this.fxChildrenRepository.batchInsert(childrens, childrens.size());
			
			//更新频道首页更新时间
			this.fxChannelManager.updateFxChannelUpdateTimeByChannelId(fxPage.getChannelId());
		}
	}

	@Override
	public List<FxChildren> getFxChildrenListByPageIdList(List<Long> pageIdList) {
		return this.fxChildrenRepository.getFxChildrenListByPageIdList(pageIdList);
	}

	@Override
	public List<Object[]> getEffectiveFxChildrenList() {
		StringBuilder strBulSql = new StringBuilder();
		strBulSql.append("select contents,concat(jkd_fx_channel.channel_name,'-',jkd_fx_page.page_name) as 板块 ");
		strBulSql.append("from jkd_fx_children ");
		strBulSql.append("left join jkd_fx_page on jkd_fx_children.page_id = jkd_fx_page.page_id ");
		strBulSql.append("left join jkd_fx_channel on jkd_fx_page.channel_id = jkd_fx_channel.channel_id ");
		strBulSql.append("where  jkd_fx_page.is_delete = 0 and jkd_fx_channel.`status` = 1   ");
		return this.querySql(strBulSql.toString());
	}	
}

package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.RollingNotice;
import com.chunruo.core.vo.MsgModel;

public interface RollingNoticeManaager extends GenericManager<RollingNotice, Long> {

	List<RollingNotice> getRollingNotice();
	
	List<RollingNotice> getRollingNoticeListByTypeAndIsEnabled(Integer type,Integer isEnabled);

	MsgModel<Void> saveRollingNotice(Long noticeId, String content, Integer isEnabled, Integer type);

}

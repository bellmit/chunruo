package com.chunruo.core.service.impl;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.RollingNotice;
import com.chunruo.core.repository.RollingNoticeRepository;
import com.chunruo.core.service.RollingNoticeManaager;
import com.chunruo.core.vo.MsgModel;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("rollingNoticeManaager")
public class RollingNoticeManaagerImpl extends GenericManagerImpl<RollingNotice, Long> implements RollingNoticeManaager {
	private RollingNoticeRepository rollingNoticeRepository;

	@Autowired
	public RollingNoticeManaagerImpl(RollingNoticeRepository rollingNoticeRepository) {
		super(rollingNoticeRepository);
		this.rollingNoticeRepository = rollingNoticeRepository;
	}

	@Override
	public List<RollingNotice> getRollingNotice() {
		return this.rollingNoticeRepository.getRollingNotice();
	}

	@Override
	public MsgModel<Void> saveRollingNotice(Long noticeId, String content, Integer isEnabled, Integer type) {
           MsgModel<Void> msgModel = new MsgModel<Void>();
           
           if(StringUtil.isNull(content)) {
        	   msgModel.setIsSucc(false);
        	   msgModel.setMessage("内容不能为空");
        	   return msgModel;
           }
           
           if(StringUtil.nullToBoolean(isEnabled)) {
        	   //检查同一类型只能同时存在一条记录
        	   List<RollingNotice> rollingNoticeList = this.getRollingNoticeListByTypeAndIsEnabled(type, isEnabled);
               if(rollingNoticeList != null && rollingNoticeList.size() > 0) {
            	   for(RollingNotice notice : rollingNoticeList) {
            		   notice.setIsEnabled(0);
            		   notice.setUpdateTime(DateUtil.getCurrentDate());
            	   }
            	   this.batchInsert(rollingNoticeList, rollingNoticeList.size());
               }
           }
           
           RollingNotice rollingNotice = this.get(noticeId);
           if(rollingNotice == null || rollingNotice.getNoticeId() == null) {
        	   rollingNotice = new RollingNotice(); 
        	   rollingNotice.setCreateTime(DateUtil.getCurrentDate());
           }
           rollingNotice.setIsEnabled(isEnabled);
           rollingNotice.setContent(content);
           rollingNotice.setType(type);
           rollingNotice.setUpdateTime(DateUtil.getCurrentDate());
           this.save(rollingNotice);
           msgModel.setIsSucc(true);
           msgModel.setMessage("保存成功");
           return msgModel;
	}

	@Override
	public List<RollingNotice> getRollingNoticeListByTypeAndIsEnabled(Integer type, Integer isEnabled) {
		return this.rollingNoticeRepository.getRollingNoticeListByTypeAndIsEnabled(type,isEnabled);
	}
}

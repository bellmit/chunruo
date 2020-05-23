package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.VisvitCount;
import com.chunruo.core.repository.VisvitCountRepository;
import com.chunruo.core.service.VisvitCountManager;
import com.chunruo.core.util.StringUtil;

@Transactional
@Component("visvitCountManager")
public class VisvitCountManagerImpl extends GenericManagerImpl<VisvitCount, Long> implements VisvitCountManager{
    private VisvitCountRepository visvitCountRepository;
	
	@Autowired
	public VisvitCountManagerImpl(VisvitCountRepository visvitCountRepository) {
		super(visvitCountRepository);
		this.visvitCountRepository = visvitCountRepository;
	}

	@Override
	public Set<Long> getVisvitCountUserId() {
		Set<Long> userIdList = new HashSet<Long>();
		String sql = "select user_id from jkd_visvit_count";
        List<Object[]> objectList = this.querySql(sql);
        if(objectList != null && objectList.size() > 0) {
        	for(int i=0;i<objectList.size();i++) {
        		userIdList.add(StringUtil.nullToLong(objectList.get(i)));
        	}
        }
		return userIdList;
	}

	@Override
	public List<VisvitCount> getVisvitCountListByUpdateTime(Date updateTime) {
		return this.visvitCountRepository.getVisvitCountListByUpdateTime(updateTime);
	}
}

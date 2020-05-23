package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.NoviceGuide;
import com.chunruo.core.repository.NoviceGuideRepository;
import com.chunruo.core.service.NoviceGuideManager;

@Transactional
@Component("noviceGuideManager")
public class NoviceGuideManagerImpl extends GenericManagerImpl<NoviceGuide, Long> implements NoviceGuideManager {
	private NoviceGuideRepository noviceGuideRepository;

	@Autowired
	public NoviceGuideManagerImpl(NoviceGuideRepository noviceGuideRepository) {
		super(noviceGuideRepository);
		this.noviceGuideRepository = noviceGuideRepository;
	}

	@Override
	public NoviceGuide getNoviceGuideByPhoneTypeAndHeightWidth(Integer phoneType, Integer height,Integer width) {
		return this.noviceGuideRepository.getNoviceGuideByPhoneTypeAndHeightWidth(phoneType,height,width);
	}

	@Override
	public List<NoviceGuide> getNoviceGuideListByStatus(Boolean status) {
		return this.noviceGuideRepository.getNoviceGuideListByStatus(status);
	}

	@Override
	public List<NoviceGuide> getNoviceGuideListByUpdateTime(Date updateTime) {
		return this.noviceGuideRepository.getNoviceGuideListByUpdateTime(updateTime);
	}

}

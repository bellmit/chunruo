package com.chunruo.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.MemberGift;
import com.chunruo.core.repository.MemberGiftRepository;
import com.chunruo.core.service.MemberGiftManager;

@Transactional
@Component("memberGiftManager")
public class MemberGiftManagerImpl extends GenericManagerImpl<MemberGift, Long> implements MemberGiftManager{
	private MemberGiftRepository memberGiftRepository;

	@Autowired
	public MemberGiftManagerImpl(MemberGiftRepository memberGiftRepository) {
		super(memberGiftRepository);
		this.memberGiftRepository = memberGiftRepository;
	}

	@Override
	public List<MemberGift> getMemberGiftListByStatus(boolean status) {
		return this.memberGiftRepository.getMemberGiftListByStatus(status);
	}

	@Override
	public List<MemberGift> getMemberGiftListByStatus(Date updateTime) {
		return this.memberGiftRepository.getMemberGiftListByStatus(updateTime);
	}

	@Override
	public List<MemberGift> getMemberGiftListByTemplateId(Long templateId) {
		return this.memberGiftRepository.getMemberGiftListByTemplateId(templateId);
	}

	
}

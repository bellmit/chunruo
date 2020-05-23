package com.chunruo.core.service;

import java.util.Date;
import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.MemberGift;

public interface MemberGiftManager extends GenericManager<MemberGift, Long> {

	public List<MemberGift> getMemberGiftListByStatus(boolean status);

	public List<MemberGift> getMemberGiftListByStatus(Date updateTime);

	public List<MemberGift> getMemberGiftListByTemplateId(Long templateId);

}

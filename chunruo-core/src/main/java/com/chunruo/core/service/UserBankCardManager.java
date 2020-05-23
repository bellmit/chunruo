package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserBankCard;

public interface UserBankCardManager extends GenericManager<UserBankCard, Long>{

	public List<UserBankCard> getListByUserId(Long userId);
	
	public void updateDefaultBankCard(Long bankCardId);
	
	public UserBankCard saveBankCard(UserBankCard userBankCard);
	
	public UserBankCard getByUserIdAndBankCardNo(Long userId, String bankCardNo);
}

package com.chunruo.core.service;

import java.util.List;

import com.chunruo.core.base.GenericManager;
import com.chunruo.core.model.UserQuickCard;

public interface UserQuickCardManager extends GenericManager<UserQuickCard, Long>{
	
	public UserQuickCard saveUserQuickCard(UserQuickCard userQuickCard);

	public List<UserQuickCard> getUserQuickCardListByUserId(Long userId);
	
	public UserQuickCard getUserQuickCardListByUserId(Long userId, String bankCardNumber);

	public boolean isExistUserQuickCardListByUserId(Long userId, String bankCardNumber);
	
}

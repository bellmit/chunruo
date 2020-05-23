package com.chunruo.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserBankCard;
import com.chunruo.core.repository.UserBankCardRepository;
import com.chunruo.core.service.UserBankCardManager;
import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

@Component("userBankCardManager")
public class UserBankCardManagerImpl extends GenericManagerImpl<UserBankCard, Long> implements UserBankCardManager{
	private UserBankCardRepository userBankCardRepository;
	
	@Autowired
	public UserBankCardManagerImpl(UserBankCardRepository userBankCardRepository) {
		super(userBankCardRepository);
		this.userBankCardRepository = userBankCardRepository;
	}

	@Override
	public List<UserBankCard> getListByUserId(Long userId) {
		return this.userBankCardRepository.getListByUserId(userId);
	}

	@Override
	public void updateDefaultBankCard(Long bankCardId) {
		UserBankCard defaultBankCard = this.get(bankCardId);
		if(defaultBankCard != null 
				&& defaultBankCard.getBankCardId() != null
				&& !StringUtil.nullToBoolean(defaultBankCard.getIsDefault())) {
			List<UserBankCard> list = this.getListByUserId(defaultBankCard.getUserId());
			if(list != null && list.size() > 0) {
				for(UserBankCard userBankCard : list) {
					userBankCard.setIsDefault(false);
					userBankCard.setUpdateTime(DateUtil.getCurrentDate());
					if(StringUtil.compareObject(bankCardId, userBankCard.getBankCardId())) {
						userBankCard.setIsDefault(true);
					}
				}
				this.batchInsert(list, list.size());
			}
		}
	}
	
	@Override
	public UserBankCard saveBankCard(UserBankCard userBankCard) {
		userBankCard = this.save(userBankCard);
		List<UserBankCard> list = this.getListByUserId(userBankCard.getUserId());
		if(list == null || list.size() <= 0) {
			userBankCard.setIsDefault(true);
			userBankCard.setUpdateTime(DateUtil.getCurrentDate());
			userBankCard = this.update(userBankCard);
		}
		return userBankCard;
	}

	@Override
	public UserBankCard getByUserIdAndBankCardNo(Long userId, String bankCardNo) {
		return this.userBankCardRepository.getByUserIdAndBankCardNo(userId, bankCardNo);
	}

}

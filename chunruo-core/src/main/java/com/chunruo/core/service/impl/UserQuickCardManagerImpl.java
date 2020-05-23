package com.chunruo.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.chunruo.core.base.GenericManagerImpl;
import com.chunruo.core.model.UserQuickCard;
import com.chunruo.core.repository.UserQuickCardRepository;
import com.chunruo.core.service.UserQuickCardManager;
import com.chunruo.core.util.DateUtil;

@Transactional
@Component("userQuickCardManager")
public class UserQuickCardManagerImpl extends GenericManagerImpl<UserQuickCard, Long> implements UserQuickCardManager{
	private UserQuickCardRepository userQuickCardRepository;
	
	@Autowired
	public UserQuickCardManagerImpl(UserQuickCardRepository userQuickCardRepository) {
		super(userQuickCardRepository);
		this.userQuickCardRepository = userQuickCardRepository;
	}
	
	@Override
	public List<UserQuickCard> getUserQuickCardListByUserId(Long userId) {
		return this.userQuickCardRepository.getUserQuickCardListByUserId(userId);
	}

	@Override
	public UserQuickCard getUserQuickCardListByUserId(Long userId, String bankCardNumber) {
		List<UserQuickCard> list = this.userQuickCardRepository.getUserQuickCardListByUserId(userId, bankCardNumber);
		return (list != null && list.size() > 0) ? list.get(0) : null;
	}

	@Override
	public boolean isExistUserQuickCardListByUserId(Long userId, String bankCardNumber) {
		List<UserQuickCard> list = this.userQuickCardRepository.getUserQuickCardListByUserId(userId, bankCardNumber);
		if(list != null && list.size() > 0){
			return true;
		}
		return false;
	}
	
	@Override
	public UserQuickCard saveUserQuickCard(UserQuickCard userQuickCard){
		List<UserQuickCard> list = this.userQuickCardRepository.getUserQuickCardListByBankCardNumber(userQuickCard.getBankCardNumber());
		if(list != null && list.size() > 0){
			List<Long> idList = new ArrayList<Long> ();
			for(UserQuickCard quickCard : list){
				idList.add(quickCard.getQuickCardId());
			}
			this.deleteByIdList(idList);
		}
		
		userQuickCard.setUpdateTime(DateUtil.getCurrentDate());
		return this.save(userQuickCard);
	}

}
